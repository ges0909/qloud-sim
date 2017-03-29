package de.infinit.emp;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;

import java.io.IOException;
import java.sql.SQLException;

import org.h2.tools.Server;

import com.google.gson.Gson;

import de.infinit.emp.controller.AuthenticationFilter;
import de.infinit.emp.controller.Controller;
import de.infinit.emp.controller.LoggingFilter;
import de.infinit.emp.controller.PartnerController;
import de.infinit.emp.controller.SensorController;
import de.infinit.emp.controller.SessionController;
import de.infinit.emp.controller.SignupController;

public class AppMain {
	static final Gson gson = new Gson();
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
				get("", sessionController::requestNonAuthorizedSession, gson::toJson); // get non-authorized session
				post("", sessionController::loginToPartnerOrProxySession, gson::toJson); // authorize partner/proxy session (login)
				delete("", sessionController::logoutFromSession, gson::toJson); // logout
			});
			path("/partner", () -> {
				get("/user", partnerController::getPartnerRelatedUsers, gson::toJson); // lists all partner related users
				get("/user/:uuid", partnerController::getUserInformation, gson::toJson); // get data of user ':uuid'
				post("/user/:uuid", Controller::notImplemented, gson::toJson); // delete user
			});
			path("/signup", () -> {
				post("/verification", signupController::reserveUserAccount, gson::toJson); // partner-controlled sign-up: pre-reserve user account
				post("/user", signupController::addUserAccount, gson::toJson); // complete sign-up: create new user account
			});
			path("/user", () -> {
				get("", Controller::notImplemented, gson::toJson); // get user
				post("", Controller::notImplemented, gson::toJson); // update user
				get("/invitation", Controller::notImplemented, gson::toJson); // get invitation code
				post("/invitation", Controller::notImplemented, gson::toJson); // invite user
				post("/link", Controller::notImplemented, gson::toJson); // accept invitation
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
				post("", sensorController::post, gson::toJson); // add sensor
				get("/:uuid", sensorController::get, gson::toJson); // get sensor
				post("/:uuid", Controller::notImplemented, gson::toJson); // update sensor
				delete(":uuid", sensorController::delete, gson::toJson); // delete sensor
				get("/:uuid/data", Controller::notImplemented, gson::toJson); // get sensor historic data
				get("/:uuid/event", Controller::notImplemented, gson::toJson); // subscribe sensor events
				delete("/:uuid/event", Controller::notImplemented, gson::toJson); // unsubscribe sensor events
				post("/:uuid/action", Controller::notImplemented, gson::toJson); // sensor action
			});
			path("/event", () -> {
				get("", Controller::notImplemented, gson::toJson); // get events
			});
		});

		// Database.getConnectionSource().close();
		server.stop();
	}
}
