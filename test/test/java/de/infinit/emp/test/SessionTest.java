package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.infinit.emp.Application;
import de.infinit.emp.test.utils.RestClient;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SessionTest {
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

	@Test // get unauthorized session
	public void testA() {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(HttpStatus.OK_200, res.status);
		sid = (String) res.body.get("sid");
		server = (String) res.body.get("server");
		assertNotNull(sid);
		assertEquals("http://localhost:4567", server);
		assertEquals("ok", res.body.get("status"));
	}

	@Test // partner login
	public void testB() {
		Map<String, Object> req = new HashMap<>();
		req.put("partner", "brightone");
		req.put("key", "abcdefghijklmnopqrstuvwxyz");
		RestClient.Response res = RestClient.POST("/api/session", req, sid, server);
		assertEquals(HttpStatus.OK_200, res.status);
		assertEquals("ok", res.body.get("status"));
	}

	@Test // proxy login
	public void testC() {
		Map<String, Object> req = new HashMap<>();
		req.put("partner", "brightone");
		req.put("key", "abcdefghijklmnopqrstuvwxyz");
		req.put("user", "sim-abcd");
		RestClient.Response res = RestClient.POST("/api/session", req, sid, server);
		assertEquals(HttpStatus.OK_200, res.status);
		assertEquals("ok", res.body.get("status"));
	}

	@Test // delete session
	public void testD() {
		RestClient.Response res = RestClient.DELETE("/api/session", sid, server);
		assertEquals(HttpStatus.OK_200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
}
