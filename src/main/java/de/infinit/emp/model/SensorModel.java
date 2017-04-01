package de.infinit.emp.model;

import de.infinit.emp.domain.Sensor;

public class SensorModel extends Model<Sensor, String> {
	private static SensorModel instance = null;

	private SensorModel() {
		super(Sensor.class);
	}

	public static SensorModel instance() {
		if (instance == null) {
			instance = new SensorModel();
		}
		return instance;
	}
}
