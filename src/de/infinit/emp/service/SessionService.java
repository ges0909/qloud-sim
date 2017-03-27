package de.infinit.emp.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.infinit.emp.model.Session;

public class SessionService {
	static Map<String, Session> sessions = new HashMap<>();

	public Session createSession(String scheme, String host) {
		Session session = new Session();
		String sid = UUID.randomUUID().toString();
		session.setSid(sid);
		session.setServer(scheme + "://" + host);
		sessions.put(sid, session);
		return session;
	}
	
	public Session findSessionBySid(String sid) {
		return sessions.get(sid);
	}
}
