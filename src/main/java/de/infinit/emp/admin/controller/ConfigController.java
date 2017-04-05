package de.infinit.emp.admin.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
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

	private boolean saved() {
		String fileName = System.getProperty("user.home") + "/.qloud-simulator.properties";
		try {
			File file = new File(fileName);
			FileOutputStream out = new FileOutputStream(file);
			config.store(out, "");
			return true;
		} catch (IOException e) {
			log.severe(e.toString());
		}
		return false;
	}

	public ModelAndView displayConfigurationDialog(Request request, Response response) {
		Map<String, String> properties = new TreeMap<>(); // sorted by keys
		config.fill(properties);
		Optional<Integer> option = properties.entrySet().stream().map(e -> e.getValue().length()).reduce(Integer::max);
		int max = option.isPresent() ? option.get() : 40 /* default size */;
		//
		CommonModel model = new CommonModel(request);
		model.put("title", "Konfiguration");
		model.put("max", max);
		model.put("properties", properties);
		model.put("max", max);
		return new ModelAndView(model, "config.ftl");
	}

	public ModelAndView saveConfiguration(Request request, Response response) {
		CommonModel model = new CommonModel(request);
		Map<String, String[]> map = request.queryMap().toMap();
		for (Map.Entry<String, String[]> e : map.entrySet()) {
			String firstValue = e.getValue()[0];
			if (firstValue != null && firstValue.isEmpty()) {
				model.put("message", "Kein Wert: " + e.getKey());
				return new ModelAndView(model, "message.ftl");
			}
		}
		for (Map.Entry<String, String[]> e : map.entrySet()) {
			config.setProperty(e.getKey(), e.getValue()[0]);
		}
		if (saved()) {
			model.put("message", "Erfolgreich konfiguriert.");
		} else {
			model.put("message", "Fehler.");
		}
		return new ModelAndView(model, "message.ftl");
	}
}
