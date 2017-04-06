package de.infinit.emp.api.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.aeonbits.owner.ConfigCache;

import com.google.gson.Gson;

import de.infinit.emp.ApplicationConfig;
import de.infinit.emp.Status;
import de.infinit.emp.api.domain.Session;
import spark.Request;
import spark.Response;

public class Controller {
	private static Controller instance = null;
	static final Logger log = Logger.getLogger(UserController.class.getName());
	static final ApplicationConfig config = ConfigCache.getOrCreate(ApplicationConfig.class);
	static final Gson gson = new Gson();

	protected Controller() {
	}

	public static Controller instance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}

	protected Map<String, Object> status(String value) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", value);
		return map;
	}

	protected Map<String, Object> result(Object... keyValuePairs) {
		Map<String, Object> map = status(Status.OK);
		for (int i = 0; i < keyValuePairs.length; i = i + 2) {
			map.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
		}
		return map;
	}

	protected <U> U decode(String jsonString, Class<U> to) {
		return gson.fromJson(jsonString, to);
	}

	protected <T, U> U convert(T from, Class<U> to) {
		String jsonString = gson.toJson(from);
		return gson.fromJson(jsonString, to);
	}

	protected boolean isProxySession(Request request) {
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		return session.getUser() != null;
	}

	protected boolean isPartnerSession(Request request) {
		return !isProxySession(request);
	}

	public Object notImplemented(Request request, Response response) {
		return ok();
	}

	protected Object ok() {
		return status(Status.OK);
	}

	protected Object fail() {
		return status(Status.FAIL);
	}
	
	public Object notFound(Request request, Response response) {
		response.type("text/plain");
		return "NOT FOUND";
	}
	
	public Object internalServerError(Request request, Response response) {
		response.type("text/plain");
		return "INTERNAL SERVER ERROR";
	}
}
