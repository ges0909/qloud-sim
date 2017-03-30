package de.infinit.emp.model;

import java.util.logging.Logger;

import de.infinit.emp.domain.User2;
import io.ebean.Ebean;
import io.ebean.EbeanServer;

public class UserModel2 {
	static final Logger log = Logger.getLogger(UserModel2.class.getName());
	
	public User2 create(User2 user) {
		EbeanServer server = Ebean.getDefaultServer();
		server.save(user);
		return user;
	}

	public User2 update(User2 user) {
		Ebean.update(user);
		return user;
	}
	
//	public int delete(String uuid) {
//		return delete(super.dao, uuid);
//	}

//	public User findById(String uuid) {
//		return super.queryForId(dao, uuid);	
//	}
//	
//	public List<User> findAll() {
//		return super.queryForAll(dao);	
//	}
	
	public User2 findByVerification(String verification) {
		return Ebean.find(User2.class)
				.where()
				.eq("verification", verification)
				.findUnique();
	}
}
