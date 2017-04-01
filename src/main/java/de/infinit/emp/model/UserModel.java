package de.infinit.emp.model;

import de.infinit.emp.domain.User;

public class UserModel extends Model<User, String> {
	private static UserModel instance = null;

	private UserModel() {
		super(User.class);
	}

	public static UserModel instance() {
		if (instance == null) {
			instance = new UserModel();
		}
		return instance;
	}
}
