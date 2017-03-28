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

public class Main {
	static final Gson GSON = new Gson();

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
				get("", sessionController::get, GSON::toJson); // get non-authorized session
				post("", sessionController::post, GSON::toJson); // authorize partner/proxy session (login)
				delete("", sessionController::delete, GSON::toJson); // logout
			});
			path("/partner", () -> {
				get("/user", partnerController::getUsers, GSON::toJson); // get users managed by partner
				get("/user/:uuid", Controller::notImplemented, GSON::toJson); // get data of user ':uuid'
				post("/user/:uuid", Controller::notImplemented, GSON::toJson); // delete user
			});
			path("/signup", () -> {
				post("/verification", signupController::initiateSignup, GSON::toJson); // partner-controlled sign-up: pre-reserve user account
				post("/user", signupController::completeSignup, GSON::toJson); // complete sign-up: create new user account
			});
			path("/user", () -> {
				get("", Controller::notImplemented, GSON::toJson); // get user
				post("", Controller::notImplemented, GSON::toJson); // update user
				get("/invitation", Controller::notImplemented, GSON::toJson); // get invitation code
				post("/invitation", Controller::notImplemented, GSON::toJson); // invite user
				post("/link", Controller::notImplemented, GSON::toJson); // accept invitation
			});
			path("/tag", () -> {
				get("", Controller::notImplemented, GSON::toJson); // get user tags
				get("/:uuid", Controller::notImplemented, GSON::toJson); // get data of tag ':uuid'
				post("", Controller::notImplemented, GSON::toJson); // add tag
				delete("/:uuid", Controller::notImplemented, GSON::toJson); // delete tag
				post("/:uuid", Controller::notImplemented, GSON::toJson); // add/delete user to/from tag
			});
			path("/object", () -> {
				post("/:uuid/tag", Controller::notImplemented, GSON::toJson); // attach tag to object
			});
			path("/sensor", () -> {
				post("", sensorController::post, GSON::toJson); // add sensor
				get("/:uuid", sensorController::get, GSON::toJson); // get sensor
				post("/:uuid", Controller::notImplemented, GSON::toJson); // update sensor
				delete(":uuid", sensorController::delete, GSON::toJson); // delete sensor
				get("/:uuid/data", Controller::notImplemented, GSON::toJson); // get sensor historic data
				get("/:uuid/event", Controller::notImplemented, GSON::toJson); // subscribe sensor events
				delete("/:uuid/event", Controller::notImplemented, GSON::toJson); // unsubscribe sensor events
				post("/:uuid/action", Controller::notImplemented, GSON::toJson); // sensor action
			});
			path("/event", () -> {
				get("", Controller::notImplemented, GSON::toJson); // get events
			});
		});

		// Database.getConnectionSource().close();
		server.stop();
	}
}
