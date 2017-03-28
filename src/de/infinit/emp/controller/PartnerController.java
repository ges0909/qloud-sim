package de.infinit.emp.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.infinit.emp.Status;
import de.infinit.emp.model.Session;
import de.infinit.emp.model.User;
import de.infinit.emp.service.UserService;
import spark.Request;
import spark.Response;

public class PartnerController extends Controller {
	static final Logger LOG = Logger.getLogger(PartnerController.class.getName());
	final UserService userService;

	class GetUserInformationResponse {
		String email;
		String username;
		String firstname;
		String lastname;
		String displayname;
	}
	public PartnerController() throws IOException, SQLException {
		userService = new UserService();
	}

	public Map<String, Object> getPartnerRelatedUsers(Request request, Response response) {
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		if (!session.isPartnerSession()) {
			LOG.severe("request for partner sessions allowed only");
			return status(Status.NO_AUTH);	
		}
		List <User> users = userService.findAll();
		List <String> uuids = users
				.stream()
				.map(User::getUuid)
				.collect(Collectors.toList());
		return result("users", uuids);
	}
	
	public Map<String, Object> getUserInformation(Request request, Response response) {
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		if (!session.isPartnerSession()) {
			LOG.severe("request for partner sessions allowed only");
			return status(Status.NO_AUTH);	
		}
		String uuid = request.params(":uuid");
		User user = userService.findById(uuid);
		if (user == null) {
			return status(Status.WRONG_USER);
		}
		return result("user", convert(user, GetUserInformationResponse.class));
	}
}
