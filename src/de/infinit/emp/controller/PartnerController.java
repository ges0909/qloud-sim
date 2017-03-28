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

	public PartnerController() throws IOException, SQLException {
		userService = new UserService();
	}

	public Map<String, Object> getPartnerRelatedUsers(Request request, Response response) {
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		if (!session.isPartnerSession()) {
			LOG.severe("request allowed for partner sessions only");
			return status(Status.FAIL);	
		}
		List <User> users = userService.findAll();
		List <String> userUuids = users
				.stream()
				.map(User::getUuid)
				.collect(Collectors.toList());
		return result("users", userUuids);
	}
}
