package de.infinit.emp.api.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import de.infinit.emp.Status;
import de.infinit.emp.api.domain.Event;
import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.domain.Session;
import de.infinit.emp.api.domain.User;
import de.infinit.emp.api.model.EventModel;
import de.infinit.emp.api.model.SensorModel;
import de.infinit.emp.api.model.UserModel;
import de.infinit.emp.utils.Json;
import spark.Request;
import spark.Response;

public class EventController extends Controller {
	private static EventController instance = null;
	final EventModel eventModel = EventModel.instance();
	final UserModel userModel = UserModel.instance();
	final SensorModel sensorModel = SensorModel.instance();
	long nextCounter = 0;

	private EventController() {
		super();
	}

	public static EventController instance() {
		if (instance == null) {
			instance = new EventController();
		}
		return instance;
	}

	// GET /api/sensor/:uuid/event
	public Object susbcribeSensorForEvents(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		UUID uuid = UUID.fromString(request.params(":uuid"));
		Sensor sensor = sensorModel.queryForId(uuid);
		if (sensor == null) {
			return status(Status.WRONG_SENSOR);
		}
		User owner = sensor.getOwner();
		if (!owner.equals(user)) {
			return status(Status.WRONG_USER);
		}
		// valid range 1..36000 seconds (10 hours), default: 3600 (1 hour )
		long timeout = 36000;
		String timeoutParam = request.queryParams("timeout");
		if (timeoutParam != null) {
			timeout = Long.parseLong(timeoutParam);
		}
		if (timeout < 1 /* min */ || timeout > 36000 /* max */) {
			return fail();
		}
		Instant now = Instant.now();
		now.plusSeconds(timeout);
		Date expiresAt = Date.from(now);
		Optional<Event> optional = session.getEvents().stream().filter(e -> e.getSensor().equals(sensor)).findFirst();
		if (optional.isPresent()) {
			Event event = optional.get();
			event.setExpiresAt(expiresAt);
			if (eventModel.update(event) == null) {
				return fail();
			}
		} else {
			Event event = new Event(session, sensor, expiresAt);
			session.getEvents().add(event);
		}
		return ok();
	}

	// DELETE /api/sensor/:uuid/event
	public Object cancelSensorEventSubcription(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		UUID uuid = UUID.fromString(request.params(":uuid"));
		Sensor sensor = sensorModel.queryForId(uuid);
		if (sensor == null) {
			return status(Status.WRONG_SENSOR);
		}
		User owner = sensor.getOwner();
		if (!owner.equals(user)) {
			return status(Status.WRONG_USER);
		}
		Optional<Event> optional = session.getEvents().stream().filter(e -> e.getSensor().equals(sensor)).findFirst();
		if (!optional.isPresent()) {
			return fail();
		}
		Event event = optional.get();
		session.getEvents().remove(event);
		return ok();
	}

	// GET /API/event
	public Object getSensorEvents(Request request, Response response) throws InterruptedException {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		int timeout = 55; // valid range: 0..300 seconds, default: 55
		String timeoutParam = request.queryParams("timeout");
		if (timeoutParam != null) {
			timeout = Integer.parseUnsignedInt(timeoutParam);
		}
		if (timeout < 0 /* min */ || timeout > 300 /* max */) {
			return fail();
		}
		long next = 0; // default: 0
		String nextParam = request.queryParams("next");
		if (nextParam != null) {
			next = Long.parseLong(nextParam);
		}
		if (next < 0 /* min */) {
			return fail();
		}

		Random rn = new Random();
		int effectiveTimeout = rn.nextInt(timeout) + 1;
		Thread.sleep(effectiveTimeout * 1000L);

		long eventId = 0;
		String eventType = "sensor_data";
		long eventTime = Instant.now().getEpochSecond();

		List<Object> events = new ArrayList<>();
		for (Event e : session.getEvents()) {
			Sensor sensor = e.getSensor();
			List<Object> values = Json.arr(1, 0, 0, 2231828644L);
			Map<String, Object> data = Json.obj("1481730387000", values);
			Object obj = Json.obj("event", eventType, "time", eventTime, "sensor", e.getSensor().getUuid(), "data",
					data, "id", eventId);
			events.add(obj);
			eventId = eventId + 1;
		}

		nextCounter = nextCounter + 1;

		return result("event", events, "next", nextCounter);
	}
}
