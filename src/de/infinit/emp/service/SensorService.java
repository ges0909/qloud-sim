package de.infinit.emp.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.infinit.emp.Database;
import de.infinit.emp.model.Sensor;

public class SensorService {
	private static final Logger LOG = Logger.getLogger(SensorService.class.getName());
	private final ConnectionSource connectionSource;
	private final Dao<Sensor, String> sensorDao;

	public SensorService() throws IOException, SQLException {
		Database db = new Database();
		this.connectionSource = db.getConnectionSource();
		this.sensorDao = DaoManager.createDao(connectionSource, Sensor.class);
		TableUtils.createTableIfNotExists(connectionSource, Sensor.class);
	}

	public Sensor create(Sensor sensor) {
		sensor.setUuid(UUID.randomUUID().toString());
		try {
			sensorDao.create(sensor);
		} catch (SQLException e) {
			LOG.severe(e.toString());
			return null;
		}
		return sensor;
	}

	public Sensor getByUuid(String uuid) {
		Sensor sensor;
		try {
			sensor = sensorDao.queryForId(uuid);
		} catch (SQLException e) {
			LOG.severe(e.toString());
			return null;
		}
		return sensor;
	}

	public int deleteByUuid(String uuid) {
		int rowCount = 0;
		try {
			rowCount = sensorDao.deleteById(uuid);
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return rowCount;
	}
}
