package de.infinit.emp;

import java.sql.SQLException;

import org.aeonbits.owner.ConfigCache;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class Persistence {
	SimulatorConfig config = ConfigCache.getOrCreate(SimulatorConfig.class);

	public ConnectionSource getConnectionSource() throws SQLException {
		ConnectionSource connectionSource = new JdbcConnectionSource(config.url());
		((JdbcConnectionSource) connectionSource).setUsername(config.username());
		((JdbcConnectionSource) connectionSource).setPassword(config.password());
		return connectionSource;
	}
}
