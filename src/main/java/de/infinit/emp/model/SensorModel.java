package de.infinit.emp.model;

import java.sql.SQLException;
import java.util.logging.Logger;

import de.infinit.emp.domain.Sensor;

public class SensorModel extends Model<Sensor, String> {
	static final Logger log = Logger.getLogger(SensorModel.class.getName());

	public SensorModel() {
		super(Sensor.class);
	}

	public Sensor create(Sensor sensor) {
		return create(super.dao, sensor);
	}

	public Sensor update(Sensor sensor) {
		return update(super.dao, sensor);
	}

	public int deleteByUuid(String uuid) {
		return delete(super.dao, uuid);
	}

	public Sensor findByUuid(String uuid) {
		return queryForId(super.dao, uuid);
	}

	public Sensor findByCode(String code) {
		try {
			return super.dao.queryBuilder()
					.where()
					.eq("code", code)
					.queryForFirst();
		} catch (SQLException e) {
			log.severe(e.toString());
		}
		return null;
	}
}
