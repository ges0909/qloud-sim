package de.infinit.emp.model;

import java.util.List;
import java.util.logging.Logger;

import de.infinit.emp.domain.User;

public class UserModel extends Model<User, String> {
	static final Logger log = Logger.getLogger(UserModel.class.getName());

	public UserModel()  {
		super(User.class);
	}

	public User create(User user) {
		return create(super.dao, user);
	}

	public User update(User user) {
		return update(super.dao, user);
	}
	
	public int delete(String uuid) {
		return delete(super.dao, uuid);
	}

	public User findById(String uuid) {
		return super.queryForId(dao, uuid);	
	}
	
	public List<User> findAll() {
		return super.queryForAll(dao);	
	}
	
	public User findByVerification(String verification) {
		return super.findByColumn(dao, "verification", verification);
	}

	public User findByEmail(String email) {
		return super.findByColumn(dao, "email", email);
	}
}
