package de.infinit.emp.admin.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

import de.infinit.emp.api.controller.SensorController;
import de.infinit.emp.api.domain.User;
import de.infinit.emp.api.model.UserModel;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class UploadController {
	private static UploadController instance = null;
	static final Logger log = Logger.getLogger(UploadController.class.getName());
	static final SensorController sensorController = SensorController.instance();
	static final UserModel userModel = UserModel.instance();

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
		Model model = new Model(request);
		return new ModelAndView(model, "upload.ftlh");
	}

	public ModelAndView uploadFile(Request request, Response response) {
		String email;
		List<String> csvLines = null;
		
		MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/temp");
		request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

		try {
			// email
			InputStream is = request.raw().getPart("email").getInputStream();
			try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
				email = scanner.useDelimiter("\\A").next();
			}
			// csv file
			is = request.raw().getPart("uploadFile").getInputStream();
			try (BufferedReader buffer = new BufferedReader(new InputStreamReader(is))) {
				csvLines = buffer.lines().collect(Collectors.toList());
			}
		} catch (IOException | ServletException | NoSuchElementException e) {
			log.severe(e.toString());
			Model model = new Model(request, "Fehlende E-Mail-Adresse oder Dateilese-Fehler.");
			return new ModelAndView(model, "message.ftlh");
		}

		if (csvLines.isEmpty()) {
			Model model = new Model(request, "CVS-Datei ist leer.");
			return new ModelAndView(model, "message.ftlh");
		}

		User user = userModel.findFirstByColumn("email", email);
		if (user == null) {
			user = new User();
			user.setEmail(email);
			if (userModel.create(user) == null) {
				Model model = new Model(request, "Nutzer '" + email + "' konnte nicht angelegt werden.");
				return new ModelAndView(model, "message.ftlh");
			}
		}

		for (String line : csvLines) {
			String[] parts = line.split(",");
			if (sensorController.createSensor(user, parts[0], parts[1]) == null) {
				Model model = new Model(request, "Sensor konnte nicht angelegt werden.");
				return new ModelAndView(model, "message.ftlh");
			}
		}

		Model model = new Model(request, "Erfolgreich hochgeladen.");
		return new ModelAndView(model, "message.ftlh");
	}
}
