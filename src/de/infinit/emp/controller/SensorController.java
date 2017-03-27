package de.infinit.emp.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.aeonbits.owner.ConfigCache;

import com.google.gson.Gson;

import de.infinit.emp.SimulatorConfig;
import de.infinit.emp.Status;
import de.infinit.emp.model.Sensor;
import de.infinit.emp.service.SensorService;
import spark.Request;
import spark.Response;

public class SensorController extends Controller {
	static final Gson GSON = new Gson();
	final SimulatorConfig config;
	final SensorService sensorService;

	public SensorController() throws IOException, SQLException {
		config = ConfigCache.getOrCreate(SimulatorConfig.class);
		sensorService = new SensorService();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> post(Request request, Response response) throws IOException, SQLException {
		Map<String, Object> req = GSON.fromJson(request.body(), HashMap.class);
		String code = (String) req.get("code");
		String description = (String) req.get("description");
		if (code == null) {
			return status(Status.WRONG_CODE);
		}
		if (!Pattern.matches(config.devicePattern(), code)) {
			return status(Status.WRONG_CODE);
		}
		if (sensorService.findByCode(code) != null) {
			return status(Status.DUPLICATE_CODE);
		}
		Sensor sensor = new Sensor();
		sensor.setCode(code);
		sensor.setDescription(description);
		if (sensorService.create(sensor) == null) {
			return status(Status.FAIL);
		}
		return result("uuid", sensor.getUuid());
	}

	public Map<String, Object> get(Request request, Response response) throws IOException, SQLException {
		String uuid = request.params(":uuid");
		Sensor sensor = sensorService.findByUuid(uuid);
		if (sensor == null) {
			return status(Status.FAIL);
		}
		return status(Status.OK);
	}

	public Map<String, Object> delete(Request request, Response response) throws IOException, SQLException {
		String uuid = request.params(":uuid");
		if (sensorService.deleteByUuid(uuid) == 1) {
			return status(Status.OK);
		}
		return status(Status.FAIL);
	}
}
