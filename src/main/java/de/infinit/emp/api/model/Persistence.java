package de.infinit.emp.api.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.aeonbits.owner.ConfigCache;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import de.infinit.emp.ApplicationConfig;

public class Persistence {
	static final ApplicationConfig config = ConfigCache.getOrCreate(ApplicationConfig.class);
	static final Logger log = Logger.getLogger(Persistence.class.getName());
	static ConnectionSource cs = null;

	private Persistence() {
	}

	public static ConnectionSource getConnectionSource() {
		if (cs == null) {
			try {
				cs = new JdbcConnectionSource(config.url());
				((JdbcConnectionSource) cs).setUsername(config.username());
				((JdbcConnectionSource) cs).setPassword(config.password());
			} catch (SQLException e) {
				log.severe(e.toString());
				e.printStackTrace();
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
}
