package de.infinit.emp.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.aeonbits.owner.ConfigCache;

import de.infinit.emp.Globals;
import de.infinit.emp.SimulatorConfig;
import de.infinit.emp.Status;
import de.infinit.emp.model.Sensor;
import de.infinit.emp.service.SensorService;
import spark.Request;
import spark.Response;

public class SensorController extends Controller {
	final SimulatorConfig config;
	final SensorService sensorService;

	class Body {
		String code;
		String description;
	}
	
	public SensorController() throws IOException, SQLException {
		config = ConfigCache.getOrCreate(SimulatorConfig.class);
		sensorService = new SensorService();
	}

	public Map<String, Object> post(Request request, Response response) throws IOException, SQLException {
		Body body = Globals.GSON.fromJson(request.body(), Body.class);
		if (body.code == null) {
			return status(Status.WRONG_CODE);
		}
		if (!Pattern.matches(config.devicePattern(), body.code)) {
			return status(Status.WRONG_CODE);
		}
		if (sensorService.findByCode(body.code) != null) {
			return status(Status.DUPLICATE_CODE);
		}
		Sensor sensor = new Sensor();
		sensor.setCode(body.code);
		sensor.setDescription(body.description);
		sensor.setUuid(Globals.getUUID());
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
