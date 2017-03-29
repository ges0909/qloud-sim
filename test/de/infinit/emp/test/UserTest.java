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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.infinit.emp.Application;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest extends WebClient {
	static String sid;
	static String server;
	static String uuid;

	private List<Object> jsonArr(Object... values) {
		List<Object> list = new ArrayList<>();
		for (Object value : values) {
			list.add(value);
		}
		return list;
	}

	private Map<String, Object> jsonObj(Object... keyValuePairs) {
		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < keyValuePairs.length; i = i + 2) {
			map.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
		}
		return map;
	}

	private String addUser(Object... keyValuePairs) {
		Map<String, Object> body = jsonObj("info", jsonObj("companyId", jsonArr("12345")));
		WebResponse resp = post("/api/signup/verification", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		assertNotNull(resp.body.get("uuid"));
		assertNotNull(resp.body.get("verification"));
		String uuid = (String) resp.body.get("uuid");
		String verification = (String) resp.body.get("verification");
		body = jsonObj(keyValuePairs);
		body.put("verification", verification);
		resp = post("/api/signup/user", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		return uuid;
	}

	@BeforeClass
	public static void setUp() throws IOException, SQLException {
		Application.main(null);
		Spark.awaitInitialization();
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
		Map<String, Object> body = jsonObj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz");
		resp = post("/api/session", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
	}

	@Test // list all partner related users: no users in database
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

	@Test // list all partner related users: one user in database
	public void testC() {
		addUser("email", "max.mustermann@test.de");
		WebResponse resp = get("/api/partner/user", sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		@SuppressWarnings("unchecked")
		List<String> users = (List<String>) resp.body.get("users");
		assertEquals(1, users.size());
	}

	@Test // list all partner related users: two users in database
	public void testD() { // one users
		addUser("email", "frieda.musterfrau@test.de");
		WebResponse resp = get("/api/partner/user", sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		@SuppressWarnings("unchecked")
		List<String> users = (List<String>) resp.body.get("users");
		assertEquals(2, users.size());
	}

	@Test // get user data
	public void testE() {
		String uuid = addUser("email", "peter.pan@test.de", "firstname", "Peter", "lastname", "Pan", "display_name", "Peter Pan");
		WebResponse resp = get("/api/partner/user/" + uuid, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) resp.body.get("user");
		assertEquals("peter.pan@test.de", user.get("email"));
		assertEquals("Peter", user.get("firstname"));
		assertEquals("Pan", user.get("lastname"));
		assertEquals("Peter Pan", user.get("display_name"));
	}
	
	@Test // delete user
	public void testF() {
		String uuid = addUser("email", "angela.merkel@test.de");
		WebResponse resp = post("/api/partner/user/" + uuid, jsonObj("deleted", true), sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}	
}
