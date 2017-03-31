package de.infinit.emp.controller;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import de.infinit.emp.Status;
import de.infinit.emp.Uuid;
import de.infinit.emp.domain.Capability;
import de.infinit.emp.domain.Sensor;
import de.infinit.emp.model.CapabilityModel;
import de.infinit.emp.model.SensorModel;
import spark.Request;
import spark.Response;

public class SensorController extends Controller {
	static SensorModel sensorModel = new SensorModel();
	static CapabilityModel capabilityModel = new CapabilityModel();

	class AddSensorRequest {
		String code;
		String description;
	}

	class GetSensorResponse {
		class ResponseCapability {
			List<String> data;
			List<String> action;
		}
		long time;
		String description;
		String model;
		int recv_interval;
		int recv_time;
		boolean battery_ok;
		ResponseCapability capabilities;
	}
	
	public static Object addSensor(Request request, Response response) {
		AddSensorRequest req = decode(request.body(), AddSensorRequest.class);
		if (req.code == null) {
			return status(Status.WRONG_CODE);
		}
		if (!Pattern.matches(config.devicePattern(), req.code)) {
			return status(Status.WRONG_CODE);
		}
		if (sensorModel.findByCode(req.code) != null) {
			return status(Status.DUPLICATE_CODE);
		}
		Sensor sensor = new Sensor();
		sensor.setCode(req.code);
		sensor.setDescription(req.description);
		sensor.setUuid(Uuid.get());
		if (sensorModel.create(sensor) == null) {
			return status(Status.FAIL);
		}
		Capability capability = null;
		capability = new Capability(sensor, "binary_8bit", "data");
		if (capabilityModel.create(capability) == null) {
			return status(Status.FAIL);
		}
		capability = new Capability(sensor, "binary_32bit", "data");
		if (capabilityModel.create(capability) == null) {
			return status(Status.FAIL);
		}
		capability = new Capability(sensor, "binary_16bit", "data");
		if (capabilityModel.create(capability) == null) {
			return status(Status.FAIL);
		}
		capability = new Capability(sensor, "binary_32bit", "data");
		if (capabilityModel.create(capability) == null) {
			return status(Status.FAIL);
		}
		return result("uuid", sensor.getUuid());
	}

	public static Object getSensor(Request request, Response response) {
		String uuid = request.params(":uuid");
		Sensor sensor = sensorModel.findByUuid(uuid);
		if (sensor == null) {
			return status(Status.FAIL);
		}
		GetSensorResponse res = convert(sensor, GetSensorResponse.class);
		res.capabilities = res.new ResponseCapability();
		res.capabilities.data = sensor.getCapabilities()
				.stream()
				.filter(c -> c.getType().equals("data"))
				.map(c -> c.getName())
				.collect(Collectors.toList());
		res.capabilities.action = sensor.getCapabilities()
				.stream()
				.filter(c -> c.getType().equals("action"))
				.map(c -> c.getName())
				.collect(Collectors.toList());
		return result("sensor", res);
	}

	public static Object deleteSensor(Request request, Response response) {
		String uuid = request.params(":uuid");
		if (sensorModel.deleteByUuid(uuid) != 1) {
			return status(Status.FAIL);
		}
		return status(Status.OK);
	}
}
