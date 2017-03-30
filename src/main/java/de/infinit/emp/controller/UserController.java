package de.infinit.emp.controller;

import java.util.Map;
import java.util.logging.Logger;

import de.infinit.emp.Status;
import de.infinit.emp.Uuid;
import de.infinit.emp.domain.Session;
import de.infinit.emp.domain.User;
import de.infinit.emp.model.UserModel;
import de.infinit.emp.utils.Json;
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

	class UpdateUserRequest {
		String firstname;
		String lastname;
		String display_name;
		String password;
		String email;
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
		return result("user", resp);
	}

	public static Object updateUser(Request request, Response response) {
		if (isPartnerSession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User user = userModel.findById(session.getUserUuid());
		if (user == null) {
			return status(Status.FAIL);
		}
		User other = userModel.findByEmail(session.getUserUuid());
		if (other != null) {
			return status(Status.ALREADY_EXIST);
		}
		UpdateUserRequest req = decode(request.body(), UpdateUserRequest.class);
		user.setFirstName(req.firstname);
		user.setLastName(req.lastname);
		user.setDisplayName(req.display_name);
		user.setPassword(req.password);
		user.setEmail(req.email);
		if (userModel.update(user) == null) {
			return status(Status.FAIL);
		}
		return status(Status.OK);
	}

	public static Object getInvitationCodes(Request request, Response response) {
		log.warning("not implemented: get user's invitations");
		Map<String, Object> invitation = Json.obj(Uuid.get(),
				Json.obj("description", "Simulator generated description for invitations."));
		return result("invitation", invitation);
	}

	public static Object inviteUser(Request request, Response response) {
		log.warning("not implemented: modify user's invitations");
		return status(Status.OK);
	}

	public static Object acceptInvitation(Request request, Response response) {
		log.warning("not implemented: modify user's links");
		return status(Status.OK);
	}
}
