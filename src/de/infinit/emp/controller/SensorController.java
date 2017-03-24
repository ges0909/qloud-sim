package de.infinit.emp.controller;

import static spark.Spark.post;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import de.infinit.emp.JsonTransformer;
import de.infinit.emp.model.Sensor;
import de.infinit.emp.service.SensorService;
import spark.Request;
import spark.Response;
import spark.Route;

public class SensorController {

	private static final String PATH = "/api/sensor";
	private static final Gson GSON = new Gson();
	private final SensorService sensorService = new SensorService();

	public SensorController() {
		post(PATH, new Route() {
			@Override
			public Object handle(Request req, Response res) {
				Sensor sensor = GSON.fromJson(req.body(), Sensor.class);
				sensor = sensorService.createSensor(sensor);
				Map<String, String> result = new HashMap<>();
				result.put("uuid", sensor.getUuid());
				result.put("status", "ok");
				return result;
			}
		}, new JsonTransformer());
	}
}
