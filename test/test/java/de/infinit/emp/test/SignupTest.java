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
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(HttpStatus.OK_200, res.status);
		sid = (String) res.body.get("sid");
		server = (String) res.body.get("server");
		Map<String, Object> req = new HashMap<>();
		req.put("partner", "brightone");
		req.put("key", "abcdefghijklmnopqrstuvwxyz");
		res = RestClient.POST("/api/session", req, sid, server);
		assertEquals(HttpStatus.OK_200, res.status);
	}

	@Test // initiate sign-up
	public void testB() {
		List<String> companyIds = new ArrayList<>();
		companyIds.add("12345");
		Map<String, Object> obj = new HashMap<>();
		obj.put("companyId", companyIds);
		Map<String, Object> req = new HashMap<>();
		req.put("info", obj);
		RestClient.Response res = RestClient.POST("/api/signup/verification", req, sid, server);
		assertEquals(HttpStatus.OK_200, res.status);
		assertEquals("ok", res.body.get("status"));
		assertNotNull(res.body.get("uuid"));
		assertNotNull(res.body.get("verification"));
		uuid = (String) res.body.get("uuid");
		verification = (String) res.body.get("verification");
	}

	@Test // complete sign-up
	public void testC() {
		Map<String, Object> req = new HashMap<>();
		req.put("email", "max.mustermann@infinit-services.de");
		req.put("username", "max");
		req.put("firstname", "max");
		req.put("username", "mustermann");
		req.put("displayname", "max.mustermann");
		req.put("password", "geheim");
		req.put("verification", verification);
		RestClient.Response res = RestClient.POST("/api/signup/user", req, sid, server);
		assertEquals(HttpStatus.OK_200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
}