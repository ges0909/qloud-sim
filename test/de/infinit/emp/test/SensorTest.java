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
import org.junit.BeforeClass;
import org.junit.Test;

import de.infinit.emp.Main;
import spark.Spark;

public class SensorTest extends WebClient {
	private static Server server; // database server
	private static String uuid;

	@BeforeClass
	public static void setUp() throws IOException, SQLException {
		server = Server.createTcpServer().start();
		Main.main(null);
	}

	@AfterClass
	public static void tearDown() throws SQLException {
		Spark.stop();
		server.stop();
	}
	
	@Test
	public void testA() {
		Map<String, Object> body = new HashMap<>();
		body.put("description", "Testsensor");
		body.put("code", "SIMUL-FGHIJ-KLMNI-OPQRS");
		WebResponse resp = post("/api/sensor", body);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		assertNotNull(resp.body.get("uuid"));
		uuid = (String) resp.body.get("uuid");
	}

	@Test
	public void testB() {
		WebResponse resp = get("/api/sensor/" + uuid);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}

	@Test
	public void testC() {
		WebResponse resp = delete("/api/sensor/" + uuid);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}
}
