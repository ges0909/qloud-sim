package de.infinit.emp.filter;

import static spark.Spark.halt;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.infinit.emp.api.controller.SessionController;
import de.infinit.emp.api.domain.Session;
import de.infinit.emp.api.model.SessionModel;
import spark.Request;
import spark.Response;

public class AuthenticationFilter {
	static final Logger log = Logger.getLogger(AuthenticationFilter.class.getName());
	static final SessionModel sessionModel = SessionModel.instance();

	static final String STATUS_NO_AUTH = "{\"status\":\"no-auth\"}";
	static final String STATUS_NO_SESSION = "{\"status\":\"no-session\"}";

	public static void authenticateRequest(Request request, Response response) {
		String path = request.pathInfo();
		if (path.startsWith("/config") || path.startsWith("/upload")) {
			return;
		}
		String method = request.requestMethod();
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
		Session session = sessionModel.queryForId(sid);
		request.session().attribute(SessionController.SESSION, session);
		if (session == null) {
			log.log(Level.SEVERE, "Authorization header: sid {0}: unknow session", sid);
			halt(STATUS_NO_SESSION);
		}
		log.log(Level.INFO, "valid sid: {0} ", sid);
	}
}
