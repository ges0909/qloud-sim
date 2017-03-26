package de.infinit.emp.controller;

import static spark.Spark.before;
import static spark.Spark.halt;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.http.HttpStatus;

import spark.Request;
import spark.Response;

public class AuthenticationFilter {
	private static final Logger LOG = Logger.getLogger(AuthenticationFilter.class.getName());

	public AuthenticationFilter() {
		before(this::authenticateRequest);
	}

	private void authenticateRequest(Request req, Response res) {
		// Bearer: 
		LOG.log(Level.INFO, "{0}", req.headers("Authentication"));
	    boolean authenticated = true;
	    if (!authenticated) {
	    	halt(HttpStatus.UNAUTHORIZED_401);
	    }
	}

}
