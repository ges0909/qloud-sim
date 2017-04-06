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
import de.infinit.emp.utils.Json;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TagTests {
	static String partnerSid;
	static String partnerServer;
	static String userSid;
	static String userServer;
	static String userUuid;
	static String tagUuid;

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
	public void testA_Login_As_Partner() throws IOException {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(200, res.status);
		partnerSid = (String) res.body.get("sid");
		partnerServer = (String) res.body.get("server");
		Map<String, Object> req = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz");
		res = RestClient.POST("/api/session", req, partnerSid, partnerServer);
		assertEquals(200, res.status);
	}

	@Test
	public void testB_Create_User_Account() throws IOException {
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
	public void testC_Login_As_User() throws IOException {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(200, res.status);
		userSid = (String) res.body.get("sid");
		userServer = (String) res.body.get("server");
		Map<String, Object> req = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz", "user",
				userUuid);
		res = RestClient.POST("/api/session", req, userSid, userServer);
		assertEquals(200, res.status);
	}

	@Test
	public void testD_Create_Tag() throws IOException {
		Map<String, Object> req = Json.obj("label", "Simulator", "foreign_use", false, "policy",
				Json.obj(Uuid.next(), 1));
		RestClient.Response res = RestClient.POST("/api/tag", req, userSid, userServer);
		assertEquals(200, res.status);
		assertNotNull(res.body.get("uuid"));
		tagUuid = (String) res.body.get("uuid");
	}

	@Test
	public void testE_Update_Tag() throws IOException {
		Map<String, Object> req = Json.obj("label", "Label updated", "foreign_use", true);
		RestClient.Response res = RestClient.POST("/api/tag/" + tagUuid, req, userSid, userServer);
		assertEquals(200, res.status);
	}

	@Test
	public void testF_Get_Tag() throws IOException {
		RestClient.Response res = RestClient.GET("/api/tag/" + tagUuid, userSid, userServer);
		assertEquals(200, res.status);
	}

	@Test
	public void testG_Get_Tags() throws IOException {
		RestClient.Response res = RestClient.GET("/api/tag", userSid, userServer);
		assertEquals(200, res.status);
	}

	@Test
	public void testH_Delete_Tag() throws IOException {
		RestClient.Response res = RestClient.DELETE("/api/tag/" + tagUuid, userSid, userServer);
		assertEquals(200, res.status);
	}
}
