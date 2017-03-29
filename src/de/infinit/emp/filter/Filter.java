package de.infinit.emp.filter;

import static spark.Spark.halt;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import de.infinit.emp.controller.SessionController;
import de.infinit.emp.entity.Session;
import de.infinit.emp.model.SessionModel;
import spark.Request;
import spark.Response;

public class Filter {
	static final Logger log = Logger.getLogger(Filter.class.getName());
	static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	static final JsonParser parser = new JsonParser();
	static final SessionModel sessionService = new SessionModel();

	static final String STATUS_NO_AUTH = "{\"status\":\"no-auth\"}";
	static final String STATUS_NO_SESSION = "{\"status\":\"no-session\"}";

	public static void logRequest(Request request, Response response) {
		String method = request.requestMethod();
		String path = request.uri();
		String body = "";
		if (request.body() != null && !request.body().isEmpty()) {
			body = gson.toJson(parser.parse(request.body()).getAsJsonObject());
		}
		log.log(Level.INFO, "{0} {1} {2}", new Object[] { method, path, body });
	}

	public static void logResponse(Request request, Response response) {
		int status = response.status();
		String body = "";
		if (response.body() != null && !response.body().isEmpty()) {
			body = gson.toJson(parser.parse(response.body()).getAsJsonObject());
		}
		log.log(Level.INFO, "{0} {1}", new Object[] { status, body });
	}

	public static void authenticateRequest(Request request, Response response) {
		String method = request.requestMethod();
		String path = request.pathInfo();
		if (method.equals("GET") && path.equals("/api/session")) {
			return; // new session requested => bypass authorization check
		}
		String authorization = request.headers("Authorization");
		if (authorization == null) {
			log.log(Level.SEVERE, "Authorization header: missing");
			halt(STATUS_NO_AUTH);
		}
		String[] authorizationParts = authorization.split("\\s");
		if (authorizationParts.length != 2) {
			log.log(Level.SEVERE, "Authorization header: only 'Bearer' and <Sid> allowed");
			halt(STATUS_NO_AUTH);
		}
		if (!authorizationParts[0].equals("Bearer")) {
			log.log(Level.SEVERE, "Authorization header: missing keyword 'Bearer'");
			halt(STATUS_NO_AUTH);
		}
		String sid = authorizationParts[1];
		if (sid == null) {
			halt(STATUS_NO_AUTH);
		}
		Session session = sessionService.findSessionBySid(sid);
		if (session == null) {
			log.log(Level.SEVERE, "Authorization header: sid {0}: unknow session", sid);
			halt(STATUS_NO_SESSION);
		}
		request.session().attribute(SessionController.QLOUD_SESSION, session);
		log.log(Level.INFO, "valid sid: {0} ", sid);
	}
}
