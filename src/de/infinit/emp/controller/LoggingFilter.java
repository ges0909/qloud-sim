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
		before("/*", (req, res) -> printRequest(req));
		after("/*", (req, res) -> printResponse(res));
	}

	private void printRequest(Request req) {
		String method = req.requestMethod();
		String path = req.uri();
		String body = "";
		if (req.body() != null && ! req.body().isEmpty()) {
			body = GSON.toJson(PARSER.parse(req.body()).getAsJsonObject());
		}
		LOG.log(Level.INFO, "{0} {1} {2}", new Object[] { method, path, body });
	}

	private void printResponse(Response res) {
		int status = res.status();
		String body = GSON.toJson(PARSER.parse(res.body()).getAsJsonObject());
		LOG.log(Level.INFO, "{0} {1}", new Object[] { status, body });
	}
}
