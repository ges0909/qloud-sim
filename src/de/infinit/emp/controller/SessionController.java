package de.infinit.emp.controller;

import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import de.infinit.emp.model.Session;
import de.infinit.emp.service.SessionService;

public class SessionController {
	private static final String PATH = "/api/session";
	private static final Gson GSON = new Gson();
	private final SessionService sessionService = new SessionService();

	public SessionController() {
		get(PATH, (request, response) -> {
			Session session = sessionService.newSession(request.scheme(), request.host());
			Map<String, String> result = new HashMap<>();
			result.put("server", session.getServer());
			result.put("sid", session.getSid());
			result.put("status", "ok");
			return result;
		}, GSON::toJson);
	}
}