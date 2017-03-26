package de.infinit.emp.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.infinit.emp.Persistence;
import de.infinit.emp.model.Sensor;

public class SensorService {
	private static final Logger LOG = Logger.getLogger(SensorService.class.getName());
	private final ConnectionSource connectionSource;
	private final Dao<Sensor, String> sensorDao;

	public SensorService() throws IOException, SQLException {
		Persistence db = new Persistence();
		this.connectionSource = db.getConnectionSource();
		this.sensorDao = DaoManager.createDao(connectionSource, Sensor.class);
		TableUtils.createTableIfNotExists(connectionSource, Sensor.class);
	}

	public int create(Sensor sensor) {
		sensor.setUuid(UUID.randomUUID().toString());
		try {
			return sensorDao.create(sensor);
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return 0;
	}

	public Sensor findByUuid(String uuid) {
		try {
			return sensorDao.queryForId(uuid);
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return null;
	}

	public int deleteByUuid(String uuid) {
		try {
			return sensorDao.deleteById(uuid);
		} catch (SQLException e) {
			LOG.severe(e.toString());
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
			LOG.severe(e.toString());
		}
		return null;
	}
}
