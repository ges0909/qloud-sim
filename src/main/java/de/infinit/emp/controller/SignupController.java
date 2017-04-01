package de.infinit.emp.controller;

import java.util.List;

import de.infinit.emp.Status;
import de.infinit.emp.Uuid;
import de.infinit.emp.domain.User;
import de.infinit.emp.model.UserModel;
import spark.Request;
import spark.Response;

public class SignupController extends Controller {
	private static SignupController instance = null;
	static final UserModel userModel = new UserModel();
	
	private SignupController() {
		super();
	}

	public static SignupController instance() {
		if (instance == null) {
			instance = new SignupController();
		}
		return instance;
	}
	
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

	public Object reserveUserAccount(Request request, Response response) {
		ReserveUserAccountRequest body = decode(request.body(), ReserveUserAccountRequest.class);
		User user = new User();
		user.setUuid(Uuid.next());
		user.setVerification(Uuid.next());
		if (userModel.create(user) == null) {
			return fail();
		}
		return result("uuid", user.getUuid(), "verification", user.getVerification(), "info", body.info);
	}

	public Object addUserAccount(Request request, Response response) {
		AddUserAccountRequest req = decode(request.body(), AddUserAccountRequest.class);
		User user = userModel.findByColumn("verification", req.verification);
		if (user == null) {
			return status(Status.UNKNOWN_VERIFICATION);
		}
		user.setDisplayName(req.display_name);
		user.setEmail(req.email);
		user.setFirstName(req.firstname);
		user.setLastName(req.lastname);
		user.setPassword(req.password);
		user.setUserName(req.username);
		user.setTagAll(Uuid.next());
		if (userModel.update(user) == null) {
			return fail();
		}
		return ok();
	}
}
