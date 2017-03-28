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

public class AppMain {
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
				get("", sessionController::requestNonAuthorizedSession, Global.GSON::toJson); // get non-authorized session
				post("", sessionController::loginToPartnerOrProxySession, Global.GSON::toJson); // authorize partner/proxy session (login)
				delete("", sessionController::logoutFromSession, Global.GSON::toJson); // logout
			});
			path("/partner", () -> {
				get("/user", partnerController::getPartnerRelatedUsers, Global.GSON::toJson); // lists all partner related users
				get("/user/:uuid", partnerController::getUserInformation, Global.GSON::toJson); // get data of user ':uuid'
				post("/user/:uuid", Controller::notImplemented, Global.GSON::toJson); // delete user
			});
			path("/signup", () -> {
				post("/verification", signupController::reserveUserAccount, Global.GSON::toJson); // partner-controlled sign-up: pre-reserve user account
				post("/user", signupController::addUserAccount, Global.GSON::toJson); // complete sign-up: create new user account
			});
			path("/user", () -> {
				get("", Controller::notImplemented, Global.GSON::toJson); // get user
				post("", Controller::notImplemented, Global.GSON::toJson); // update user
				get("/invitation", Controller::notImplemented, Global.GSON::toJson); // get invitation code
				post("/invitation", Controller::notImplemented, Global.GSON::toJson); // invite user
				post("/link", Controller::notImplemented, Global.GSON::toJson); // accept invitation
			});
			path("/tag", () -> {
				get("", Controller::notImplemented, Global.GSON::toJson); // get user tags
				get("/:uuid", Controller::notImplemented, Global.GSON::toJson); // get data of tag ':uuid'
				post("", Controller::notImplemented, Global.GSON::toJson); // add tag
				delete("/:uuid", Controller::notImplemented, Global.GSON::toJson); // delete tag
				post("/:uuid", Controller::notImplemented, Global.GSON::toJson); // add/delete user to/from tag
			});
			path("/object", () -> {
				post("/:uuid/tag", Controller::notImplemented, Global.GSON::toJson); // attach tag to object
			});
			path("/sensor", () -> {
				post("", sensorController::post, Global.GSON::toJson); // add sensor
				get("/:uuid", sensorController::get, Global.GSON::toJson); // get sensor
				post("/:uuid", Controller::notImplemented, Global.GSON::toJson); // update sensor
				delete(":uuid", sensorController::delete, Global.GSON::toJson); // delete sensor
				get("/:uuid/data", Controller::notImplemented, Global.GSON::toJson); // get sensor historic data
				get("/:uuid/event", Controller::notImplemented, Global.GSON::toJson); // subscribe sensor events
				delete("/:uuid/event", Controller::notImplemented, Global.GSON::toJson); // unsubscribe sensor events
				post("/:uuid/action", Controller::notImplemented, Global.GSON::toJson); // sensor action
			});
			path("/event", () -> {
				get("", Controller::notImplemented, Global.GSON::toJson); // get events
			});
		});

		// Database.getConnectionSource().close();
		server.stop();
	}
}
