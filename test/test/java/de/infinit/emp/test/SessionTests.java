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
public class SessionTests {
	static String sid;
	static String server;

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
	public void testA_Get_NonAuthorized_Session() throws IOException {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(200, res.status);
		sid = (String) res.body.get("sid");
		server = (String) res.body.get("server");
		assertNotNull(sid);
		assertEquals("http://localhost:4567", server);
		assertEquals("ok", res.body.get("status"));
	}

	@Test
	public void testB_Login_As_Partner() throws IOException {
		Map<String, Object> req =
				Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz");
		RestClient.Response res = RestClient.POST("/api/session", req, sid, server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}

	@Test
	public void testC_Login_as_Proxy() throws IOException {
		Map<String, Object> req =
				Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz", "user", Uuid.next());
		RestClient.Response res = RestClient.POST("/api/session", req, sid, server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}

	@Test
	public void testD_Logout_From_Session() throws IOException {
		RestClient.Response res = RestClient.DELETE("/api/session", sid, server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
}
