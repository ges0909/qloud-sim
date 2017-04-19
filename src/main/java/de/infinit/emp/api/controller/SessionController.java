package de.infinit.emp.api.controller;

import java.util.UUID;

import de.infinit.emp.Status;
import de.infinit.emp.api.domain.Session;
import de.infinit.emp.api.domain.User;
import de.infinit.emp.api.model.SessionModel;
import de.infinit.emp.api.model.UserModel;
import spark.Request;
import spark.Response;

public class SessionController extends Controller {
	public static final String SESSION = "QLOUD_SESSION";
	private static SessionController instance = null;
	final SessionModel sessionModel = SessionModel.instance();
	final UserModel userModel = UserModel.instance();

	private SessionController() {
		super();
	}

	public static SessionController instance() {
		if (instance == null) {
			instance = new SessionController();
		}
		return instance;
	}

	class LoginRequest {
		String partner;
		String key;
		String user;
	}

	public Object requestNonAuthorizedSession(Request request, Response response) {
		Session session = new Session();
		String server = request.scheme() + "://" + request.host();
		session.setServer(server);
		if (sessionModel.create(session) == null) {
			return fail();
		}
		return result("sid", session.getSid(), "server", server);
	}

	public Object loginToPartnerOrProxySession(Request request, Response response) {
		LoginRequest req = gson.fromJson(request.body(), LoginRequest.class);
		if (req.partner == null || req.key == null) {
			return status(Status.WRONG_CREDENTIALS);
		}
		if (!(config.partner().equals(req.partner) && config.key().equals(req.key))) {
			return status(Status.WRONG_CREDENTIALS);
		}
		Session session = request.session().attribute(SESSION);
		if (session == null) {
			return fail();
		}
		session.setPartner(req.partner);
		session.setKey(req.key);
		if (req.user != null) {
			User user = userModel.queryForId(UUID.fromString(req.user));
			if (user == null) {
				return status(Status.WRONG_USER);
			}
			session.setUser(user);
		}
		if (sessionModel.update(session) == null) {
			return fail();
		}
		return ok();
	}

	public Object logoutFromSession(Request request, Response response) {
		Session session = request.session().attribute(SESSION);
		if (session == null) {
			return fail();
		}
		if (sessionModel.delete(session.getSid()) != 1) {
			return fail();
		}
		return ok();
	}
}