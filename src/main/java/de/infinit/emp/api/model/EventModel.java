package de.infinit.emp.api.model;

import java.util.UUID;

import de.infinit.emp.api.domain.Event;
import de.infinit.emp.api.domain.Sensor;

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

	public Event findEventBySensor(Sensor sensor) {
		for (Event e : queryForAll()) {
			if (e.getSensor().equals(sensor)) {
				return e;
			}
		}
		return null;
	}
}
