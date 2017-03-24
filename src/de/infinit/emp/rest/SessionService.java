package de.infinit.emp.rest;

import java.util.UUID;

import spark.Request;

public class SessionService {
	public Session newSession(Request request) {
		Session session = new Session();
		session.setSid(UUID.randomUUID().toString());
		session.setServer(request.uri());
		session.setAuth(false);
		return session;
	}
}
