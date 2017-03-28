package de.infinit.emp.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.infinit.emp.model.User;

public class UserService extends Service {
	static final Logger LOG = Logger.getLogger(UserService.class.getName());
	final ConnectionSource connectionSource;
	final Dao<User, String> userDao;

	public UserService() throws IOException, SQLException {
		connectionSource = Database.getConnectionSource();
		userDao = DaoManager.createDao(connectionSource, User.class);
		TableUtils.createTableIfNotExists(connectionSource, User.class);
	}

	public User create(User user) {
		return create(userDao, user);
	}

	public User update(User user) {
		return update(userDao, user);
	}

	public User findByVerification(String verification) {
		try {
			return userDao.queryBuilder().where().eq("verification", verification).queryForFirst();
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return null;
	}
}
