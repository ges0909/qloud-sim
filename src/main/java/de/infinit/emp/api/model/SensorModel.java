package de.infinit.emp.api.model;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.domain.Tag;

public class SensorModel extends Model<Sensor, UUID> {
	private static SensorModel instance = null;
	final TagSensorModel tagSensorModel = TagSensorModel.instance();

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

	public List<Sensor> queryForTaggedWith(Tag tag) throws SQLException {
		return tagSensorModel.findSensorsByTag(tag);
	}
}
