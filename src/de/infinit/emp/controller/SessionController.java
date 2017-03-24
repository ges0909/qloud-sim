package de.infinit.emp.controller;

import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import de.infinit.emp.JsonTransformer;
import de.infinit.emp.service.SessionService;
import spark.Request;
import spark.Response;
import spark.Route;

public class SessionController extends LoggingFilter {

	private static final String PATH = "/api/session";
	private final SessionService sessionService = new SessionService();
	
	public SessionController() {

		get(PATH, (req, res) -> sessionService.newSession(req), new JsonTransformer());

		post(PATH, new Route() {
			@Override
			public Object handle(Request request, Response response) {
				return sessionService.newSession(request);
			}
		});

		delete(PATH, new Route() {
			@Override
			public Object handle(Request request, Response response) {
				return sessionService.newSession(request);
			}
		});

		after(PATH, (req, res) -> res.type("application/json"));
	}
}