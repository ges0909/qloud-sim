package de.infinit.emp.api.model;

import java.util.UUID;

import de.infinit.emp.api.domain.Session;

public class SessionModel extends Model<Session, UUID> {
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
