package de.infinit.emp;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;
import static spark.Spark.notFound;

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

	public static void main(String[] args) {
//		Server server = Server.createTcpServer().start();

		before(AuthenticationFilter::authenticateRequest);
		before(LoggingFilter::logRequest);
		
		path("/api", () -> {
			path("/session", () -> {
				get("", SessionController::requestNonAuthorizedSession, gson::toJson);
				post("", SessionController::loginToPartnerOrProxySession, gson::toJson);
				delete("", SessionController::logoutFromSession, gson::toJson);
			});
			path("/partner", () -> {
				path("/user", () -> {
					get("", PartnerController::getUsers, gson::toJson);
					get("/:uuid", PartnerController::getUserData, gson::toJson);
					post("/:uuid", PartnerController::deleteUser, gson::toJson);
				});
			});
			path("/signup", () -> {
				post("/verification", SignupController::reserveUserAccount, gson::toJson);
				post("/user", SignupController::addUserAccount, gson::toJson);
			});
			path("/user", () -> {
				get("", UserController::getUser, gson::toJson);
				post("", UserController::updateUser, gson::toJson);
				get("/invitation", UserController::getUserInvitations, gson::toJson);
				post("/invitation", UserController::inviteUser, gson::toJson);
				post("/link", UserController::acceptInvitation, gson::toJson);
			});
			path("/tag", () -> {
				get("", Controller::notImplemented, gson::toJson);
				get("/:uuid", Controller::notImplemented, gson::toJson);
				post("", Controller::notImplemented, gson::toJson);
				delete("/:uuid", Controller::notImplemented, gson::toJson);
				post("/:uuid", Controller::notImplemented, gson::toJson);
			});
			path("/object", () -> {
				post("/:uuid/tag", Controller::notImplemented, gson::toJson);
			});
			path("/sensor", () -> {
				post("", SensorController::addSensor, gson::toJson);
				get("/:uuid", SensorController::getSensor, gson::toJson);
				post("/:uuid", SensorController::updateSensor, gson::toJson);
				delete("/:uuid", SensorController::deleteSensor, gson::toJson);
				get("/:uuid/data", SensorController::getSensorData, gson::toJson);
				get("/:uuid/event", SensorController::susbcribeSensorForEvents, gson::toJson);
				delete("/:uuid/event", SensorController::cancelSensorEventSubcription, gson::toJson);
				post("/:uuid/action", Controller::notImplemented, gson::toJson);
			});
			path("/event", () -> {
				get("", Controller::notImplemented, gson::toJson); 
			});
			after((request, response) -> { response.type("application/json"); });
		});
		
		path("/upload", () -> {
			get("", UploadController::provideUploadForm);
			post("", UploadController::uploadFile);
			after((request, response) -> { response.type("text/html"); });
		});
		
		notFound(Controller::notFound);
		
		after(LoggingFilter::logResponse);

//		exception(Exception.class, (exception, request, response) -> {
//			Controller.notFound(request, response);
//		});

//		Database.getConnectionSource().close();
//		server.stop();
	}
}
