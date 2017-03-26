package de.infinit.emp.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.aeonbits.owner.ConfigCache;

import com.google.gson.Gson;

import de.infinit.emp.SimulatorConfig;
import de.infinit.emp.model.Sensor;
import de.infinit.emp.service.SensorService;
import spark.Request;
import spark.Response;

public class SensorController {
	private static final Gson GSON = new Gson();
	private SimulatorConfig config;
	private SensorService sensorService;
	
	public SensorController() throws IOException, SQLException {
		config = ConfigCache.getOrCreate(SimulatorConfig.class);
		sensorService = new SensorService();
	}

	private Map<String, Object> status(String value) {
		Map<String, Object> r = new HashMap<>();
		r.put("status", value);
		return r;
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
		if (sensor.getCode() == null) {
			return status("wrong-code");
		}
		if (!Pattern.matches(config.deviceCodePattern(), sensor.getCode())) {
			return status("wrong-code");
		}
		if (sensorService.findByCode(sensor.getCode()) != null) {
			return status("´duplicate-code");
		}
		if (sensorService.create(sensor) != 1) {
			return status("fail");
		}
		return result("status", "ok", "uuid", sensor.getUuid());
	}

	public Map<String, Object> get(Request request, Response response) throws IOException, SQLException {
		String uuid = request.params(":uuid");
		Sensor sensor = sensorService.findByUuid(uuid);
		if (sensor == null) {
			return status("fail");
		}
		return status("ok");
	}

	public Map<String, Object> delete(Request request, Response response) throws IOException, SQLException {
		String uuid = request.params(":uuid");
		if (sensorService.deleteByUuid(uuid) == 1) {
			return status("ok");
		}
		return status("fail");
	}
}
