package de.infinit.emp.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.infinit.emp.model.Sensor;

public class SensorService {
	static final Logger LOG = Logger.getLogger(SensorService.class.getName());
	final ConnectionSource connectionSource;
	final Dao<Sensor, String> sensorDao;

	public SensorService() throws IOException, SQLException {
		connectionSource = Database.getConnectionSource();
		sensorDao = DaoManager.createDao(connectionSource, Sensor.class);
		TableUtils.createTableIfNotExists(connectionSource, Sensor.class);
	}

	public int create(Sensor sensor) {
		sensor.setUuid(UUID.randomUUID().toString());
		try {
			return sensorDao.create(sensor);
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "sensor creation", e);
		}
		return 0;
	}

	public Sensor findByUuid(String uuid) {
		try {
			return sensorDao.queryForId(uuid);
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "sensor query", e);
		}
		return null;
	}

	public int deleteByUuid(String uuid) {
		try {
			return sensorDao.deleteById(uuid);
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "sensor delete", e);
		}
		return 0;
	}

	public Sensor findByCode(String code) {
		try {
			List<Sensor> list = sensorDao.queryBuilder().where().eq("code", code).query();
			if (list.size() == 1) {
				return list.get(0);
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "sensor query", e);
		}
		return null;
	}
}
