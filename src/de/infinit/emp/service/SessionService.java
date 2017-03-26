package de.infinit.emp.service;

import java.util.UUID;

import de.infinit.emp.model.Session;

public class SessionService {
	public Session newSession(String scheme, String host) {
		Session session = new Session();
		session.setSid(UUID.randomUUID().toString());
		session.setServer(scheme + "://" + host);
		session.setAuth(false);
		return session;
	}
}
