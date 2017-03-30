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
import java.sql.SQLException;

import org.h2.tools.Server;

import com.google.gson.Gson;

import de.infinit.emp.controller.Controller;
import de.infinit.emp.controller.PartnerController;
import de.infinit.emp.controller.SensorController;
import de.infinit.emp.controller.SessionController;
import de.infinit.emp.controller.SignupController;
import de.infinit.emp.controller.UploadController;
import de.infinit.emp.controller.UserController;
import de.infinit.emp.filter.AuthenticationFilter;
import de.infinit.emp.filter.LoggingFilter;

public class Application {
	static final Gson gson = new Gson();

	public static void main(String[] args) throws IOException, SQLException {
		Server server = Server.createTcpServer().start();

		before(LoggingFilter::logRequest);
		before(AuthenticationFilter::authenticateRequest);

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
				post("", UserController::updateUser, gson::toJson); // update user
				get("/invitation", UserController::getInvitationCodes, gson::toJson); // get user's invitations
				post("/invitation", UserController::inviteUser, gson::toJson); // invite user
				post("/link", UserController::acceptInvitation, gson::toJson); // accept invitation
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
				get("/:uuid/data", Controller::notImplemented, gson::toJson); // get sensor historic data
				get("/:uuid/event", Controller::notImplemented, gson::toJson); // subscribe sensor events
				delete("/:uuid/event", Controller::notImplemented, gson::toJson); // un-subscribe sensor events
				post("/:uuid/action", Controller::notImplemented, gson::toJson); // sensor action
			});
			path("/event", () -> {
				get("", Controller::notImplemented, gson::toJson); // get events
			});
		});

		path("/upload", () -> {
			get("", UploadController::provideUploadForm);
			post("", UploadController::uploadFile);
		});

		notFound(Controller::notFound);
		exception(Exception.class, (exception, request, response) -> {
			Controller.notFound(request, response);
		});

		after(LoggingFilter::logResponse);

		// Database.getConnectionSource().close();
		server.stop();
	}
}
