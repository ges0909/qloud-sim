package de.infinit.emp.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import de.infinit.emp.model.User;

public class UserService extends Service<User, String> {
	static final Logger LOG = Logger.getLogger(UserService.class.getName());

	public UserService() throws IOException, SQLException {
		super(User.class);
	}

	public User create(User user) {
		return create(super.dao, user);
	}

	public User update(User user) {
		return update(super.dao, user);
	}

	public List<User> findAll() {
		return super.queryForAll(dao);	
	}
	
	public User findByVerification(String verification) {
		try {
			return super.dao.queryBuilder()
					.where()
					.eq("verification", verification)
					.queryForFirst();
		} catch (SQLException e) {
			LOG.severe(e.toString());
		}
		return null;
	}
}
