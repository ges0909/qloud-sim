package de.infinit.emp;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class Database {
	private static final Logger LOG = Logger.getLogger(Database.class.getName());
	private static final String PROPERTIES_FILE = "db.properties";

	private String url;
	private String username;
	private String password;

	public Database() {
		try {
			InputStream in = getClass().getResourceAsStream(PROPERTIES_FILE);
			Properties prop = new Properties();
			prop.load(in);
			this.url = prop.getProperty("db.url");
			this.username = prop.getProperty("db.username");
			this.password = prop.getProperty("db.password");
		} catch (IOException e) {
			LOG.severe("missing property file '" + PROPERTIES_FILE + "'");
			LOG.severe(e.toString());
		}
	}

	public ConnectionSource getConnectionSource() throws SQLException {
		ConnectionSource connectionSource = new JdbcConnectionSource(url);
		((JdbcConnectionSource) connectionSource).setUsername(username);
		((JdbcConnectionSource) connectionSource).setPassword(password);
		return connectionSource;
	}
}
