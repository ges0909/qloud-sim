package de.infinit.emp.controller;

import java.util.List;
import java.util.regex.Pattern;

import de.infinit.emp.Status;
import de.infinit.emp.Uuid;
import de.infinit.emp.controller.PartnerController.UserDataResponse;
import de.infinit.emp.entity.Sensor;
import de.infinit.emp.model.SensorModel;
import spark.Request;
import spark.Response;

public class SensorController extends Controller {
	static SensorModel sensorModel = new SensorModel();

	class AddSensorRequest {
		String code;
		String description;
	}

	class GetSensorResponse {
		Long time;
		String description;
		String model;
		Integer recv_interval;
		Long recv_time;
		Boolean battery_ok;
		List<Capabities> capabilities;
	}
	
	class Capabities {
		List<String> data;
		List<String> action;
	}
	
	public static Object addSensor(Request request, Response response) {
		AddSensorRequest body = decode(request.body(), AddSensorRequest.class);
		if (body.code == null) {
			return status(Status.WRONG_CODE);
		}
		if (!Pattern.matches(config.devicePattern(), body.code)) {
			return status(Status.WRONG_CODE);
		}
		if (sensorModel.findByCode(body.code) != null) {
			return status(Status.DUPLICATE_CODE);
		}
		Sensor sensor = new Sensor();
		sensor.setCode(body.code);
		sensor.setDescription(body.description);
		sensor.setUuid(Uuid.get());
		if (sensorModel.create(sensor) == null) {
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
		return result("sensor", convert(sensor, UserDataResponse.class));
	}

	public static Object deleteSensor(Request request, Response response) {
		String uuid = request.params(":uuid");
		if (sensorModel.deleteByUuid(uuid) == 1) {
			return status(Status.OK);
		}
		return status(Status.FAIL);
	}
}
