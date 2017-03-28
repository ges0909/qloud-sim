package de.infinit.emp;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;

import java.io.IOException;
import java.sql.SQLException;

import org.h2.tools.Server;

import de.infinit.emp.controller.AuthenticationFilter;
import de.infinit.emp.controller.Controller;
import de.infinit.emp.controller.LoggingFilter;
import de.infinit.emp.controller.PartnerController;
import de.infinit.emp.controller.SensorController;
import de.infinit.emp.controller.SessionController;
import de.infinit.emp.controller.SignupController;

public class Main {
	public static void main(String[] args) throws IOException, SQLException {
		Server server = Server.createTcpServer().start();

		new LoggingFilter();
		new AuthenticationFilter();

		SessionController sessionController = new SessionController();
		PartnerController partnerController = new PartnerController();
		SignupController signupController = new SignupController();
		SensorController sensorController = new SensorController();

		path("/api", () -> {
			path("/session", () -> {
				get("", sessionController::getUnauthorizedSession, Globals.GSON::toJson); // get non-authorized session
				post("", sessionController::partnerOrProxySessionLogin, Globals.GSON::toJson); // authorize partner/proxy session (login)
				delete("", sessionController::sessionLogout, Globals.GSON::toJson); // logout
			});
			path("/partner", () -> {
				get("/user", partnerController::getPartnerRelatedUsers, Globals.GSON::toJson); // lists all partner related users
				get("/user/:uuid", Controller::notImplemented, Globals.GSON::toJson); // get data of user ':uuid'
				post("/user/:uuid", Controller::notImplemented, Globals.GSON::toJson); // delete user
			});
			path("/signup", () -> {
				post("/verification", signupController::initiateSignup, Globals.GSON::toJson); // partner-controlled sign-up: pre-reserve user account
				post("/user", signupController::completeSignup, Globals.GSON::toJson); // complete sign-up: create new user account
			});
			path("/user", () -> {
				get("", Controller::notImplemented, Globals.GSON::toJson); // get user
				post("", Controller::notImplemented, Globals.GSON::toJson); // update user
				get("/invitation", Controller::notImplemented, Globals.GSON::toJson); // get invitation code
				post("/invitation", Controller::notImplemented, Globals.GSON::toJson); // invite user
				post("/link", Controller::notImplemented, Globals.GSON::toJson); // accept invitation
			});
			path("/tag", () -> {
				get("", Controller::notImplemented, Globals.GSON::toJson); // get user tags
				get("/:uuid", Controller::notImplemented, Globals.GSON::toJson); // get data of tag ':uuid'
				post("", Controller::notImplemented, Globals.GSON::toJson); // add tag
				delete("/:uuid", Controller::notImplemented, Globals.GSON::toJson); // delete tag
				post("/:uuid", Controller::notImplemented, Globals.GSON::toJson); // add/delete user to/from tag
			});
			path("/object", () -> {
				post("/:uuid/tag", Controller::notImplemented, Globals.GSON::toJson); // attach tag to object
			});
			path("/sensor", () -> {
				post("", sensorController::post, Globals.GSON::toJson); // add sensor
				get("/:uuid", sensorController::get, Globals.GSON::toJson); // get sensor
				post("/:uuid", Controller::notImplemented, Globals.GSON::toJson); // update sensor
				delete(":uuid", sensorController::delete, Globals.GSON::toJson); // delete sensor
				get("/:uuid/data", Controller::notImplemented, Globals.GSON::toJson); // get sensor historic data
				get("/:uuid/event", Controller::notImplemented, Globals.GSON::toJson); // subscribe sensor events
				delete("/:uuid/event", Controller::notImplemented, Globals.GSON::toJson); // unsubscribe sensor events
				post("/:uuid/action", Controller::notImplemented, Globals.GSON::toJson); // sensor action
			});
			path("/event", () -> {
				get("", Controller::notImplemented, Globals.GSON::toJson); // get events
			});
		});

		// Database.getConnectionSource().close();
		server.stop();
	}
}
