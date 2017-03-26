package de.infinit.emp.controller;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import de.infinit.emp.model.Sensor;
import de.infinit.emp.service.SensorService;

public class SensorController {
	private static final String PATH = "/api/sensor";
	private static final Gson GSON = new Gson();
	private final SensorService sensorService;

	private Map<String, Object> result(Object... args) {
		Map<String, Object> r = new HashMap<>();
		for (int i = 0; i < args.length; i = i + 2) {
			r.put((String) args[i], args[i + 1]);
		}
		return r;
	}

	public SensorController() throws IOException, SQLException {
		sensorService = new SensorService();

		// POST /api/sensor
		post(PATH, (request, response) -> {
			Sensor sensor = GSON.fromJson(request.body(), Sensor.class);
			sensor = sensorService.create(sensor);
			if (sensor == null) {
				return result("status", "fail");
			}
			return result("status", "ok", "uuid", sensor.getUuid());
		}, GSON::toJson);

		// GET /api/sensor/:uuid
		get(PATH + "/:uuid", (request, response) -> {
			String uuid = request.params(":uuid");
			Sensor sensor = sensorService.getByUuid(uuid);
			if (sensor == null) {
				return result("status", "fail");
			}
			return result("status", "ok");
		}, GSON::toJson);

		// DELETE /api/sensor/:uuid
		delete(PATH + "/:uuid", (request, response) -> {
			String uuid = request.params(":uuid");
			if (sensorService.deleteByUuid(uuid) == 1) {
				return result("status", "ok");
			}
			return result("status", "fail");
		}, GSON::toJson);
	}
}
