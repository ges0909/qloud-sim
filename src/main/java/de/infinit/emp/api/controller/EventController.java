package de.infinit.emp.api.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import de.infinit.emp.Status;
import de.infinit.emp.api.domain.Event;
import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.domain.Session;
import de.infinit.emp.api.domain.State;
import de.infinit.emp.api.domain.User;
import de.infinit.emp.api.domain.Value;
import de.infinit.emp.api.model.CapabilityModel;
import de.infinit.emp.api.model.EventModel;
import de.infinit.emp.api.model.SensorModel;
import de.infinit.emp.api.model.StateModel;
import de.infinit.emp.api.model.UserModel;
import de.infinit.emp.utils.Json;
import spark.Request;
import spark.Response;

public class EventController extends Controller {
	private static EventController instance = null;
	final EventModel eventModel = EventModel.instance();
	final UserModel userModel = UserModel.instance();
	final SensorModel sensorModel = SensorModel.instance();
	final CapabilityModel capabilityModel = CapabilityModel.instance();
	final StateModel stateModel = StateModel.instance();

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
	public Object susbcribeForEvents(Request request, Response response) {
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
		long timeout = 3600;
		String timeoutParam = request.queryParams("timeout");
		if (timeoutParam != null) {
			timeout = Long.parseLong(timeoutParam);
		}
		if (timeout < 1 /* min */ || timeout > 36000 /* max */) {
			return fail();
		}
		Instant now = Instant.now();
		Date expiresAt = Date.from(now.plusSeconds(timeout));
		Optional<Event> optional = session.getEvents().stream().filter(e -> e.getSensor().equals(sensor)).findFirst();
		if (optional.isPresent()) {
			Event event = optional.get();
			event.setExpiresAt(expiresAt);
			if (eventModel.update(event) == null) {
				return fail();
			}
		} else {
			Event event = new Event(session, sensor, expiresAt);
			if (eventModel.create(event) == null) {
				return fail();
			}
			session.getEvents().add(event);
		}
		return ok();
	}

	// DELETE /api/sensor/:uuid/event
	public Object unsubscribeForEvents(Request request, Response response) {
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
		if (optional.isPresent()) {
			Event event = optional.get();
			session.getEvents().remove(event);
			if (eventModel.delete(event.getUuid()) != 1) {
				return fail();
			}
		}
		return ok();
	}

	// GET /api/event
	public Object getEvents(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		// timeout query param
		int timeout = 55; // valid range: 0..300 seconds, default: 55
		String timeoutParam = request.queryParams("timeout");
		if (timeoutParam != null) {
			timeout = Integer.parseUnsignedInt(timeoutParam);
		}
		if (timeout < 0 /* min */ || timeout > 300 /* max */) {
			return fail();
		}
		if (config.eventTimout() != null && config.eventTimout() > 0) {
			timeout = config.eventTimout();
		}
		// next query param
		long next = 0; // default: 0
		String nextParam = request.queryParams("next");
		if (nextParam != null) {
			next = Long.parseLong(nextParam);
		}
		if (next < 0 /* min */) {
			return fail();
		}
		// block request
		Random rn = new Random();
		int effectiveTimeout = rn.nextInt(timeout) + 1;
		try {
			TimeUnit.SECONDS.sleep(effectiveTimeout);
		} catch (InterruptedException e) {
			// ignore execption
		}
		//
		long id = 0;
		List<Object> events = new ArrayList<>();
		for (Event e : session.getSubcribedEvents()) {
			if (e.isExpired()) {
				session.getEvents().remove(e);
				eventModel.delete(e.getUuid());
				continue;
			}
			Sensor sensor = e.getSensor();
			State state = sensor.getState();
			if (!state.isEventSent()) {
				state.setEventSent(true);
				stateModel.update(state);
				// build sensor event
				UUID uuid = e.getSensor().getUuid();
				long eventTime = Instant.now().getEpochSecond();
				String recvTime = String.valueOf(state.getRecvTime()) + "000";
				List<Long> values = state.getValues().stream().map(Value::getValue).collect(Collectors.toList());
				Map<String, Object> data = Json.obj(recvTime, values);
				Object obj = Json.obj("event", "sensor_data", "time", eventTime, "sensor", uuid, "data", data, "id", id);
				id = id + 1;
				// add sensor event to event list
				events.add(obj);
			}
		}
		return result("event", events, "next", next);
	}
}
