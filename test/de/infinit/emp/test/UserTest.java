package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.infinit.emp.Application;
import spark.Spark;

public class UserTest extends WebClient {
	static String sid;
	static String server;
	static String uuid;

	private String addUser(String email) {
		List<String> companyIds = new ArrayList<>();
		companyIds.add("12345");
		Map<String, Object> obj = new HashMap<>();
		obj.put("companyId", companyIds);
		Map<String, Object> body = new HashMap<>();
		body.put("info", obj);
		WebResponse resp = post("/api/signup/verification", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		assertNotNull(resp.body.get("uuid"));
		assertNotNull(resp.body.get("verification"));
		String uuid = (String) resp.body.get("uuid");
		String verification = (String) resp.body.get("verification");
		body = new HashMap<>();
		body.put("email", email);
		body.put("verification", verification);
		resp = post("/api/signup/user", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		return uuid;
	}

	@BeforeClass
	public static void setUp() throws IOException, SQLException {
		Application.main(null);
	}

	@AfterClass
	public static void tearDown() throws SQLException {
		Spark.stop();
	}

	@Test // login
	public void testA() {
		WebResponse resp = get("/api/session", null, "http://localhost:4567");
		assertEquals(HttpStatus.OK_200, resp.status);
		sid = (String) resp.body.get("sid");
		server = (String) resp.body.get("server");
		Map<String, Object> body = new HashMap<>();
		body.put("partner", "brightone");
		body.put("key", "abcdefghijklmnopqrstuvwxyz");
		resp = post("/api/session", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
	}

	@Test // lists all partner related users
	public void testB() { // no users
		WebResponse resp = get("/api/partner/user", sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		// Type type = new TypeToken<List<String>>(){}.getType();
		// List<String> users = GSON.fromJson((String) resp.body.get("users"),
		// type);
		@SuppressWarnings("unchecked")
		List<String> users = (List<String>) resp.body.get("users");
		assertEquals(0, users.size());
	}

	@Test
	public void testC() {  // onw users
		addUser("max.mustermann@test.de");
		WebResponse resp = get("/api/partner/user", sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		@SuppressWarnings("unchecked")
		List<String> users = (List<String>) resp.body.get("users");
		assertEquals(1, users.size());
	}
	
	@Test
	public void testD() { // one users
		addUser("frieda.musterfrau@test.de");
		WebResponse resp = get("/api/partner/user", sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		@SuppressWarnings("unchecked")
		List<String> users = (List<String>) resp.body.get("users");
		assertEquals(2, users.size());
	}
	
	@Test
	public void testE() {
		String uuid = addUser("peter.pan@test.de");
		WebResponse resp = get("/api/partner/user/" + uuid, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) resp.body.get("user");
		assertEquals("peter.pan@test.de", user.get("email"));
	}
}
