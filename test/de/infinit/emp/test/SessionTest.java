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
import org.junit.Test;

import de.infinit.emp.Application;
import spark.Spark;

public class SessionTest extends WebClient {
	static String sid;
	static String server;

	@BeforeClass
	public static void setUp() throws IOException, SQLException {
		Application.main(null);
	}

	@AfterClass
	public static void tearDown() throws SQLException {
		Spark.stop();
	}

	@Test // get unauthorized session
	public void testA() {
		WebResponse resp = get("/api/session", null, "http://localhost:4567");
		assertEquals(HttpStatus.OK_200, resp.status);
		sid = (String) resp.body.get("sid");
		server = (String) resp.body.get("server");
		assertNotNull(sid);
		assertEquals("http://localhost:4567", server);
		assertEquals("ok", resp.body.get("status"));
	}

	@Test // partner login
	public void testB() {
		Map<String, Object> body = new HashMap<>();
		body.put("partner", "brightone");
		body.put("key", "abcdefghijklmnopqrstuvwxyz");
		WebResponse resp = post("/api/session", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}

	@Test // proxy login
	public void testC() {
		Map<String, Object> body = new HashMap<>();
		body.put("partner", "brightone");
		body.put("key", "abcdefghijklmnopqrstuvwxyz");
		body.put("user", "sim-abcd");
		WebResponse resp = post("/api/session", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}

	@Test // delete session
	public void testD() {
		WebResponse resp = delete("/api/session", sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}
}
