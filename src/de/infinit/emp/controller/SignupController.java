package de.infinit.emp.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import de.infinit.emp.Global;
import de.infinit.emp.Status;
import de.infinit.emp.model.User;
import de.infinit.emp.service.UserService;
import spark.Request;
import spark.Response;

public class SignupController extends Controller {
	final UserService userService;

	class ReserveUserAccountRequest {
		class Obj {
			List<String> companyId;
		}
		Obj info;
	}

	class AddUserAccountRequest {
		String email;
		String username;
		String firstname;
		String lastname;
		String displayname;
		String password;
		String verification;
	}

	public SignupController() throws IOException, SQLException {
		userService = new UserService();
	}

	public Map<String, Object> reserveUserAccount(Request request, Response response) {
		ReserveUserAccountRequest body = Global.GSON.fromJson(request.body(), ReserveUserAccountRequest.class);
		User user = new User();
		user.setUuid(Global.getUUID());
		user.setVerification(Global.getUUID());
		if (userService.create(user) == null) {
			return status(Status.FAIL);
		}
		return result("uuid", user.getUuid(), "verification", user.getVerification(), "info", body.info);
	}

	public Map<String, Object> addUserAccount(Request request, Response response) {
		AddUserAccountRequest body = Global.GSON.fromJson(request.body(), AddUserAccountRequest.class);
		User user = userService.findByVerification(body.verification);
		if (user == null) {
			return status(Status.UNKNOWN_VERIFICATION);
		}
		user.setDisplayName(body.displayname);
		user.setEmail(body.email);
		user.setFirstName(body.firstname);
		user.setLastName(body.lastname);
		user.setPassword(body.password);
		user.setUserName(body.username);
		if (userService.update(user) == null) {
			return status(Status.FAIL);
		}
		return status(Status.OK);
	}
}
