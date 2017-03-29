package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class SignupTest {
	static String sid;
	static String server;
	static String uuid;
	static String verification;

	@BeforeClass
	public static void setUp() throws IOException, SQLException {
		Application.main(null);
		Spark.awaitInitialization();
	}

	@AfterClass
	public static void tearDown() throws SQLException {
		Spark.stop();
	}

	@Test // login
	public void testA() {
		RestClient.Response resp = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(HttpStatus.OK_200, resp.status);
		sid = (String) resp.body.get("sid");
		server = (String) resp.body.get("server");
		Map<String, Object> body = new HashMap<>();
		body.put("partner", "brightone");
		body.put("key", "abcdefghijklmnopqrstuvwxyz");
		resp = RestClient.POST("/api/session", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
	}

	@Test // initiate sign-up
	public void testB() {
		List<String> companyIds = new ArrayList<>();
		companyIds.add("12345");
		Map<String, Object> obj = new HashMap<>();
		obj.put("companyId", companyIds);
		Map<String, Object> body = new HashMap<>();
		body.put("info", obj);
		RestClient.Response resp = RestClient.POST("/api/signup/verification", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		assertNotNull(resp.body.get("uuid"));
		assertNotNull(resp.body.get("verification"));
		uuid = (String) resp.body.get("uuid");
		verification = (String) resp.body.get("verification");
	}

	@Test // complete sign-up
	public void testC() {
		Map<String, Object> body = new HashMap<>();
		body.put("email", "max.mustermann@infinit-services.de");
		body.put("username", "max");
		body.put("firstname", "max");
		body.put("username", "mustermann");
		body.put("displayname", "max.mustermann");
		body.put("password", "geheim");
		body.put("verification", verification);
		RestClient.Response resp = RestClient.POST("/api/signup/user", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}
}
