package de.infinit.emp.controller;

import de.infinit.emp.Status;
import de.infinit.emp.Uuid;
import de.infinit.emp.domain.Session;
import de.infinit.emp.model.SessionModel;
import spark.Request;
import spark.Response;

public class SessionController extends Controller {
	public static final String QLOUD_SESSION = "session";
	private static SessionController instance = null;
	final SessionModel sessionModel = SessionModel.instance();

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
		String sid = Uuid.next();
		String server = request.scheme() + "://" + request.host();
		session.setSid(sid);
		session.setServer(server);
		if (sessionModel.create(session) == null) {
			return fail();
		}
		return result("sid", sid, "server", server);
	}

	public Object loginToPartnerOrProxySession(Request request, Response response) {
		LoginRequest req = gson.fromJson(request.body(), LoginRequest.class);
		if (req.partner == null || req.key == null) {
			return status(Status.WRONG_CREDENTIALS);
		}
		if (!(config.partner().equals(req.partner) && config.key().equals(req.key))) {
			return status(Status.WRONG_CREDENTIALS);
		}
		Session session = request.session().attribute(QLOUD_SESSION);
		if (session == null) {
			return fail();
		}
		session.setPartner(req.partner);
		session.setKey(req.key);
		session.setUser(req.user);
		if (sessionModel.update(session) == null) {
			return fail();
		}
		return ok();
	}

	public Object logoutFromSession(Request request, Response response) {
		Session session = request.session().attribute(QLOUD_SESSION);
		if (session == null) {
			return fail();
		}
		if (sessionModel.delete(session.getSid()) != 1) {
			return fail();
		}
		return ok();
	}
}