package de.infinit.emp.controller;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.infinit.emp.Status;
import de.infinit.emp.entity.Session;
import de.infinit.emp.entity.User;
import de.infinit.emp.model.UserModel;
import spark.Request;
import spark.Response;

public class PartnerController extends Controller {
	static final Logger log = Logger.getLogger(PartnerController.class.getName());
	static UserModel userModel = new UserModel();

	class UserDataResponse {
		String email;
		String username;
		String firstname;
		String lastname;
		String display_name;
	}

	class DeleteUserRequest {
		boolean deleted;
	}

	private static boolean isPartnerSession(Request request) {
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		if (!session.isPartnerSession()) {
			log.severe("request for partner sessions allowed only");
			return false;
		}
		return true;
	}

	public static Object getUsers(Request request, Response response) {
		if (!isPartnerSession(request)) {
			return status(Status.NO_AUTH);
		}
		List<User> users = userModel.findAll();
		List<String> uuids = users.stream().map(User::getUuid).collect(Collectors.toList());
		return result("users", uuids);
	}

	public static Object getUserData(Request request, Response response) {
		if (!isPartnerSession(request)) {
			return status(Status.NO_AUTH);
		}
		String uuid = request.params(":uuid");
		User user = userModel.findById(uuid);
		if (user == null) {
			return status(Status.WRONG_USER);
		}
		return result("user", convert(user, UserDataResponse.class));
	}

	public static Object deleteUser(Request request, Response response) {
		if (!isPartnerSession(request)) {
			return status(Status.NO_AUTH);
		}
		DeleteUserRequest req = decode(request.body(), DeleteUserRequest.class);
		if (req.deleted) {
			String uuid = request.params(":uuid");
			if (userModel.delete(uuid) == 0) {
				return status(Status.WRONG_USER);
			}
		}
		return status(Status.OK);
	}
}
