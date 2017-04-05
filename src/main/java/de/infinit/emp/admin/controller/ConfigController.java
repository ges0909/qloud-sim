package de.infinit.emp.admin.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.aeonbits.owner.ConfigCache;

import de.infinit.emp.ApplicationConfig;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class ConfigController {
	private static ConfigController instance = null;
	static final Logger log = Logger.getLogger(ConfigController.class.getName());
	static final ApplicationConfig config = ConfigCache.getOrCreate(ApplicationConfig.class);

	private ConfigController() {
		super();
	}

	public static ConfigController instance() {
		if (instance == null) {
			instance = new ConfigController();
		}
		return instance;
	}
	
	public ModelAndView displayConfigForm(Request request, Response response)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, Object> model = new HashMap<>();
		model.put("configurl", request.scheme() + "://" + request.host() + "/config");
		model.put("uploadurl", request.scheme() + "://" + request.host() + "/upload");
		//
		Class<ApplicationConfig> aClass = ApplicationConfig.class;
		Method[] methods = aClass.getMethods();
		Annotation[] annotations = aClass.getAnnotations();
		for (Method method : methods) {
			log.info(method + "=" + method.invoke(config, null));
		}
		for (Annotation annotation : annotations) {
			log.info(annotation.toString());
		}
		//
		return new ModelAndView(model, "config.ftl");
	}
	
	public ModelAndView doConfiguration(Request request, Response response) {
		Map<String, Object> model = new HashMap<>();
		model.put("result", "Erfolgreich");
		return new ModelAndView(model, "result.ftl");
	}
}
