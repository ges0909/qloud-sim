package de.infinit.emp.controller;

import static spark.Spark.after;
import static spark.Spark.before;

import java.util.logging.Logger;

public class LoggingFilter {
	private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

	public LoggingFilter() {
		before("/*", (req, res) -> LOG.info(req.requestMethod() + " " + req.body()));
		after("/*", (req, res) -> LOG.info(res.status() + " " + res.body()));
	}
}
