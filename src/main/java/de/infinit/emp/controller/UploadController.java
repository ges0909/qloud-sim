package de.infinit.emp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class UploadController extends Controller {
	private static UploadController instance = null;
	static final Logger log = Logger.getLogger(UploadController.class.getName());

	private UploadController() {
		super();
	}

	public static UploadController instance() {
		if (instance == null) {
			instance = new UploadController();
		}
		return instance;
	}

	public ModelAndView provideUploadForm(Request request, Response response) {
		Map<String, Object> model = new HashMap<>();
		model.put("action", "Hochladen");
		return new ModelAndView(model, "upload.ftl");
	}

	public ModelAndView uploadFile(Request request, Response response) throws IOException, ServletException {
		MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/temp");
		request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
		List<String> lines;
		InputStream is = request.raw().getPart("uploaded_file").getInputStream();
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(is))) {
			lines = buffer.lines().collect(Collectors.toList());
		}
		for (String line : lines) {
			log.info(line);
		}
		return new ModelAndView(null, "result.ftl");
	}
}
