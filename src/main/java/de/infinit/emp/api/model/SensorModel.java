package de.infinit.emp.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.domain.Tag;

public class SensorModel extends Model<Sensor, UUID> {
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
		List<Sensor> taggedSensors = new ArrayList<>();
		for (Sensor sensor : super.queryForAll()) {
			for (Tag tag : sensor.getTags()) {
				if (tag.equals(tagToSearch)) {
					taggedSensors.add(sensor);
					break;
				}
			}
		}
		return taggedSensors;
	}
}
