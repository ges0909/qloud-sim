package de.infinit.emp.filter;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;

public class LoggingFilter {
	static final Logger log = Logger.getLogger(LoggingFilter.class.getName());
	static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	static final JsonParser parser = new JsonParser();

	public static void logRequest(Request request, Response response) {
		String method = request.requestMethod();
		String path = request.uri();
		String body = "";
		if (request.body() != null && !request.body().isEmpty()) {
			body = gson.toJson(parser.parse(request.body()).getAsJsonObject());
		}
		log.log(Level.INFO, "{0} {1} {2}", new Object[] { method, path, body });
	}

	public static void logResponse(Request request, Response response) {
		int status = response.status();
		String body = "";
		if (response.body() != null && !response.body().isEmpty()) {
			body = gson.toJson(parser.parse(response.body()).getAsJsonObject());
		}
		log.log(Level.INFO, "{0} {1}", new Object[] { status, body });
	}
}
