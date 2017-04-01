package de.infinit.emp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

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
	
	public Object provideUploadForm(Request request, Response response) {
		response.type("text/html");
		return "<form method='post' enctype='multipart/form-data'>" + "  <input type='file' name='uploaded_file'>"
				+ "  <button>Sensor upload</button>" + "</form>";
	}

	public Object uploadFile(Request request, Response response) throws IOException, ServletException {
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
		return "File uploaded";
	}
}
