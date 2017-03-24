package de.infinit.emp.rest;

import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import de.infinit.emp.JsonTransformer;
import spark.Request;
import spark.Response;
import spark.Route;

public class SessionController {

	private static final String SESSSION_PATH = "/api/session";

	public SessionController(final SessionService service) {

		get(SESSSION_PATH, (req, res) -> service.newSession(req), new JsonTransformer());

		post(SESSSION_PATH, new Route() {
			@Override
			public Object handle(Request request, Response response) {
				return service.newSession(request);
			}
		});

		delete(SESSSION_PATH, new Route() {
			@Override
			public Object handle(Request request, Response response) {
				return service.newSession(request);
			}
		});

		after(SESSSION_PATH, (req, res) -> res.type("application/json"));
	}
}