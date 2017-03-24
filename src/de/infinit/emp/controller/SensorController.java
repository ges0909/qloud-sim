package de.infinit.emp.controller;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
//import java.util.logging.Logger;

import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;

import de.infinit.emp.model.Sensor;
import de.infinit.emp.service.SensorService;

public class SensorController {
	// private static final Logger LOG =
	// Logger.getLogger(SensorController.class.getName());
	private static final String PATH = "/api/sensor";
	private static final Gson GSON = new Gson();
	private final SensorService sensorService;

	public SensorController() throws IOException, SQLException {
		sensorService = new SensorService();

		// POST /api/sensor
		post(PATH, (request, response) -> {
			Sensor sensor = GSON.fromJson(request.body(), Sensor.class);
			sensor = sensorService.create(sensor);
			Map<String, String> result = new HashMap<>();
			if (sensor == null) {
				result.put("status", "fail");
				response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
			} else {
				result.put("uuid", sensor.getUuid());
				result.put("status", "ok");
				response.status(HttpStatus.CREATED_201);
			}
			return result;
		}, new JsonTransformer());

		// GET /api/sensor/:id
		get(PATH + "/:uuid", (request, response) -> {
			String uuid = request.params("uuid");
			Sensor sensor = sensorService.getByUuid(uuid);
			Map<String, String> result = new HashMap<>();
			if (sensor == null) {
				result.put("status", "fail");
				response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
			} else {
				result.put("uuid", sensor.getUuid());
				result.put("status", "ok");
				response.status(HttpStatus.OK_200);
			}
			return result;
		}, new JsonTransformer());

		// DELETE /api/sensor/:id
		delete(PATH + "/:uuid", (request, response) -> {
			String uuid = request.params("uuid");
			int rowCount = sensorService.deleteByUuid(uuid);
			Map<String, String> result = new HashMap<>();
			if (rowCount == 1) {
				result.put("status", "ok");
				response.status(HttpStatus.NO_CONTENT_204);
			} else {
				result.put("status", "fail");
				response.status(HttpStatus.NOT_FOUND_404);
			}
			return result;
		}, new JsonTransformer());
	}
}
