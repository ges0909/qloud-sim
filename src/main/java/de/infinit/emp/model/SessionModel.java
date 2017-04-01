package de.infinit.emp.model;

import de.infinit.emp.domain.Session;

public class SessionModel extends Model<Session, String> {
	private static SessionModel instance = null;

	private SessionModel() {
		super(Session.class);
	}

	public static SessionModel instance() {
		if (instance == null) {
			instance = new SessionModel();
		}
		return instance;
	}
}
