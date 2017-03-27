package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.infinit.emp.Main;
import spark.Spark;

public class SensorTest extends WebClient {
	static Server h2DbServer;
	static String sid;
	static String server;
	static String uuid;

	@BeforeClass
	public static void setUp() throws IOException, SQLException {
		h2DbServer = Server.createTcpServer().start();
		Main.main(null);
	}

	@AfterClass
	public static void tearDown() throws SQLException {
		Spark.stop();
		h2DbServer.stop();
	}

	@Before
	public void login() {
		WebResponse resp = get("/api/session", null, "http://localhost:4567");
		assertEquals(HttpStatus.OK_200, resp.status);
		sid = (String) resp.body.get("sid");
		server = (String) resp.body.get("server");
		Map<String, Object> body = new HashMap<>();
		body.put("partner", "brightone");
		body.put("key", "abcdefghijklmnopqrstuvwxyz");
		resp = post("/api/session", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
	}

	@Test // register sensor
	public void testA() {
		Map<String, Object> body = new HashMap<>();
		body.put("description", "Testsensor");
		body.put("code", "SIMUL-FGHIJ-KLMNI-OPQRS");
		WebResponse resp = post("/api/sensor", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		assertNotNull(resp.body.get("uuid"));
		uuid = (String) resp.body.get("uuid");
	}

	@Test // get sensor
	public void testB() {
		WebResponse resp = get("/api/sensor/" + uuid, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}

	//@Test // delete sensor
	public void testC() {
		WebResponse resp = delete("/api/sensor/" + uuid, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}
}
