package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.infinit.emp.Application;
import de.infinit.emp.test.utils.Json;
import de.infinit.emp.test.utils.RestClient;
import de.infinit.emp.test.utils.Utils;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PartnerTest {
	static String sid;
	static String server;
	static String uuid;

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
		RestClient.Response resp = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(HttpStatus.OK_200, resp.status);
		sid = (String) resp.body.get("sid");
		server = (String) resp.body.get("server");
		Map<String, Object> body = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz");
		resp = RestClient.POST("/api/session", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
	}

	@Test // list all partner related users: no users in database
	public void testB() { // no users
		RestClient.Response resp = RestClient.GET("/api/partner/user", sid, server);
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
		Utils.addUser(sid, server, "email", "max.mustermann@test.de");
		RestClient.Response resp = RestClient.GET("/api/partner/user", sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		@SuppressWarnings("unchecked")
		List<String> users = (List<String>) resp.body.get("users");
		assertEquals(1, users.size());
	}

	@Test // list all partner related users: two users in database
	public void testD() { // one users
		Utils.addUser(sid, server, "email", "frieda.musterfrau@test.de");
		RestClient.Response resp = RestClient.GET("/api/partner/user", sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		@SuppressWarnings("unchecked")
		List<String> users = (List<String>) resp.body.get("users");
		assertEquals(2, users.size());
	}

	@Test // get user data
	public void testE() {
		String uuid = Utils.addUser(sid, server, "email", "peter.pan@test.de", "firstname", "Peter", "lastname", "Pan", "display_name", "Peter Pan");
		RestClient.Response resp = RestClient.GET("/api/partner/user/" + uuid, sid, server);
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
		String uuid = Utils.addUser(sid, server, "email", "angela.merkel@test.de");
		RestClient.Response resp = RestClient.POST("/api/partner/user/" + uuid, Json.obj("deleted", true), sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}	
}
