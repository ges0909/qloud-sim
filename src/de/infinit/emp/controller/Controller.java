package de.infinit.emp.controller;

import java.util.HashMap;
import java.util.Map;

import org.aeonbits.owner.ConfigCache;

import com.google.gson.Gson;

import de.infinit.emp.ApplicationConfig;
import de.infinit.emp.Status;
import spark.Request;
import spark.Response;

public class Controller {
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

	public static String notFound(Request request, Response response) {
		return gson.toJson(status(Status.FAIL));
	}
	
	public static Map<String, Object> notImplemented(Request request, Response response) {
		return status(Status.FAIL);
	}
}
