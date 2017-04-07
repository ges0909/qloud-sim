package de.infinit.emp.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.domain.Tag;

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

	public int delete(Sensor sensor) {
		sensor.getCapabilities().clear();
		return super.delete(sensor.getUuid());
	}

	public List<Sensor> queryForTaggedWith(Tag tagToSearch) {
		List<Sensor> sensors = new ArrayList<>();
		for (Sensor sensor : queryForAll()) {
			for (Tag tag : sensor.getTags()) {
				if (tag.equals(tagToSearch)) {
					sensors.add(sensor);
					break;
				}
			}
		}
		return sensors;
	}
}
