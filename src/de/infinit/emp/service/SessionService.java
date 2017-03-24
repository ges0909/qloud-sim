package de.infinit.emp.service;

import java.util.UUID;

import de.infinit.emp.model.Session;
import spark.Request;

public class SessionService {
	public Session newSession(Request request) {
		Session session = new Session();
		session.setSid(UUID.randomUUID().toString());
		session.setServer("http://" + request.host());
		session.setAuth(false);
		return session;
	}
}
