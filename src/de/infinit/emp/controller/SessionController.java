package de.infinit.emp.controller;

import java.util.HashMap;
import java.util.Map;

import de.infinit.emp.model.Session;
import de.infinit.emp.service.SessionService;
import spark.Request;
import spark.Response;

public class SessionController {
	public Map<String, Object> get(Request request, Response response) {
		SessionService sessionService = new SessionService();
		Session session = sessionService.newSession(request.scheme(), request.host());
		Map<String, Object> result = new HashMap<>();
		result.put("server", session.getServer());
		result.put("sid", session.getSid());
		result.put("status", "ok");
		return result;
	}
}