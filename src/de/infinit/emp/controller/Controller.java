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

	protected static Map<String, Object> result(Object... args) {
		Map<String, Object> map = status(Status.OK);
		for (int i = 0; i < args.length; i = i + 2) {
			map.put((String) args[i], args[i + 1]);
		}
		return map;
	}

	protected static <T, U> U convert(T from, Class<U> to) {
		String jsonString = gson.toJson(from);
		return gson.fromJson(jsonString, to);
	}

	public static Map<String, Object> notImplemented(Request request, Response response) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Status.NOT_IMPLEMENTED);
		return map;
	}
}
