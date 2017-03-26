package de.infinit.emp.controller;

import static spark.Spark.after;
import static spark.Spark.before;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;

public class LoggingFilter {
	private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final JsonParser PARSER = new JsonParser();

	public LoggingFilter() {
		before(this::logRequest);
		after(this::logResponse);
	}

	private void logRequest(Request request, Response response) {
		String method = request.requestMethod();
		String path = request.uri();
		String body = "";
		if (request.body() != null && !request.body().isEmpty()) {
			body = GSON.toJson(PARSER.parse(request.body()).getAsJsonObject());
		}
		LOG.log(Level.INFO, "{0} {1} {2}", new Object[] { method, path, body });
	}

	private void logResponse(Request request, Response response) {
		int status = response.status();
		String body = "";
		if (response.body() != null && !response.body().isEmpty()) {
			body = GSON.toJson(PARSER.parse(response.body()).getAsJsonObject());
		}
		LOG.log(Level.INFO, "{0} {1}", new Object[] { status, body });
	}
}
