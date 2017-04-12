package de.infinit.emp.api.model;

import java.util.UUID;

import de.infinit.emp.api.domain.User;

public class UserModel extends Model<User, UUID> {
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

	public int delete(User user) {
		user.getInvitations().clear();
		return super.delete(user.getUuid());
	}
}
