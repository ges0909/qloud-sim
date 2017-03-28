package de.infinit.emp.controller;

import java.util.Map;

import de.infinit.emp.Status;
import spark.Request;
import spark.Response;

public class PartnerController extends Controller {

	public Map<String, Object> getUsers(Request request, Response response) {
		return status(Status.NOT_IMPLEMENTED);
	}
}
