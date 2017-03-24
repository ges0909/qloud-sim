package de.infinit.emp.service;

import java.util.UUID;

import de.infinit.emp.model.Session;

public class SessionService {
	private static final String SERVER = "http://localhost:4567";
	
	public Session newSession() {
		Session session = new Session();
		session.setSid(UUID.randomUUID().toString());
		session.setServer(SERVER);
		session.setAuth(false);
		return session;
	}
}
