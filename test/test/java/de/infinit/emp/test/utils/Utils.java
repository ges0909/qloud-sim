package de.infinit.emp.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;

import de.infinit.emp.utils.Json;

public class Utils {
	public static String addUser(String sid, String server, Object... keyValuePairs) {
		Map<String, Object> body = Json.obj("info", Json.obj("companyId", Json.arr("12345")));
		RestClient.Response resp = RestClient.POST("/api/signup/verification", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		assertNotNull(resp.body.get("uuid"));
		assertNotNull(resp.body.get("verification"));
		String uuid = (String) resp.body.get("uuid");
		String verification = (String) resp.body.get("verification");
		body = Json.obj(keyValuePairs);
		body.put("verification", verification);
		resp = RestClient.POST("/api/signup/user", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		return uuid;
	}
}
