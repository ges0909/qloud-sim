package de.infinit.emp.controller;

import java.util.List;
import java.util.Map;
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

	class GetUserInformationResponse {
		String email;
		String username;
		String firstname;
		String lastname;
		String displayname;
	}

	public static Map<String, Object> getPartnerRelatedUsers(Request request, Response response) {
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		if (!session.isPartnerSession()) {
			log.severe("request for partner sessions allowed only");
			return status(Status.NO_AUTH);
		}
		List<User> users = userModel.findAll();
		List<String> uuids = users.stream().map(User::getUuid).collect(Collectors.toList());
		return result("users", uuids);
	}

	public static Map<String, Object> getUserInformation(Request request, Response response) {
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		if (!session.isPartnerSession()) {
			log.severe("request for partner sessions allowed only");
			return status(Status.NO_AUTH);
		}
		String uuid = request.params(":uuid");
		User user = userModel.findById(uuid);
		if (user == null) {
			return status(Status.WRONG_USER);
		}
		return result("user", convert(user, GetUserInformationResponse.class));
	}
}
