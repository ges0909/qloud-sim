package de.infinit.emp.test.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.utils.IOUtils;

public class RestClient {
	static final Gson GSON = new GsonBuilder().create();

	public static class Response {
		public final int status;
		public final Map<String, Object> body;

		@SuppressWarnings("unchecked")
		public Response(int status, String body) {
			this.status = status;
			this.body = GSON.fromJson(body, HashMap.class);
		}
	}

	static Response request(String method, String path, String input, String sid, String server) throws IOException {
		URL url = new URL(server + path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod(method);
		connection.setRequestProperty("Accept", "application/json");
		if (sid != null) {
			connection.setRequestProperty("Authorization", "Bearer " + sid);
		}
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
	}

	public static Response POST(String path, Map<String, Object> body, String sid, String server) throws IOException {
		return request("POST", path, GSON.toJson(body), sid, server);
	}

	public static Response GET(String path, String sid, String server) throws IOException {
		return request("GET", path, null, sid, server);
	}

	public static Response DELETE(String path, String sid, String server) throws IOException {
		return request("DELETE", path, null, sid, server);
	}
}
