package de.infinit.emp.api.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.aeonbits.owner.ConfigCache;

import com.google.gson.annotations.SerializedName;

import de.infinit.emp.ApplicationConfig;
import de.infinit.emp.Status;
import de.infinit.emp.api.domain.Capability;
import de.infinit.emp.api.domain.Event;
import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.domain.Session;
import de.infinit.emp.api.domain.Tag;
import de.infinit.emp.api.domain.User;
import de.infinit.emp.api.model.CapabilityModel;
import de.infinit.emp.api.model.EventModel;
import de.infinit.emp.api.model.SensorModel;
import de.infinit.emp.api.model.SessionModel;
import de.infinit.emp.api.model.TagModel;
import de.infinit.emp.api.model.UserModel;
import de.infinit.emp.utils.Json;
import spark.Request;
import spark.Response;

public class SensorController extends Controller {
	static final ApplicationConfig config = ConfigCache.getOrCreate(ApplicationConfig.class);
	private static SensorController instance = null;
	final SensorModel sensorModel = SensorModel.instance();
	final CapabilityModel capabilityModel = CapabilityModel.instance();
	final SessionModel sessionModel = SessionModel.instance();
	final UserModel userModel = UserModel.instance();
	final TagModel tagModel = TagModel.instance();
	final EventModel eventModel = EventModel.instance();

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
			List<Long> data;
			List<Object> action;
		}

		UUID owner;
		long time;
		String description;
		String sdevice;
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

	private String generateHexId(int length) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length) {
			sb.append(Integer.toHexString(random.nextInt()));
		}
		return sb.toString();
	}

	public Sensor createSensor(User user, String code, String description) {
		Sensor sensor = new Sensor();
		sensor.setOwner(user);
		sensor.setCode(code);
		sensor.setSdevice(generateHexId(8));
		sensor.setDescription(description);
		sensor.setRecvInterval(config.recvInterval());
		sensor.setBatteryOk(true);
		sensor.setSentAsEvent(false);
		if (sensorModel.create(sensor) == null) {
			return null;
		}
		//
		Tag tagAll = user.getTagAll();
		tagAll.setSensor(sensor);
		sensor.getTags().add(tagAll); // tag sensor with user's 'tag_all' tag
		tagModel.update(tagAll);
		//
		Capability capability = new Capability(sensor, 1, "binary_8bit", 1L, null);
		sensor.getCapabilities().add(capability);
		capabilityModel.create(capability);
		capability = new Capability(sensor, 2, "binary_32bit", 0L, config.defaultDelta());
		sensor.getCapabilities().add(capability);
		capabilityModel.create(capability);
		capability = new Capability(sensor, 3, "binary_16bit", 0L, null);
		sensor.getCapabilities().add(capability);
		capabilityModel.create(capability);
		capability = new Capability(sensor, 4, "binary_32bit", 2214814041L, null);
		sensor.getCapabilities().add(capability);
		capabilityModel.create(capability);
		//
		sensor.startSimulation();
		//
		return sensor;
	}

	public Object createSensor(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		CreateOrUpdateSensorRequest req = decode(request.body(), CreateOrUpdateSensorRequest.class);
		if (req.code == null || !Pattern.matches(config.devicePattern(), req.code)) {
			return status(Status.WRONG_CODE);
		}
		if (sensorModel.findFirstByColumn("code", req.code) != null) {
			return status(Status.DUPLICATE_CODE);
		}
		Sensor sensor = createSensor(user, req.code, req.description);
		if (sensor == null) {
			return fail();
		}
		return result("uuid", sensor.getUuid().toString());
	}

	public Object updateSensor(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		if (request.params(":uuid") == null) {
			return status(Status.WRONG_SENSOR);
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
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		if (request.params(":uuid") == null) {
			return status(Status.WRONG_SENSOR);
		}
		UUID uuid = UUID.fromString(request.params(":uuid"));
		Sensor sensor = sensorModel.queryForId(uuid);
		if (sensor == null) {
			return fail();
		}
		User owner = sensor.getOwner();
		if (!owner.equals(user)) {
			return status(Status.WRONG_USER);
		}
		GetSensorResponse res = convert(sensor, GetSensorResponse.class);
		res.owner = owner.getUuid();
		res.time = Instant.now().getEpochSecond();
		//
		res.capabilities = res.new Capability();
		res.capabilities.data = sensor.getCapabilitiesByOrder().stream().map(Capability::getName).collect(Collectors.toList());
		res.capabilities.action = new ArrayList<>();
		//
		res.state = res.new State();
		res.state.data = sensor.getCapabilitiesByOrder().stream().map(Capability::getValue).collect(Collectors.toList());
		res.state.action = new ArrayList<>();		
		return result("sensor", res);
	}

	public Object deleteSensor(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		if (request.params(":uuid") == null) {
			return status(Status.WRONG_SENSOR);
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
		//
		sensor.startSimulation();
		// before sensor is deleted remove it from session event list
		Event event = eventModel.findEventBySensor(sensor);
		if (event != null) {
			eventModel.delete(event.getUuid());
		}
		// delete sensor
		if (sensorModel.delete(uuid) != 1) {
			return fail();
		}
		return ok();
	}

	public Object getSensorData(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		if (request.params(":uuid") == null) {
			return status(Status.WRONG_SENSOR);
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
		String timestamp = Long.toString(Instant.now().getEpochSecond());
		List<Integer> values = Json.arr(1234, 5678, 901234, 9961);
		Map<String, Object> obj = Json.obj(timestamp, values);
		return result("data", obj);
	}
}
