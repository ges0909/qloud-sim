package de.infinit.emp.model;

import java.sql.SQLException;

import org.aeonbits.owner.ConfigCache;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import de.infinit.emp.ApplicationConfig;

public class Database {
	static ConnectionSource cs = null;

	private Database() {
	}
	
	public static ConnectionSource getConnectionSource() throws SQLException {
		if (cs == null) {
			ApplicationConfig config = ConfigCache.getOrCreate(ApplicationConfig.class);
			cs = new JdbcConnectionSource(config.url());
			((JdbcConnectionSource) cs).setUsername(config.username());
			((JdbcConnectionSource) cs).setPassword(config.password());
		}
		return cs;
	}
}
