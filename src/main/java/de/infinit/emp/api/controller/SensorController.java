package de.infinit.emp.api.controller;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.annotations.SerializedName;

import de.infinit.emp.Status;
import de.infinit.emp.api.domain.Capability;
import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.domain.Session;
import de.infinit.emp.api.domain.Tag;
import de.infinit.emp.api.domain.User;
import de.infinit.emp.api.model.CapabilityModel;
import de.infinit.emp.api.model.SensorModel;
import de.infinit.emp.api.model.SessionModel;
import de.infinit.emp.api.model.TagModel;
import de.infinit.emp.api.model.UserModel;
import de.infinit.emp.utils.Json;
import spark.Request;
import spark.Response;

public class SensorController extends Controller {
	private static SensorController instance = null;
	final SensorModel sensorModel = SensorModel.instance();
	final CapabilityModel capabilityModel = CapabilityModel.instance();
	final SessionModel sessionModel = SessionModel.instance();
	final UserModel userModel = UserModel.instance();
	final TagModel tagModel = TagModel.instance();

	private SensorController() {
		super();
	}

	public static SensorController instance() {
		if (instance == null) {
			instance = new SensorController();
		}
		return instance;
	}

	class CreateOrUpdateSensorRequest {
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
		@SerializedName("recv_interval")
		int recvInterval;
		@SerializedName("recv_time")
		int recvTime;
		@SerializedName("battery_ok")
		boolean batteryOk;
		Capability capabilities;
		State state;
	}

	public Sensor createSensor(User user, String code, String description) {
		Sensor sensor = new Sensor();
		sensor.setOwner(user);
		sensor.setCode(code);
		sensor.setDescription(description);
		sensor.setRecvTime(Instant.now().getEpochSecond());
		sensor.setRecvInterval(900);
		sensor.setBatteryOk(true);
		//
		Tag tagAll = user.getTagAll();
		tagAll.setSensor(sensor);
		Collection<Tag> tags = sensor.getTags();
		tags.add(tagAll); // tag sensor with user's 'tag_all' tag
		//
		Collection<Capability> capabilities = sensor.getCapabilities();
		capabilities.add(new Capability(sensor, "binary_8bit", "data"));
		capabilities.add(new Capability(sensor, "binary_32bit", "data"));
		capabilities.add(new Capability(sensor, "binary_16bit", "data"));
		capabilities.add(new Capability(sensor, "binary_32bit", "data"));
		//
		tagModel.update(tagAll);
		return sensorModel.create(sensor);
	}

	public Object createSensor(Request request, Response response) {
		if (!isProxySession(request)) {
			status(Status.NO_AUTH);
		}
		Session session = sessionModel.queryForId(SessionController.SESSION);
		User own = session.getUser();
		if (own == null) {
			return status(Status.WRONG_USER);
		}
		CreateOrUpdateSensorRequest req = decode(request.body(), CreateOrUpdateSensorRequest.class);
		if (req.code == null || !Pattern.matches(config.devicePattern(), req.code)) {
			return status(Status.WRONG_CODE);
		}
		if (sensorModel.findFirstByColumn("code", req.code) != null) {
			return status(Status.DUPLICATE_CODE);
		}
		Sensor sensor = createSensor(own, req.code, req.description);
		if (sensor == null) {
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
		CreateOrUpdateSensorRequest req = decode(request.body(), CreateOrUpdateSensorRequest.class);
		if (req.code != null) {
			if (!Pattern.matches(config.devicePattern(), req.code)) {
				return status(Status.WRONG_CODE);
			}
			if (sensorModel.findFirstByColumn("code", req.code) != null) {
				return status(Status.DUPLICATE_SENSOR);
			}
			sensor.setCode(req.code);
		}
		sensor.setDescription(req.description);
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
		res.owner = sensor.getOwnerUuid();
		res.capabilities = res.new Capability();
		res.capabilities.data = sensor.getCapabilities().stream().filter(c -> c.getType().equals("data")).map(Capability::getName).collect(Collectors.toList());
		res.capabilities.action = sensor.getCapabilities().stream().filter(c -> c.getType().equals("action")).map(Capability::getName).collect(Collectors.toList());
		res.state = res.new State();
		res.state.data = sensor.getCapabilities().stream().filter(c -> c.getType().equals("data")).map(c -> (Integer) null).collect(Collectors.toList());
		res.state.action = sensor.getCapabilities().stream().filter(c -> c.getType().equals("action")).map(c -> null).collect(Collectors.toList());
		return result("sensor", res);
	}

	public Object deleteSensor(Request request, Response response) {
		if (!isProxySession(request)) {
			status(Status.NO_AUTH);
		}
		String uuid = request.params(":uuid");
		Sensor sensor = sensorModel.queryForId(uuid);
		if (sensor == null) {
			return status(Status.WRONG_SENSOR);
		}
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
}
