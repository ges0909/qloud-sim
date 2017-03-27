package de.infinit.emp.service;

import java.sql.SQLException;

import org.aeonbits.owner.ConfigCache;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import de.infinit.emp.SimulatorConfig;

public class Database {
	static ConnectionSource connectionSource = null;

	private Database() {
	}

	public static ConnectionSource getConnectionSource() throws SQLException {
		if (connectionSource == null) {
			SimulatorConfig config = ConfigCache.getOrCreate(SimulatorConfig.class);
			connectionSource = new JdbcConnectionSource(config.url());
			((JdbcConnectionSource) connectionSource).setUsername(config.username());
			((JdbcConnectionSource) connectionSource).setPassword(config.password());
		}
		return connectionSource;
	}
}
