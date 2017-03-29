package de.infinit.emp.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Pattern;

import de.infinit.emp.Status;
import de.infinit.emp.Uuid;
import de.infinit.emp.entity.Sensor;
import de.infinit.emp.model.SensorModel;
import spark.Request;
import spark.Response;

public class SensorController extends Controller {
	static SensorModel sensorModel = new SensorModel();

	class Body {
		String code;
		String description;
	}

	public static Map<String, Object> post(Request request, Response response) throws IOException, SQLException {
		Body body = gson.fromJson(request.body(), Body.class);
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

	public static Map<String, Object> get(Request request, Response response) throws IOException, SQLException {
		String uuid = request.params(":uuid");
		Sensor sensor = sensorModel.findByUuid(uuid);
		if (sensor == null) {
			return status(Status.FAIL);
		}
		return status(Status.OK);
	}

	public static Map<String, Object> delete(Request request, Response response) throws IOException, SQLException {
		String uuid = request.params(":uuid");
		if (sensorModel.deleteByUuid(uuid) == 1) {
			return status(Status.OK);
		}
		return status(Status.FAIL);
	}
}
