package de.infinit.emp.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.infinit.emp.model.Sensor;

public class SensorService extends Service {
	static final Logger LOG = Logger.getLogger(SensorService.class.getName());
	final ConnectionSource connectionSource;
	final Dao<Sensor, String> sensorDao;

	public SensorService() throws IOException, SQLException {
		connectionSource = Database.getConnectionSource();
		sensorDao = DaoManager.createDao(connectionSource, Sensor.class);
		TableUtils.createTableIfNotExists(connectionSource, Sensor.class);
	}

	public Sensor create(Sensor sensor) {
		return create(sensorDao, sensor);
	}

	public Sensor findByUuid(String uuid) {
		return queryForId(sensorDao, uuid);
	}

	public int deleteByUuid(String uuid) {
		return deleteByUuid(sensorDao, uuid);
	}

	public Sensor findByCode(String code) {
		try {
			return sensorDao.queryBuilder()
					.where()
					.eq("code", code)
					.queryForFirst();
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return null;
	}
}
