package de.infinit.emp.controller;

import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import de.infinit.emp.model.Session;
import de.infinit.emp.service.SessionService;

public class SessionController extends LoggingFilter {
	private static final String PATH = "/api/session";
	private final SessionService sessionService = new SessionService();

	public SessionController() {
		get(PATH, (request, response) -> {
			Session session = sessionService.newSession();
			Map<String, String> result = new HashMap<>();
			result.put("server", session.getServer());
			result.put("sid", session.getSid());
			result.put("status", "ok");
			return result;
		}, new JsonTransformer());
	}
}