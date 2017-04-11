package de.infinit.emp.api.controller;

import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import de.infinit.emp.Status;
import de.infinit.emp.api.domain.Event;
import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.model.EventModel;
import de.infinit.emp.api.model.SensorModel;
import spark.Request;
import spark.Response;

public class EventController extends Controller {
	private static EventController instance = null;
	static final Logger log = Logger.getLogger(TagController.class.getName());
	final EventModel eventModel = EventModel.instance();
	final SensorModel sensorModel = SensorModel.instance();

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
		if (!isProxySession(request)) {
			status(Status.NO_AUTH);
		}
		String uuid = request.params(":uuid");
		Sensor sensor = sensorModel.queryForId(uuid);
		if (sensor == null) {
			return status(Status.WRONG_SENSOR);
		}
		long timeout = 36000; // valid range 1..36000 seconds (10 hours),
								// default: 3600 (1 hour )
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
		Event event = eventModel.queryForId(sensor.getUuid());
		if (event == null) {
			event = new Event();
			event.setExpiresAt(expiresAt);
			event.setSensorUuid(sensor.getUuid());
			if (eventModel.create(event) == null) {
				return fail();
			}
		} else {
			event.setExpiresAt(expiresAt);
			if (eventModel.update(event) == null) {
				return fail();
			}
		}
		return ok();
	}

	// DELETE /api/sensor/:uuid/event
	public Object cancelSensorEventSubcription(Request request, Response response) {
		if (!isProxySession(request)) {
			status(Status.NO_AUTH);
		}
		String uuid = request.params(":uuid");
		Sensor sensor = sensorModel.queryForId(uuid);
		if (sensor == null) {
			return status(Status.WRONG_SENSOR);
		}
		if (eventModel.delete(sensor.getUuid()) != 1) {
			return fail();
		}
		return ok();
	}

	// GET /API/event
	public Object getSensorEvents(Request request, Response response) {
		if (!isProxySession(request)) {
			status(Status.NO_AUTH);
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
		try {
			Thread.sleep(effectiveTimeout * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return ok();
	}
}
