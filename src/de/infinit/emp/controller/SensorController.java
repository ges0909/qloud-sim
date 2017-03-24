package de.infinit.emp.controller;

import static spark.Spark.post;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;

import de.infinit.emp.model.Sensor;
import de.infinit.emp.service.SensorService;
import spark.Request;
import spark.Response;
import spark.Route;

public class SensorController {
	private static final String PATH = "/api/sensor";
	private static final Gson GSON = new Gson();
	private final SensorService sensorService;

	public SensorController() throws IOException, SQLException {
		sensorService = new SensorService();
		// POST /api/sensor
		post(PATH, new Route() {
			@Override
			public Object handle(Request request, Response response) {
				Sensor sensor = GSON.fromJson(request.body(), Sensor.class);
				sensor = sensorService.createSensor(sensor);
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
			}
		}, new JsonTransformer());
		
	}
}
