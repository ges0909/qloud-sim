package de.infinit.emp.model;

import java.sql.SQLException;

import org.aeonbits.owner.ConfigCache;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.infinit.emp.ApplicationConfig;
import de.infinit.emp.domain.Capability;
import de.infinit.emp.domain.Invitation;
import de.infinit.emp.domain.Sensor;
import de.infinit.emp.domain.Session;
import de.infinit.emp.domain.User;

public class Persistence {
	static ConnectionSource cs = null;
	static ApplicationConfig config = ConfigCache.getOrCreate(ApplicationConfig.class);

	private Persistence() {
	}

	public static ConnectionSource getConnectionSource() throws SQLException {
		if (cs == null) {
			cs = new JdbcConnectionSource(config.url());
			((JdbcConnectionSource) cs).setUsername(config.username());
			((JdbcConnectionSource) cs).setPassword(config.password());
			createTabelsIfNotExists();
		}
		return cs;
	}

	private static void createTabelsIfNotExists() throws SQLException {
		TableUtils.createTableIfNotExists(cs, Session.class);
		TableUtils.createTableIfNotExists(cs, User.class);
		TableUtils.createTableIfNotExists(cs, Invitation.class);
		TableUtils.createTableIfNotExists(cs, Sensor.class);
		TableUtils.createTableIfNotExists(cs, Capability.class);
	}
}
