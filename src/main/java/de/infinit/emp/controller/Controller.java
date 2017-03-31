package de.infinit.emp.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.aeonbits.owner.ConfigCache;

import com.google.gson.Gson;

import de.infinit.emp.ApplicationConfig;
import de.infinit.emp.Status;
import de.infinit.emp.domain.Session;
import spark.Request;
import spark.Response;

public class Controller {
	static final Logger log = Logger.getLogger(UserController.class.getName());
	static ApplicationConfig config = ConfigCache.getOrCreate(ApplicationConfig.class);
	static Gson gson = new Gson();

	protected static Map<String, Object> status(String value) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", value);
		return map;
	}

	protected static Map<String, Object> result(Object... keyValuePairs) {
		Map<String, Object> map = status(Status.OK);
		for (int i = 0; i < keyValuePairs.length; i = i + 2) {
			map.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
		}
		return map;
	}

	protected static <U> U decode(String jsonString, Class<U> to) {
		return gson.fromJson(jsonString, to);
	}

	protected static <T, U> U convert(T from, Class<U> to) {
		String jsonString = gson.toJson(from);
		return gson.fromJson(jsonString, to);
	}

	protected static boolean isProxySession(Request request) {
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		return session.getUserUuid() != null;
	}
	
	protected static boolean isPartnerSession(Request request) {
		return !isProxySession(request);
	}
	
	public static String notFound(Request request, Response response) {
		log.severe("route not found");
		return gson.toJson(status(Status.FAIL));
	}
	
	public static Map<String, Object> notImplemented(Request request, Response response) {
		return status(Status.NOT_IMPLEMENTED);
	}
}
