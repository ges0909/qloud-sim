package de.infinit.emp.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import de.infinit.emp.utils.Json;

public class Utils {
	public static String sid;
	public static String server;

	public static void loginAsPartner() {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(200, res.status);
		sid = (String) res.body.get("sid");
		server = (String) res.body.get("server");
		Map<String, Object> req = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz");
		res = RestClient.POST("/api/session", req, sid, server);
		assertEquals(200, res.status);
	}

	public static void loginAsUser(String uuid) {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(200, res.status);
		sid = (String) res.body.get("sid");
		server = (String) res.body.get("server");
		Map<String, Object> req = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz", "user", uuid);
		res = RestClient.POST("/api/session", req, sid, server);
		assertEquals(200, res.status);
	}

	public static void logout() {
		RestClient.DELETE("/api/session", sid, server);
		sid = null;
		server = null;
	}

	public static String addUser(Object... keyValuePairs) {
		Map<String, Object> body = Json.obj("info", Json.obj("companyId", Json.arr("12345")));
		RestClient.Response resp = RestClient.POST("/api/signup/verification", body, sid, server);
		assertEquals(200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		assertNotNull(resp.body.get("uuid"));
		assertNotNull(resp.body.get("verification"));
		String uuid = (String) resp.body.get("uuid");
		String verification = (String) resp.body.get("verification");
		body = Json.obj(keyValuePairs);
		body.put("verification", verification);
		resp = RestClient.POST("/api/signup/user", body, sid, server);
		assertEquals(200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		return uuid;
	}
}
