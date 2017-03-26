package de.infinit.emp.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import de.infinit.emp.model.Sensor;
import de.infinit.emp.service.SensorService;
import spark.Request;
import spark.Response;

public class SensorController {
	private static final Gson GSON = new Gson();
	private static SensorService sensorService;

	public SensorController() throws IOException, SQLException {
		sensorService = new SensorService();
	}

	private Map<String, Object> result(Object... args) {
		Map<String, Object> r = new HashMap<>();
		for (int i = 0; i < args.length; i = i + 2) {
			r.put((String) args[i], args[i + 1]);
		}
		return r;
	}

	public Map<String, Object> post(Request request, Response response) throws IOException, SQLException {
		Sensor sensor = GSON.fromJson(request.body(), Sensor.class);
		sensor = sensorService.create(sensor);
		if (sensor == null) {
			return result("status", "fail");
		}
		return result("status", "ok", "uuid", sensor.getUuid());
	}

	public Map<String, Object> get(Request request, Response response) throws IOException, SQLException {
		String uuid = request.params(":uuid");
		Sensor sensor = sensorService.getByUuid(uuid);
		if (sensor == null) {
			return result("status", "fail");
		}
		return result("status", "ok");
	}

	public Map<String, Object> delete(Request request, Response response) throws IOException, SQLException {
		String uuid = request.params(":uuid");
		if (sensorService.deleteByUuid(uuid) == 1) {
			return result("status", "ok");
		}
		return result("status", "fail");
	}
}
