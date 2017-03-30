package de.infinit.emp.controller;

import de.infinit.emp.Status;
import de.infinit.emp.domain.Session;
import de.infinit.emp.model.SessionModel;
import spark.Request;
import spark.Response;

public class SessionController extends Controller {
	public static final String QLOUD_SESSION = "session";
	static final SessionModel sessionModel = new SessionModel();

	class LoginRequest {
		String partner;
		String key;
		String user;
	}

	public static Object requestNonAuthorizedSession(Request request, Response response) {
		Session session = sessionModel.createSession(request.scheme(), request.host());
		return result("server", session.getServer(), "sid", session.getSid());
	}

	public static Object loginToPartnerOrProxySession(Request request, Response response) {
		LoginRequest req = gson.fromJson(request.body(), LoginRequest.class);
		if (req.partner == null || req.key == null) {
			return status(Status.WRONG_CREDENTIALS);
		}
		if (!(config.partner().equals(req.partner) && config.key().equals(req.key))) {
			return status(Status.WRONG_CREDENTIALS);
		}
		Session session = request.session().attribute(QLOUD_SESSION);
		session.setPartner(req.partner);
		session.setKey(req.key);
		session.setUserUuid(req.user);
		return status(Status.OK);
	}

	public static Object logoutFromSession(Request request, Response response) {
		Session session = request.session().attribute(QLOUD_SESSION);
		if (sessionModel.deleteSession(session) == null) {
			return status(Status.FAIL);
		}
		return status(Status.OK);
	}
}