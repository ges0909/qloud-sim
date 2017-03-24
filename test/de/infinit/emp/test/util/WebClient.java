package de.infinit.emp.test.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import com.google.gson.Gson;

import spark.utils.IOUtils;

public class WebClient {

	protected Response request(String method, String path) {
		try {
			URL url = new URL("http://localhost:4567" + path);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setDoOutput(true);
			connection.connect();
			String body = IOUtils.toString(connection.getInputStream());
			return new Response(connection.getResponseCode(), body);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Sending request failed: " + e.getMessage());
			return null;
		}
	}

	protected static class Response {
		public final String body;
		public final int status;

		public Response(int status, String body) {
			this.status = status;
			this.body = body;
		}

		public Map<String, String> json() {
			return new Gson().fromJson(body, HashMap.class);
		}
	}
}
