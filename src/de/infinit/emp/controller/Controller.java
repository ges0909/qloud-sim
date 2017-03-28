package de.infinit.emp.controller;

import java.util.HashMap;
import java.util.Map;

import de.infinit.emp.Status;
import spark.Request;
import spark.Response;

public class Controller {

	protected Map<String, Object> status(String value) {
		Map<String, Object> r = new HashMap<>();
		r.put("status", value);
		return r;
	}

	protected Map<String, Object> result(Object... args) {
		Map<String, Object> r = status(Status.OK);
		for (int i = 0; i < args.length; i = i + 2) {
			r.put((String) args[i], args[i + 1]);
		}
		return r;
	}

	public static Map<String, Object> notImplemented(Request request, Response response) {
		Map<String, Object> r = new HashMap<>();
		r.put("status", Status.NOT_IMPLEMENTED);
		return r;
	}
}
