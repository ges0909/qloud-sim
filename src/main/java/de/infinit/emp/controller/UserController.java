package de.infinit.emp.controller;

import java.util.logging.Logger;

import de.infinit.emp.Status;
import de.infinit.emp.entity.Session;
import de.infinit.emp.entity.User;
import de.infinit.emp.model.UserModel;
import spark.Request;
import spark.Response;

public class UserController extends Controller {
	static final Logger log = Logger.getLogger(UserController.class.getName());
	static UserModel userModel = new UserModel();

	class GetUserResponse {
		String uuid;
		String username;
		String firstname;
		String lastname;
		String display_name;
		String email;
		String partner;
		String tag_all;
	}

	public static Object getUser(Request request, Response response) {
		if (isPartnerSession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User user = userModel.findById(session.getUserUuid());
		if (user == null) {
			return status(Status.FAIL);
		}
		GetUserResponse resp = convert(user, GetUserResponse.class);
		resp.partner = config.partner();
		resp.tag_all = "";
		return result("user", resp);
	}

}
