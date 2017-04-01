package de.infinit.emp;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.path;
import static spark.Spark.post;

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
import freemarker.template.Configuration;
import freemarker.template.Version;
import spark.template.freemarker.FreeMarkerEngine;

public class Application {
	static final Gson gson = new Gson();
	static FreeMarkerEngine freeMarkerEngine;

	public static void main(String[] args) {
		Configuration configuration = new Configuration(new Version(2, 3, 23));
		configuration.setClassForTemplateLoading(Application.class, "/templates");
		freeMarkerEngine = new FreeMarkerEngine(configuration);

		// Server server = Server.createTcpServer().start();

		before(AuthenticationFilter::authenticateRequest);
		before(LoggingFilter::logRequest);

		apiEndpoints();
		uploadEndpoints();

		notFound(Controller.instance()::notFound);
		after(LoggingFilter::logResponse);

		// exception(Exception.class, (exception, request, response) -> {
		// Controller.instance()::notFound });

		// Persistence.getConnectionSource().close();
		// server.stop();
	}

	static void apiEndpoints() {
		path("/api", () -> {
			path("/session", () -> {
				get("", SessionController.instance()::requestNonAuthorizedSession, gson::toJson);
				post("", SessionController.instance()::loginToPartnerOrProxySession, gson::toJson);
				delete("", SessionController.instance()::logoutFromSession, gson::toJson);
			});
			path("/partner", () -> {
				path("/user", () -> {
					get("", PartnerController.instance()::getUsers, gson::toJson);
					get("/:uuid", PartnerController.instance()::getUserData, gson::toJson);
					post("/:uuid", PartnerController.instance()::deleteUser, gson::toJson);
				});
			});
			path("/signup", () -> {
				post("/verification", SignupController.instance()::reserveUserAccount, gson::toJson);
				post("/user", SignupController.instance()::addUserAccount, gson::toJson);
			});
			path("/user", () -> {
				get("", UserController.instance()::getUser, gson::toJson);
				post("", UserController.instance()::updateUser, gson::toJson);
				get("/invitation", UserController.instance()::getUserInvitations, gson::toJson);
				post("/invitation", UserController.instance()::inviteUser, gson::toJson);
				post("/link", UserController.instance()::acceptInvitation, gson::toJson);
			});
			path("/tag", () -> {
				get("", Controller.instance()::notImplemented, gson::toJson);
				get("/:uuid", Controller.instance()::notImplemented, gson::toJson);
				post("", Controller.instance()::notImplemented, gson::toJson);
				delete("/:uuid", Controller.instance()::notImplemented, gson::toJson);
				post("/:uuid", Controller.instance()::notImplemented, gson::toJson);
			});
			path("/object", () -> {
				post("/:uuid/tag", Controller.instance()::notImplemented, gson::toJson);
			});
			path("/sensor", () -> {
				post("", SensorController.instance()::addSensor, gson::toJson);
				get("/:uuid", SensorController.instance()::getSensor, gson::toJson);
				post("/:uuid", SensorController.instance()::updateSensor, gson::toJson);
				delete("/:uuid", SensorController.instance()::deleteSensor, gson::toJson);
				get("/:uuid/data", SensorController.instance()::getSensorData, gson::toJson);
				get("/:uuid/event", SensorController.instance()::susbcribeSensorForEvents, gson::toJson);
				delete("/:uuid/event", SensorController.instance()::cancelSensorEventSubcription, gson::toJson);
				post("/:uuid/action", Controller.instance()::notImplemented, gson::toJson);
			});
			path("/event", () -> {
				get("", Controller.instance()::notImplemented, gson::toJson);
			});
			after((request, response) -> {
				response.type("application/json");
			});
		});
	}

	static void uploadEndpoints() {
		path("/upload", () -> {
			get("", UploadController.instance()::provideUploadForm, freeMarkerEngine);
			post("", UploadController.instance()::uploadFile, freeMarkerEngine);
			after((request, response) -> {
				response.type("text/html");
			});
		});
	}
}
