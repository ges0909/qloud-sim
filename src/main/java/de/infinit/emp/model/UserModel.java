package de.infinit.emp.model;

import de.infinit.emp.domain.User;

public class UserModel extends Model<User, String> {
	public UserModel()  {
		super(User.class);
	}
}
