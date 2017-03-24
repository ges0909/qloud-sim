package de.infinit.emp.test;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.infinit.emp.Main;
import spark.Spark;
import spark.utils.IOUtils;

public class TestClient {

	private static final String SERVER = "http://localhost:4567";
	private static final Gson GSON = new GsonBuilder().create();

	protected static class Response {
		public final int status;
		public final Map<String, Object> body;

		public Response(int status, String body) {
			this.status = status;
			this.body = GSON.fromJson(body, HashMap.class);
		}
	}

	private Response request(String method, String path, String input) {
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
			return new Response(connection.getResponseCode(), output);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Sending request failed: " + e.getMessage());
			return null;
		}
	}

	public Response get(String path) {
		return request("GET", path, null);
	}

	public Response post(String path, Map<String, Object> body) {
		return request("POST", path, GSON.toJson(body));
	}
	
	@BeforeClass
	public static void setUp() {
		Main.main(null);
	}

	@AfterClass
	public static void tearDown() {
		Spark.stop();
	}
}
