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
import de.infinit.emp.test.utils.RestClient;
import de.infinit.emp.utils.Json;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTests {
	static String partnerSid;
	static String partnerServer;
	static String userSid;
	static String userServer;
	static String otherUserSid;
	static String otherUserServer;
	static String userUuid;
	static String otherUserUuid;
	static String invitationUuid;

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
	public void testA_Login_As_Partner() {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(200, res.status);
		partnerSid = (String) res.body.get("sid");
		partnerServer = (String) res.body.get("server");
		Map<String, Object> req = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz");
		res = RestClient.POST("/api/session", req, partnerSid, partnerServer);
		assertEquals(200, res.status);
	}

	@Test
	public void testB_Create_User_Account() {
		Map<String, Object> req = Json.obj("info", Json.obj("companyId", Json.arr("12345")));
		RestClient.Response res = RestClient.POST("/api/signup/verification", req, partnerSid, partnerServer);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
		assertNotNull(res.body.get("uuid"));
		assertNotNull(res.body.get("verification"));
		userUuid = (String) res.body.get("uuid");
		String verification = (String) res.body.get("verification");
		req = Json.obj("email", "max.mustermann@mail.de", "firstname", "Max", "lastname", "Mustermann");
		req.put("verification", verification);
		res = RestClient.POST("/api/signup/user", req, partnerSid, partnerServer);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}

	@Test
	public void testC_Login_As_User() {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(200, res.status);
		userSid = (String) res.body.get("sid");
		userServer = (String) res.body.get("server");
		Map<String, Object> req = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz", "user", userUuid);
		res = RestClient.POST("/api/session", req, userSid, userServer);
		assertEquals(200, res.status);
	}

	@Test
	public void testD_Get_User_Data() {
		RestClient.Response res = RestClient.GET("/api/user", userSid, userServer);
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
	public void testE_Update_User_Data() {
		Map<String, Object> req = Json.obj("email", "max.mustermann@mail.de", "firstname", "Peter", "lastname", "Pan");
		RestClient.Response res = RestClient.POST("/api/user", req, userSid, userServer);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}

	@Test
	public void testF_Add_Other_User() {
		Map<String, Object> body = Json.obj("info", Json.obj("companyId", Json.arr("12345")));
		RestClient.Response resp = RestClient.POST("/api/signup/verification", body, partnerSid, partnerServer);
		assertEquals(200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		assertNotNull(resp.body.get("uuid"));
		assertNotNull(resp.body.get("verification"));
		otherUserUuid = (String) resp.body.get("uuid");
		String verification = (String) resp.body.get("verification");
		body = Json.obj("email", "angela.merkel@mail.de");
		body.put("verification", verification);
		resp = RestClient.POST("/api/signup/user", body, partnerSid, partnerServer);
		assertEquals(200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}

	@Test
	public void testG_Invite_Other_User() {
		Map<String, Object> req = Json.obj("invite", Json.arr(otherUserUuid));
		RestClient.Response res = RestClient.POST("/api/user/invitation", req, userSid, userServer);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}

	@Test
	public void testH_Login_As_Other_User() {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(200, res.status);
		otherUserSid = (String) res.body.get("sid");
		otherUserServer = (String) res.body.get("server");
		Map<String, Object> req = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz", "user", otherUserUuid);
		res = RestClient.POST("/api/session", req, otherUserSid, otherUserServer);
		assertEquals(200, res.status);
	}

	@Test
	public void testI_Get_Other_User_Invitations() {
		RestClient.Response res = RestClient.GET("/api/user/invitation", otherUserSid, otherUserServer);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
		assertNotNull(res.body.get("invitation"));
		@SuppressWarnings("unchecked")
		Map<String, Object> invitations = (Map<String, Object>) res.body.get("invitation");
		invitationUuid = (String) invitations.keySet().toArray()[0];
	}

	@Test
	public void testJ_Get_Accept_Invitation() {
		Map<String, Object> req = Json.obj("invitation", Json.arr(invitationUuid));
		RestClient.Response res = RestClient.POST("/api/user/link", req, otherUserSid, otherUserServer);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
}
