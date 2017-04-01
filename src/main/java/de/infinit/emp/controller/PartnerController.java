package de.infinit.emp.controller;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.infinit.emp.Status;
import de.infinit.emp.domain.User;
import de.infinit.emp.model.InvitationModel;
import de.infinit.emp.model.SensorModel;
import de.infinit.emp.model.UserModel;
import spark.Request;
import spark.Response;

public class PartnerController extends Controller {
	private static PartnerController instance = null;
	static final Logger log = Logger.getLogger(PartnerController.class.getName());
	static UserModel userModel = new UserModel();
	static InvitationModel invitationModel = new InvitationModel();
	static SensorModel sensorModel = new SensorModel();

	private PartnerController() {
		super();
	}

	public static PartnerController instance() {
		if (instance == null) {
			instance = new PartnerController();
		}
		return instance;
	}

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

	public Object getUsers(Request request, Response response) {
		if (isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		List<User> users = userModel.queryForAll();
		List<String> uuids = users.stream().map(User::getUuid).collect(Collectors.toList());
		return result("users", uuids);
	}

	public Object getUserData(Request request, Response response) {
		if (isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		String uuid = request.params(":uuid");
		User user = userModel.queryForId(uuid);
		if (user == null) {
			return status(Status.WRONG_USER);
		}
		return result("user", convert(user, UserDataResponse.class));
	}

	public Object deleteUser(Request request, Response response) {
		if (isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		DeleteUserRequest req = decode(request.body(), DeleteUserRequest.class);
		if (req.deleted) {
			String uuid = request.params(":uuid");
			if (userModel.delete(uuid) == 0) {
				return status(Status.WRONG_USER);
			}
		}
		return ok();
	}
}
