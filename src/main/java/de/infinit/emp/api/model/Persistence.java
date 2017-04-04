package de.infinit.emp.api.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.aeonbits.owner.ConfigCache;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.infinit.emp.ApplicationConfig;
import de.infinit.emp.api.domain.Capability;
import de.infinit.emp.api.domain.Invitation;
import de.infinit.emp.api.domain.Policy;
import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.domain.Session;
import de.infinit.emp.api.domain.Tag;
import de.infinit.emp.api.domain.User;

public class Persistence {
	static final Logger log = Logger.getLogger(Persistence.class.getName());
	static ConnectionSource cs = null;
	static ApplicationConfig config = ConfigCache.getOrCreate(ApplicationConfig.class);

	private Persistence() {
	}

	public static ConnectionSource getConnectionSource() {
		if (cs == null) {
			try {
				cs = new JdbcConnectionSource(config.url());
				((JdbcConnectionSource) cs).setUsername(config.username());
				((JdbcConnectionSource) cs).setPassword(config.password());
				createTabelsIfNotExists();
			} catch (SQLException e) {
				log.severe(e.toString());
				System.exit(0);
			}
		}
		return cs;
	}

	public static void close() throws IOException {
		if (cs != null) {
			cs.close();
		}
	}

	private static void createTabelsIfNotExists() throws SQLException {
		TableUtils.createTableIfNotExists(cs, Session.class);
		TableUtils.createTableIfNotExists(cs, User.class);
		TableUtils.createTableIfNotExists(cs, Invitation.class);
		TableUtils.createTableIfNotExists(cs, Sensor.class);
		TableUtils.createTableIfNotExists(cs, Capability.class);
		TableUtils.createTableIfNotExists(cs, Tag.class);
		TableUtils.createTableIfNotExists(cs, Policy.class);
	}
	
}
