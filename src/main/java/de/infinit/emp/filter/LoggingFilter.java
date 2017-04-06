package de.infinit.emp.filter;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;

public class LoggingFilter {
	static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
	static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
	static final Logger log = Logger.getLogger(LoggingFilter.class.getName());
	static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	static final JsonParser parser = new JsonParser();

	public static void logRequest(Request request, Response response) {
		String body = "";
		if (request.contentType() != null && request.body() != null && !request.body().isEmpty()) {
			if (request.contentType().equals(CONTENT_TYPE_APPLICATION_JSON)) {
				body = gson.toJson(parser.parse(request.body()).getAsJsonObject());
			} else {
				body = request.body();
			}
		}
		log.log(Level.INFO, "{0} {1} {2}", new Object[] { request.requestMethod(), request.uri(), body });
	}

	public static void logResponse(Request request, Response response) {
		String body = "";
		if (response.type() != null && response.body() != null && !response.body().isEmpty()) {
			if (response.type().equals(CONTENT_TYPE_APPLICATION_JSON)) {
				body = gson.toJson(parser.parse(response.body()).getAsJsonObject());
			} else {
				body = request.body();
			}
		}
		log.log(Level.INFO, "{0} {1}", new Object[] { response.status(), body });
	}
}
