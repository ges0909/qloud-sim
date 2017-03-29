package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.infinit.emp.Application;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SensorTest extends WebClient {
	static String sid;
	static String server;
	static String uuid;

	@BeforeClass
	public static void setUp() throws IOException, SQLException {
		Application.main(null);
	}

	@AfterClass
	public static void tearDown() throws SQLException {
		Spark.stop();
	}

	@Test
	public void testA_Login() {
		WebResponse resp = get("/api/session", null, "http://localhost:4567");
		assertEquals(200, resp.status);
		sid = (String) resp.body.get("sid");
		server = (String) resp.body.get("server");
		Map<String, Object> body = new HashMap<>();
		body.put("partner", "brightone");
		body.put("key", "abcdefghijklmnopqrstuvwxyz");
		resp = post("/api/session", body, sid, server);
		assertEquals(200, resp.status);
	}

	@Test
	public void testB_Add_Sensor() {
		Map<String, Object> body = new HashMap<>();
		body.put("description", "Testsensor");
		body.put("code", "SIMUL-FGHIJ-KLMNI-OPQRS");
		WebResponse resp = post("/api/sensor", body, sid, server);
		assertEquals(200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		assertNotNull(resp.body.get("uuid"));
		uuid = (String) resp.body.get("uuid");
	}

	@Test
	public void testC_Get_Sensor() {
		WebResponse resp = get("/api/sensor/" + uuid, sid, server);
		assertEquals(200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		//Map<String, Object> sensor = (Map<String, Object>) resp.body.get("sensor");
		//assertNotNull("description", sensor.get("description"));
	}

	@Test
	public void testD_Delete_Sensor() {
		WebResponse resp = delete("/api/sensor/" + uuid, sid, server);
		assertEquals(200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}
}
