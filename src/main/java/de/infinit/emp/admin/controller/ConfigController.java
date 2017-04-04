package de.infinit.emp.admin.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class ConfigController {
	private static ConfigController instance = null;
	static final Logger log = Logger.getLogger(ConfigController.class.getName());

	private ConfigController() {
		super();
	}

	public static ConfigController instance() {
		if (instance == null) {
			instance = new ConfigController();
		}
		return instance;
	}
	
	public ModelAndView displayConfigForm(Request request, Response response) {
		Map<String, Object> model = new HashMap<>();
		model.put("configurl", request.scheme() + "://" + request.host() + "/config");
		model.put("uploadurl", request.scheme() + "://" + request.host() + "/upload");
		return new ModelAndView(model, "config.ftl");
	}
	
	public ModelAndView doConfiguration(Request request, Response response) {
		Map<String, Object> model = new HashMap<>();
		model.put("result", "Erfolgreich");
		return new ModelAndView(model, "result.ftl");
	}
}
