package de.infinit.emp.test;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.utils.IOUtils;

public class WebClient {

	private static final String SERVER = "http://localhost:4567";
	private static final Gson GSON = new GsonBuilder().create();
	
	protected static class WebResponse {
		public final int status;
		public final Map<String, Object> body;

		@SuppressWarnings("unchecked")
		public WebResponse(int status, String body) {
			this.status = status;
			this.body = GSON.fromJson(body, HashMap.class);
		}
	}

	private WebResponse request(String method, String path, String input) {
		try {
			URL url = new URL(SERVER + path);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod(method);
			connection.setRequestProperty("Accept", "application/json");
			if (input != null) {
				connection.setDoInput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
				writer.write(input);
				writer.flush();
			}
			connection.connect();
			String output = IOUtils.toString(connection.getInputStream());
			return new WebResponse(connection.getResponseCode(), output);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Sending request failed: " + e.getMessage());
			return null;
		}
	}

	public WebResponse post(String path, Map<String, Object> body) {
		return request("POST", path, GSON.toJson(body));
	}

	public WebResponse get(String path) {
		return request("GET", path, null);
	}

	public WebResponse delete(String path) {
		return request("DELETE", path, null);
	}
}
