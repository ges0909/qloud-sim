package de.infinit.emp;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.path;
import static spark.Spark.post;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.h2.tools.Server;

import com.google.gson.Gson;

import de.infinit.emp.controller.Controller;
import de.infinit.emp.controller.PartnerController;
import de.infinit.emp.controller.SensorController;
import de.infinit.emp.controller.SessionController;
import de.infinit.emp.controller.SignupController;
import de.infinit.emp.controller.UserController;
import de.infinit.emp.filter.Filter;

public class Application {
	static final Gson gson = new Gson();

	public static void main(String[] args) throws IOException, SQLException {
		Server server = Server.createTcpServer().start();

		before(Filter::authenticateRequest);
		before(Filter::logRequest);

		path("/api", () -> {
			path("/session", () -> {
				get("", SessionController::requestNonAuthorizedSession, gson::toJson); // get non-authorized session
				post("", SessionController::loginToPartnerOrProxySession, gson::toJson); // login to partner/proxy session
				delete("", SessionController::logoutFromSession, gson::toJson); // logout
			});
			path("/partner", () -> {
				get("/user", PartnerController::getUsers, gson::toJson); // lists all partner related users
				get("/user/:uuid", PartnerController::getUserData, gson::toJson); // partner related user: get user data
				post("/user/:uuid", PartnerController::deleteUser, gson::toJson); // partner related user: delete user
			});
			path("/signup", () -> {
				post("/verification", SignupController::reserveUserAccount, gson::toJson); // reserve user account
				post("/user", SignupController::addUserAccount, gson::toJson); // create user account
			});
			path("/user", () -> {
				get("", UserController::getUser, gson::toJson); // get user
				post("", Controller::notImplemented, gson::toJson); // update user
				get("/invitation", Controller::notImplemented, gson::toJson); // get invitation code
				post("/invitation", Controller::notImplemented, gson::toJson); // invite user
				post("/link", Controller::notImplemented, gson::toJson); // accept
																			// invitation
			});
			path("/tag", () -> {
				get("", Controller::notImplemented, gson::toJson); // get user tags
				get("/:uuid", Controller::notImplemented, gson::toJson); // get data of tag ':uuid'
				post("", Controller::notImplemented, gson::toJson); // add tag
				delete("/:uuid", Controller::notImplemented, gson::toJson); // delete tag
				post("/:uuid", Controller::notImplemented, gson::toJson); // add/delete user to/from tag
			});
			path("/object", () -> {
				post("/:uuid/tag", Controller::notImplemented, gson::toJson); // attach tag to object
			});
			path("/sensor", () -> {
				post("", SensorController::addSensor, gson::toJson); // add sensor
				get("/:uuid", SensorController::getSensor, gson::toJson); // get sensor
				post("/:uuid", Controller::notImplemented, gson::toJson); // update sensor
				delete("/:uuid", SensorController::deleteSensor, gson::toJson); // delete sensor
				get("/:uuid/data", Controller::notImplemented, gson::toJson); // getsensor historicdata
				get("/:uuid/event", Controller::notImplemented, gson::toJson); // subscribe sensor events
				delete("/:uuid/event", Controller::notImplemented, gson::toJson); // unsubscribe sensor events
				post("/:uuid/action", Controller::notImplemented, gson::toJson); // sensor action
			});
			path("/event", () -> {
				get("", Controller::notImplemented, gson::toJson); // get events
			});
		});

		get("/upload", (request, response) ->
			"<form method='post' enctype='multipart/form-data'>"
		  + "    <input type='file' name='upload' accept='*'>"
		  + "    <button>Upload</button>"
		  + "</form>");
		post("/upload", (request, response) -> {
			MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
			request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
			Part uploadedFile = request.raw().getPart("uploadedFile");
			Path path = Paths.get("/tmp/meh");
			try (InputStream in = uploadedFile.getInputStream()) {
				Files.copy(in, path);
			}
			response.redirect("/");
			return "OK";
		});

		notFound(Controller::notFound);
		exception(Exception.class, (exception, request, response) -> {
			Controller.notFound(request, response);
		});

		after(Filter::logResponse);

		// Database.getConnectionSource().close();
		server.stop();
	}
}
