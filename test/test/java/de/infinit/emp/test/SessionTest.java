package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.aeonbits.owner.ConfigCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.infinit.emp.Application;
import de.infinit.emp.ApplicationConfig;
import de.infinit.emp.test.utils.RestClient;
import de.infinit.emp.utils.Json;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SessionTest {
	static String initialURL;
	static String partnerSid;
	static String partnerServer;
	static String userSid;
	static String userServer;
	static String userUuid;

	@BeforeClass
	public static void setUp() throws IOException, SQLException {
		ApplicationConfig config = ConfigCache.getOrCreate(ApplicationConfig.class);
		initialURL = "http://localhost:" + config.port();
		Application.main(null);
		Spark.awaitInitialization();
	}

	@AfterClass
	public static void tearDown() throws SQLException {
		Spark.stop();
	}

	@Test
	public void testA_Get_NonAuthorized_Session() throws IOException {
		RestClient.Response res = RestClient.GET("/api/session", null, initialURL);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
		assertNotNull(res.body.get("sid"));
		assertNotNull(res.body.get("server"));
		partnerSid = (String) res.body.get("sid");
		partnerServer = (String) res.body.get("server");
	}

	@Test
	public void testB_Login_As_Partner() throws IOException {
		Map<String, Object> req = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz");
		RestClient.Response res = RestClient.POST("/api/session", req, partnerSid, partnerServer);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
	
	@Test
	public void testC_Create_User_Account() throws IOException {
		Map<String, Object> body = Json.obj("info", Json.obj("companyId", Json.arr("12345")));
		RestClient.Response resp = RestClient.POST("/api/signup/verification", body, partnerSid, partnerServer);
		assertEquals(200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		assertNotNull(resp.body.get("uuid"));
		assertNotNull(resp.body.get("verification"));
		userUuid = (String) resp.body.get("uuid");
		String verification = (String) resp.body.get("verification");
		body = Json.obj("email", "max.mustermann@mail.de", "firstname", "Max", "lastname", "Mustermann");
		body.put("verification", verification);
		resp = RestClient.POST("/api/signup/user", body, partnerSid, partnerServer);
		assertEquals(200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}

	@Test
	public void testD_Login_As_User() throws IOException {
		RestClient.Response res = RestClient.GET("/api/session", null, initialURL);
		assertEquals(200, res.status);
		userSid = (String) res.body.get("sid");
		userServer = (String) res.body.get("server");
		Map<String, Object> req =
				Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz", "user", userUuid);
		res = RestClient.POST("/api/session", req, userSid, userServer);
		assertEquals(200, res.status);
	}

	@Test
	public void testE_Logout_From_User_Session() throws IOException {
		RestClient.Response res = RestClient.DELETE("/api/session", userSid, userServer);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}

	@Test
	public void testF_Logout_From_Partner_Session() throws IOException {
		RestClient.Response res = RestClient.DELETE("/api/session", partnerSid, partnerServer);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
}
