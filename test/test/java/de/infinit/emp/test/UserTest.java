package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.infinit.emp.Application;
import de.infinit.emp.Uuid;
import de.infinit.emp.test.utils.RestClient;
import de.infinit.emp.test.utils.Utils;
import de.infinit.emp.utils.Json;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest {
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

	@Test
	public void testA_Login_As_Partner_And_Add_User() {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(200, res.status);
		String sid = (String) res.body.get("sid");
		String server = (String) res.body.get("server");
		Map<String, Object> req = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz");
		res = RestClient.POST("/api/session", req, sid, server);
		assertEquals(200, res.status);
		uuid = Utils.addUser(sid, server, "email", "max.mustermann@mail.de", "firstname", "Max", "lastname", "Mustermann");
		RestClient.DELETE("/api/session", sid, server);
	}

	@Test
	public void testB_Login_As_Added_User() {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(200, res.status);
		sid = (String) res.body.get("sid");
		server = (String) res.body.get("server");
		Map<String, Object> body = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz", "user", uuid);
		res = RestClient.POST("/api/session", body, sid, server);
		assertEquals(200, res.status);
	}
	
	@Test
	public void testC_Get_User_Data_Of_LoggedIn_User() {
		RestClient.Response res = RestClient.GET("/api/user", sid, server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) res.body.get("user");
		assertEquals("max.mustermann@mail.de", user.get("email"));
		assertEquals("Max", user.get("firstname"));
		assertEquals("Mustermann", user.get("lastname"));
		assertNotNull(user.get("tag_all"));
	}

	@Test
	public void testD_Update_LoggedIn_User() {
		Map<String, Object> req = Json.obj("email", "max.mustermann@mail.de", "firstname", "Peter", "lastname", "Pan");
		RestClient.Response res = RestClient.POST("/api/user", req, sid, server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
	
	@Test
	public void testE_Get_User_Invitations() {
		RestClient.Response res = RestClient.GET("/api/user/invitation", sid, server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
		assertNotNull(res.body.get("invitation"));
	}
	
	@Test
	public void testF_Invite_User() {
		Map<String, Object> req = Json.obj("invite", Json.arr(Uuid.get()));
		RestClient.Response res = RestClient.POST("/api/user/invitation", req, sid, server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
	
	@Test
	public void testG_Get_Accept_Invitation() {
		Map<String, Object> req = Json.obj("invitation", Json.arr(Uuid.get()));
		RestClient.Response res = RestClient.POST("/api/user/link", req, sid, server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
}
