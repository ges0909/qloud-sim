package de.infinit.emp.model;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import de.infinit.emp.entity.User;

public class UserModel extends Model<User, String> {
	static final Logger LOG = Logger.getLogger(UserModel.class.getName());

	public UserModel()  {
		super(User.class);
	}

	public User create(User user) {
		return create(super.dao, user);
	}

	public User update(User user) {
		return update(super.dao, user);
	}

	public User findById(String uuid) {
		return super.queryForId(dao, uuid);	
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
