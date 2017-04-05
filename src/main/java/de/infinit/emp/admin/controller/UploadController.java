package de.infinit.emp.admin.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

import de.infinit.emp.api.controller.SensorController;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class UploadController {
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

	public ModelAndView displayUploadForm(Request request, Response response) {
		CommonModel model = new CommonModel(request);
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
			String[] parts = line.split(",");
			SensorController.instance().createSensor(parts[0], parts[1]);
		}
		CommonModel model = new CommonModel(request);
		model.put("message", "Erfolgreich hochgeladen.");
		return new ModelAndView(model, "message.ftl");
	}
}
