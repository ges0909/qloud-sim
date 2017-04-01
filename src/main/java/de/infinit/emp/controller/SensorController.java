package de.infinit.emp.controller;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.infinit.emp.Status;
import de.infinit.emp.Uuid;
import de.infinit.emp.domain.Capability;
import de.infinit.emp.domain.Sensor;
import de.infinit.emp.domain.Session;
import de.infinit.emp.domain.User;
import de.infinit.emp.model.CapabilityModel;
import de.infinit.emp.model.SensorModel;
import de.infinit.emp.model.UserModel;
import de.infinit.emp.utils.Json;
import spark.Request;
import spark.Response;

public class SensorController extends Controller {
	private static SensorController instance = null;
	final SensorModel sensorModel = SensorModel.instance();
	final CapabilityModel capabilityModel = CapabilityModel.instance();
	final UserModel userModel = UserModel.instance();

	private SensorController() {
		super();
	}

	public static SensorController instance() {
		if (instance == null) {
			instance = new SensorController();
		}
		return instance;
	}

	class AddOrUpdateSensorRequest {
		String code;
		String description;
	}

	class GetSensorResponse {
		class Capability {
			List<String> data;
			List<String> action;
		}

		class State {
			List<Integer> data;
			List<Object> action;
		}

		String owner;
		long time;
		String description;
		String model;
		int recv_interval;
		int recv_time;
		boolean battery_ok;
		Capability capabilities;
		State state;
	}

	public Object addSensor(Request request, Response response) {
		if (!isProxySession(request)) {
			status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User own = userModel.queryForId(session.getUser());
		if (own == null) {
			return fail();
		}
		AddOrUpdateSensorRequest req = decode(request.body(), AddOrUpdateSensorRequest.class);
		if (req.code == null) {
			return status(Status.WRONG_CODE);
		}
		if (!Pattern.matches(config.devicePattern(), req.code)) {
			return status(Status.WRONG_CODE);
		}
		if (sensorModel.findByColumn("code", req.code) != null) {
			return status(Status.DUPLICATE_CODE);
		}
		Sensor sensor = new Sensor();
		sensor.setCode(req.code);
		sensor.setDescription(req.description);
		sensor.setRecvTime(Instant.now().getEpochSecond());
		sensor.setRecvInterval(900);
		sensor.setBatteryOk(true);
		sensor.setUuid(Uuid.next());
		sensor.setUser(own);
		if (sensorModel.create(sensor) == null) {
			return fail();
		}
		Capability capability = null;
		capability = new Capability(sensor, "binary_8bit", "data");
		if (capabilityModel.create(capability) == null) {
			return fail();
		}
		capability = new Capability(sensor, "binary_32bit", "data");
		if (capabilityModel.create(capability) == null) {
			return fail();
		}
		capability = new Capability(sensor, "binary_16bit", "data");
		if (capabilityModel.create(capability) == null) {
			return fail();
		}
		capability = new Capability(sensor, "binary_32bit", "data");
		if (capabilityModel.create(capability) == null) {
			return fail();
		}
		return result("uuid", sensor.getUuid());
	}

	public Object updateSensor(Request request, Response response) {
		if (!isProxySession(request)) {
			status(Status.NO_AUTH);
		}
		String uuid = request.params(":uuid");
		Sensor sensor = sensorModel.queryForId(uuid);
		if (sensor == null) {
			return status(Status.WRONG_SENSOR);
		}
		AddOrUpdateSensorRequest req = decode(request.body(), AddOrUpdateSensorRequest.class);
		if (req.code != null) {
			if (!Pattern.matches(config.devicePattern(), req.code)) {
				return status(Status.WRONG_CODE);
			}
			if (sensorModel.findByColumn("code", req.code) != null) {
				return status(Status.DUPLICATE_SENSOR);
			}
			sensor.setCode(req.code);
		}
		if (req.description != null) {
			sensor.setDescription(req.description);
		}
		if (sensorModel.update(sensor) == null) {
			return fail();
		}
		return ok();
	}

	public Object getSensor(Request request, Response response) {
		if (!isProxySession(request)) {
			status(Status.NO_AUTH);
		}
		String uuid = request.params(":uuid");
		Sensor sensor = sensorModel.queryForId(uuid);
		if (sensor == null) {
			return fail();
		}
		GetSensorResponse res = convert(sensor, GetSensorResponse.class);
		res.owner = sensor.getUser().getUuid();
		res.capabilities = res.new Capability();
		res.capabilities.data = sensor.getCapabilities().stream().filter(c -> c.getType().equals("data"))
				.map(c -> c.getName()).collect(Collectors.toList());
		res.capabilities.action = sensor.getCapabilities().stream().filter(c -> c.getType().equals("action"))
				.map(c -> c.getName()).collect(Collectors.toList());
		res.state = res.new State();
		res.state.data = sensor.getCapabilities().stream().filter(c -> c.getType().equals("data"))
				.map(c -> (Integer) null).collect(Collectors.toList());
		res.state.action = sensor.getCapabilities().stream().filter(c -> c.getType().equals("action")).map(c -> null)
				.collect(Collectors.toList());
		return result("sensor", res);
	}

	public Object deleteSensor(Request request, Response response) {
		if (!isProxySession(request)) {
			status(Status.NO_AUTH);
		}
		String uuid = request.params(":uuid");
		if (sensorModel.delete(uuid) != 1) {
			return fail();
		}
		return ok();
	}

	public Object getSensorData(Request request, Response response) {
		if (!isProxySession(request)) {
			status(Status.NO_AUTH);
		}
		String uuid = request.params(":uuid");
		Sensor sensor = sensorModel.queryForId(uuid);
		if (sensor == null) {
			return status(Status.WRONG_SENSOR);
		}
		String timestamp = Long.toString(Instant.now().getEpochSecond());
		List<Integer> values = Json.arr(1234, 5678, 901234, 9961);
		Map<String, Object> obj = Json.obj(timestamp, values);
		return result("data", obj);
	}

	public Object susbcribeSensorForEvents(Request request, Response response) {
		if (!isProxySession(request)) {
			status(Status.NO_AUTH);
		}
		String uuid = request.params(":uuid");
		Sensor sensor = sensorModel.queryForId(uuid);
		if (sensor == null) {
			return status(Status.WRONG_SENSOR);
		}
		return ok();
	}

	public Object cancelSensorEventSubcription(Request request, Response response) {
		if (!isProxySession(request)) {
			status(Status.NO_AUTH);
		}
		String uuid = request.params(":uuid");
		Sensor sensor = sensorModel.queryForId(uuid);
		if (sensor == null) {
			return status(Status.WRONG_SENSOR);
		}
		return ok();
	}
}
