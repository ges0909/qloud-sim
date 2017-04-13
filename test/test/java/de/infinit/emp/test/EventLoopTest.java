package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
public class EventLoopTest {
	static String initialURL;
	static String partnerSid;
	static String partnerServer;
	static String userUuid;
	static String userSid;
	static String userServer;

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
	public void testA_Login_as_Partner() throws IOException {
		RestClient.Response res = RestClient.GET("/api/session", null, initialURL);
		assertEquals(200, res.status);
		partnerSid = (String) res.body.get("sid");
		partnerServer = (String) res.body.get("server");
		Map<String, Object> req = new HashMap<>();
		req.put("partner", "brightone");
		req.put("key", "abcdefghijklmnopqrstuvwxyz");
		res = RestClient.POST("/api/session", req, partnerSid, partnerServer);
		assertEquals(200, res.status);
	}

	@Test
	public void testB_Get_All_Partner_Related_Users() throws IOException {
		RestClient.Response res = RestClient.GET("/api/partner/user", partnerSid, partnerServer);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
		@SuppressWarnings("unchecked")
		List<String> users = (List<String>) res.body.get("users");
		userUuid = users.get(0);
	}

	@Test
	public void testC_Login_As_User() throws IOException {
		RestClient.Response res = RestClient.GET("/api/session", null, initialURL);
		assertEquals(200, res.status);
		userSid = (String) res.body.get("sid");
		userServer = (String) res.body.get("server");
		Map<String, Object> req = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz", "user",
				userUuid);
		res = RestClient.POST("/api/session", req, userSid, userServer);
		assertEquals(200, res.status);
	}

	@Test
	public void testH_Get_SessionEvents() throws IOException {
		RestClient.Response res = RestClient.GET("/api/event?timeout=5", userSid, userServer);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
}
