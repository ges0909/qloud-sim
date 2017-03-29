package de.infinit.emp.model;

import java.sql.SQLException;

import org.aeonbits.owner.ConfigCache;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import de.infinit.emp.ApplicationConfig;

public class Database {
	static ConnectionSource connectionSource = null;

	private Database() {
	}

	public static ConnectionSource getConnectionSource() throws SQLException {
		if (connectionSource == null) {
			ApplicationConfig config = ConfigCache.getOrCreate(ApplicationConfig.class);
			connectionSource = new JdbcConnectionSource(config.url());
			((JdbcConnectionSource) connectionSource).setUsername(config.username());
			((JdbcConnectionSource) connectionSource).setPassword(config.password());
		}
		return connectionSource;
	}
}
