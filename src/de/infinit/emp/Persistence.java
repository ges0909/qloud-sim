package de.infinit.emp;

import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class Persistence {

	public ConnectionSource getConnectionSource() throws SQLException {
		ConnectionSource connectionSource = new JdbcConnectionSource(Config.getString("db.url"));
		((JdbcConnectionSource) connectionSource).setUsername(Config.getString("db.username"));
		((JdbcConnectionSource) connectionSource).setPassword(Config.getString("db.password"));
		return connectionSource;
	}
}
