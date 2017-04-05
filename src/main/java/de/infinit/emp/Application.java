package de.infinit.emp;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.path;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

import java.io.IOException;
import java.sql.SQLException;

import com.google.gson.Gson;

import de.infinit.emp.admin.controller.ConfigController;
import de.infinit.emp.admin.controller.UploadController;
import de.infinit.emp.api.controller.Controller;
import de.infinit.emp.api.controller.PartnerController;
import de.infinit.emp.api.controller.SensorController;
import de.infinit.emp.api.controller.SessionController;
import de.infinit.emp.api.controller.SignupController;
import de.infinit.emp.api.controller.TagController;
import de.infinit.emp.api.controller.UserController;
import de.infinit.emp.filter.AuthenticationFilter;
import de.infinit.emp.filter.LoggingFilter;
import spark.template.freemarker.FreeMarkerEngine;

public class Application {
	static final Gson gson = new Gson();
	static final FreeMarkerEngine fmTransformer = new FreeMarkerEngine(new FreeMarkerConfig());

	public static void main(String[] args) throws IOException, SQLException {
		// Server server;
		staticFileLocation("/public"); // to serve css, js, ...
		// server = Server.createTcpServer().start();

		before(AuthenticationFilter::authenticateRequest);
		before(LoggingFilter::logRequest);

		apiEndpoints();
		adminEndpoints();

		notFound(Controller.instance()::notFound);
		// exception(Exception.class, (exception, request, response) ->
		// Controller.instance()::except);

		after(LoggingFilter::logResponse);

		// Persistence.close();
		// server.stop();
	}

	static void apiEndpoints() {
		path("/api", () -> {
			path("/session", () -> {
				get("", SessionController.instance()::requestNonAuthorizedSession, gson::toJson);
				post("", SessionController.instance()::loginToPartnerOrProxySession, gson::toJson);
				delete("", SessionController.instance()::logoutFromSession, gson::toJson);
			});
			path("/partner", () -> path("/user", () -> {
				get("", PartnerController.instance()::getAccounts, gson::toJson);
				get("/:uuid", PartnerController.instance()::getAccount, gson::toJson);
				post("/:uuid", PartnerController.instance()::deleteAccount, gson::toJson);
			}));
			path("/signup", () -> {
				post("/verification", SignupController.instance()::reserveAccount, gson::toJson);
				post("/user", SignupController.instance()::addAccount, gson::toJson);
			});
			path("/user", () -> {
				get("", UserController.instance()::getUser, gson::toJson);
				post("", UserController.instance()::updateUser, gson::toJson);
				get("/invitation", UserController.instance()::getUserInvitations, gson::toJson);
				post("/invitation", UserController.instance()::inviteUser, gson::toJson);
				post("/link", UserController.instance()::acceptInvitation, gson::toJson);
			});
			path("/tag", () -> {
				get("", TagController.instance()::getTags, gson::toJson);
				get("/:uuid", TagController.instance()::getTag, gson::toJson);
				post("", TagController.instance()::createTag, gson::toJson);
				delete("/:uuid", TagController.instance()::deleteTag, gson::toJson);
				post("/:uuid", TagController.instance()::updateTag, gson::toJson);
				get("/:uuid/object", TagController.instance()::getTaggedObjects, gson::toJson);
			});
			path("/object", () -> post("/:uuid/tag", Controller.instance()::notImplemented, gson::toJson));
			path("/sensor", () -> {
				post("", SensorController.instance()::createSensor, gson::toJson);
				get("/:uuid", SensorController.instance()::getSensor, gson::toJson);
				post("/:uuid", SensorController.instance()::updateSensor, gson::toJson);
				delete("/:uuid", SensorController.instance()::deleteSensor, gson::toJson);
				get("/:uuid/data", SensorController.instance()::getSensorData, gson::toJson);
				get("/:uuid/event", SensorController.instance()::susbcribeSensorForEvents, gson::toJson);
				delete("/:uuid/event", SensorController.instance()::cancelSensorEventSubcription, gson::toJson);
				post("/:uuid/action", Controller.instance()::notImplemented, gson::toJson);
			});
			path("/event", () -> get("", Controller.instance()::notImplemented, gson::toJson));
			after((request, response) -> response.type("application/json"));
		});
	}

	static void adminEndpoints() {
		path("/config", () -> {
			get("", ConfigController.instance()::displayConfigurationDialog, fmTransformer);
			post("", ConfigController.instance()::saveConfiguration, fmTransformer);
			after((request, response) -> response.type("text/html"));
		});
		path("/upload", () -> {
			get("", UploadController.instance()::displayUploadForm, fmTransformer);
			post("", UploadController.instance()::uploadFile, fmTransformer);
			after((request, response) -> response.type("text/html"));
		});
	}
}
