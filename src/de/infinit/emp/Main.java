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
import de.infinit.emp.controller.LoggingFilter;
import de.infinit.emp.controller.SensorController;
import de.infinit.emp.controller.SessionController;

public class Main {
	static final Gson GSON = new Gson();

	public static void main(String[] args) throws IOException, SQLException {
		Server server = Server.createTcpServer().start();

		new LoggingFilter();
		new AuthenticationFilter();

		SessionController sessionController = new SessionController();
		SensorController sensorController = new SensorController();

		path("/api", () -> {
			get("/session", sessionController::get, GSON::toJson);
			post("/session", sessionController::post, GSON::toJson);

			get("/sensor/:uuid", sensorController::get, GSON::toJson);
			post("/sensor", sensorController::post, GSON::toJson);
			delete("/sensor/:uuid", sensorController::delete, GSON::toJson);
		});

		server.stop();
	}
}
