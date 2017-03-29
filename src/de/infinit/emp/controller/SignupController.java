package de.infinit.emp.controller;

import java.util.List;

import de.infinit.emp.Status;
import de.infinit.emp.Uuid;
import de.infinit.emp.entity.User;
import de.infinit.emp.model.UserModel;
import spark.Request;
import spark.Response;

public class SignupController extends Controller {
	static final UserModel userModel = new UserModel();
	
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
		String display_name;
		String password;
		String verification;
	}

	public static Object reserveUserAccount(Request request, Response response) {
		ReserveUserAccountRequest body = decode(request.body(), ReserveUserAccountRequest.class);
		User user = new User();
		user.setUuid(Uuid.get());
		user.setVerification(Uuid.get());
		if (userModel.create(user) == null) {
			return status(Status.FAIL);
		}
		return result("uuid", user.getUuid(), "verification", user.getVerification(), "info", body.info);
	}

	public static Object addUserAccount(Request request, Response response) {
		AddUserAccountRequest body = decode(request.body(), AddUserAccountRequest.class);
		User user = userModel.findByVerification(body.verification);
		if (user == null) {
			return status(Status.UNKNOWN_VERIFICATION);
		}
		user.setDisplayName(body.display_name);
		user.setEmail(body.email);
		user.setFirstName(body.firstname);
		user.setLastName(body.lastname);
		user.setPassword(body.password);
		user.setUserName(body.username);
		if (userModel.update(user) == null) {
			return status(Status.FAIL);
		}
		return status(Status.OK);
	}
}
