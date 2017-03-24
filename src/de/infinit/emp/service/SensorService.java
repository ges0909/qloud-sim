package de.infinit.emp.service;

import java.util.UUID;

import de.infinit.emp.model.Sensor;

public class SensorService {
	public Sensor createSensor(Sensor sensor) {
		sensor.setUuid(UUID.randomUUID().toString());
		return sensor;
	}
}
