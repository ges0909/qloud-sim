package de.infinit.emp.api.controller;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.infinit.emp.Status;
import de.infinit.emp.api.domain.Session;
import de.infinit.emp.api.domain.User;
import de.infinit.emp.api.model.InvitationModel;
import de.infinit.emp.api.model.SensorModel;
import de.infinit.emp.api.model.UserModel;
import spark.Request;
import spark.Response;

public class PartnerController extends Controller {
	private static PartnerController instance = null;
	static final Logger log = Logger.getLogger(PartnerController.class.getName());
	final UserModel userModel = UserModel.instance();
	final InvitationModel invitationModel = InvitationModel.instance();
	final SensorModel sensorModel = SensorModel.instance();

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
		Boolean disabled;
		Boolean deleted;
	}

	public Object getAccounts(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();		
		if (user != null) {
			return status(Status.NO_AUTH);
		}
		List<User> users = userModel.queryForAll();
		List<String> uuids = users.stream().map(u -> u.getUuid().toString()).collect(Collectors.toList());
		return result("users", uuids);
	}

	public Object getAccount(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();		
		if (user != null) {
			return status(Status.NO_AUTH);
		}
		UUID uuid = UUID.fromString(request.params(":uuid"));
		user = userModel.queryForId(uuid);
		if (user == null) {
			return status(Status.WRONG_USER);
		}
		return result("user", convert(user, UserDataResponse.class));
	}

	public Object deleteAccount(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();		
		if (user != null) {
			return status(Status.NO_AUTH);
		}
		DeleteUserRequest req = decode(request.body(), DeleteUserRequest.class);
		if (req.deleted == null) {
			return status(Status.NOT_IMPLEMENTED);
		}
		if (!req.deleted) {
			return ok();
		}
		UUID uuid = UUID.fromString(request.params(":uuid"));
		user = userModel.queryForId(uuid);
		if (user == null) {
			return status(Status.WRONG_USER);
		}
		if (userModel.delete(user) != 1) { // incl. sensors assigned to user and pending invitations
			return fail();
		}
		return ok();
	}
}
