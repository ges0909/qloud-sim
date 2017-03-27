package de.infinit.emp.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.aeonbits.owner.ConfigCache;

import com.google.gson.Gson;

import de.infinit.emp.SimulatorConfig;
import de.infinit.emp.Status;
import de.infinit.emp.model.Session;
import de.infinit.emp.service.SessionService;
import spark.Request;
import spark.Response;

public class SessionController extends Controller {
	public static final String QLOUD_SESSION = "session";
	static final Gson GSON = new Gson();
	final SimulatorConfig config;
	final SessionService sessionService;

	public SessionController() {
		config = ConfigCache.getOrCreate(SimulatorConfig.class);
		sessionService = new SessionService();
	}

	public Map<String, Object> get(Request request, Response response) {
		Session session = sessionService.createSession(request.scheme(), request.host());
		return result("server", session.getServer(), "sid", session.getSid());
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> post(Request request, Response response) {
		Map<String, Object> req = GSON.fromJson(request.body(), HashMap.class);
		String partner = (String) req.get("partner");
		String key = (String) req.get("key");
		String user = (String) req.get("user");
		if (partner == null) {
			return status(Status.WRONG_CREDENTIALS);
		}
		if (key == null) {
			return status(Status.WRONG_CREDENTIALS);
		}
		if (!config.partner().equals(partner)) {
			return status(Status.WRONG_CREDENTIALS);
		}
		if (!config.key().equals(key)) {
			return status(Status.WRONG_CREDENTIALS);
		}
		if (user != null && !Pattern.matches(config.userPattern(), user)) {
			return status(Status.WRONG_USER);
		}
		Session session = request.session().attribute(QLOUD_SESSION);
		session.setPartner(partner);
		session.setKey(key);
		session.setUserUuid(user);
		return status(Status.OK);
	}

	public Map<String, Object> delete(Request request, Response response) {
		Session session = request.session().attribute(QLOUD_SESSION);
		if (sessionService.deleteSession(session) == null) {
			return status(Status.FAIL);
		}
		return status(Status.OK);
	}
}