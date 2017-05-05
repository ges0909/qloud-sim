package de.infinit.emp.api.model;

import java.util.UUID;

import de.infinit.emp.api.domain.Event;

public class EventModel extends Model<Event, UUID> {
	private static EventModel instance = null;

	private EventModel() {
		super(Event.class);
	}

	public static EventModel instance() {
		if (instance == null) {
			instance = new EventModel();
		}
		return instance;
	}
}
