package de.infinit.emp.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import spark.Request;
import spark.Response;

public class UploadController extends Controller {
	public static Object provideUploadForm(Request request, Response response) {
		return "<form method='post' enctype='multipart/form-data'>"
				+ "  <input type='file' name='upload' accept='*'>"
				+ "  <button>Upload</button>"
				+ "</form>";
	}

	public static Object uploadFile(Request request, Response response) {
		MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
		request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
		try {
			Part uploadedFile = request.raw().getPart("uploadedFile");
			Path path = Paths.get("/tmp/meh");
			try (InputStream in = uploadedFile.getInputStream()) {
				Files.copy(in, path);
			}
		} catch (IOException | ServletException e) {
			e.printStackTrace();
		}
		//response.redirect("/");
		return "OK";
	}
}
