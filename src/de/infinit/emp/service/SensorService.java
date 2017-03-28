package de.infinit.emp.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import de.infinit.emp.model.Sensor;

public class SensorService extends Service<Sensor, String> {
	static final Logger LOG = Logger.getLogger(SensorService.class.getName());

	public SensorService() throws IOException, SQLException {
		super(Sensor.class);
	}

	public Sensor create(Sensor sensor) {
		return create(super.dao, sensor);
	}

	public Sensor findByUuid(String uuid) {
		return queryForId(super.dao, uuid);
	}

	public int deleteByUuid(String uuid) {
		return deleteByUuid(super.dao, uuid);
	}

	public Sensor findByCode(String code) {
		try {
			return super.dao.queryBuilder()
					.where()
					.eq("code", code)
					.queryForFirst();
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return null;
	}
}
