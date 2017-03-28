package de.infinit.emp.controller;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import de.infinit.emp.Status;
import spark.Request;
import spark.Response;

public class Controller {
	protected Map<String, Object> status(String value) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", value);
		return map;
	}

	protected Map<String, Object> result(Object... args) {
		Map<String, Object> map = status(Status.OK);
		for (int i = 0; i < args.length; i = i + 2) {
			map.put((String) args[i], args[i + 1]);
		}
		return map;
	}

	protected <T, U> U convert(T from, Class<U> to) {
		Gson gson = new Gson();
		String jsonString = gson.toJson(from);
		return gson.fromJson(jsonString, to);
	}

	public static Map<String, Object> notImplemented(Request request, Response response) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Status.NOT_IMPLEMENTED);
		return map;
	}
}
