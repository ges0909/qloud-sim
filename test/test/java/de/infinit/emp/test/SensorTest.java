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
import de.infinit.emp.test.utils.RestClient;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SensorTest {
	static String sid;
	static String server;
	static String uuid;

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
	public void testA_Login() {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(200, res.status);
		sid = (String) res.body.get("sid");
		server = (String) res.body.get("server");
		Map<String, Object> req = new HashMap<>();
		req.put("partner", "brightone");
		req.put("key", "abcdefghijklmnopqrstuvwxyz");
		res = RestClient.POST("/api/session", req, sid, server);
		assertEquals(200, res.status);
	}

	@Test
	public void testB_Add_Sensor() {
		Map<String, Object> req = new HashMap<>();
		req.put("description", "Testsensor");
		req.put("code", "SIMUL-FGHIJ-KLMNI-OPQRS");
		RestClient.Response res = RestClient.POST("/api/sensor", req, sid, server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
		assertNotNull(res.body.get("uuid"));
		uuid = (String) res.body.get("uuid");
	}

	@Test
	public void testC_Get_Sensor() {
		RestClient.Response res = RestClient.GET("/api/sensor/" + uuid, sid, server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
		//Map<String, Object> sensor = (Map<String, Object>) resp.body.get("sensor");
		//assertNotNull("description", sensor.get("description"));
	}

	@Test
	public void testD_Delete_Sensor() {
		RestClient.Response res = RestClient.DELETE("/api/sensor/" + uuid, sid, server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
}
