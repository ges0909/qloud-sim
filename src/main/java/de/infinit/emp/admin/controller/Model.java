package de.infinit.emp.admin.controller;

import java.util.HashMap;

import spark.Request;

@SuppressWarnings("serial")
public class Model extends HashMap<String, Object> {
	public Model(Request request) {
		super();
		HashMap<String, Object> config = new HashMap<>();
		config.put("title", "Konfigurieren");
		config.put("url", "/config");
		HashMap<String, Object> upload = new HashMap<>();
		upload.put("title", "Hochladen");
		upload.put("url", "/upload");
		this.put("config", config);
		this.put("upload", upload);
	}

	public Model(Request request, String message) {
		this(request);
		this.put("message", message);
	}
}
