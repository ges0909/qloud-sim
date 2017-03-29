package de.infinit.emp;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;

import java.io.IOException;
import java.sql.SQLException;

import org.h2.tools.Server;

import com.google.gson.Gson;

import de.infinit.emp.controller.Controller;
import de.infinit.emp.filter.Filter;
import de.infinit.emp.controller.PartnerController;
import de.infinit.emp.controller.SensorController;
import de.infinit.emp.controller.SessionController;
import de.infinit.emp.controller.SignupController;

public class Application {
	static final Gson gson = new Gson();
	public static void main(String[] args) throws IOException, SQLException {
		Server server = Server.createTcpServer().start();

		before(Filter::authenticateRequest);
		before(Filter::logRequest);
			
		path("/api", () -> {
			path("/session", () -> {
				get("", SessionController::requestNonAuthorizedSession, gson::toJson); // get non-authorized session
				post("", SessionController::loginToPartnerOrProxySession, gson::toJson); // authorize partner/proxy session (login)
				delete("", SessionController::logoutFromSession, gson::toJson); // logout
			});
			path("/partner", () -> {
				get("/user", PartnerController::getPartnerRelatedUsers, gson::toJson); // lists all partner related users
				get("/user/:uuid", PartnerController::getUserInformation, gson::toJson); // get data of user ':uuid'
				post("/user/:uuid", Controller::notImplemented, gson::toJson); // delete user
			});
			path("/signup", () -> {
				post("/verification", SignupController::reserveUserAccount, gson::toJson); // partner-controlled sign-up: pre-reserve user account
				post("/user", SignupController::addUserAccount, gson::toJson); // complete sign-up: create new user account
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
				post("", SensorController::post, gson::toJson); // add sensor
				get("/:uuid", SensorController::get, gson::toJson); // get sensor
				post("/:uuid", Controller::notImplemented, gson::toJson); // update sensor
				delete(":uuid", SensorController::delete, gson::toJson); // delete sensor
				get("/:uuid/data", Controller::notImplemented, gson::toJson); // get sensor historic data
				get("/:uuid/event", Controller::notImplemented, gson::toJson); // subscribe sensor events
				delete("/:uuid/event", Controller::notImplemented, gson::toJson); // unsubscribe sensor events
				post("/:uuid/action", Controller::notImplemented, gson::toJson); // sensor action
			});
			path("/event", () -> {
				get("", Controller::notImplemented, gson::toJson); // get events
			});
		});

		after(Filter::logResponse);
		
		// Database.getConnectionSource().close();
		server.stop();
	}
}
