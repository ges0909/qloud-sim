package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.infinit.emp.Application;
import de.infinit.emp.test.utils.Json;
import de.infinit.emp.test.utils.RestClient;
import de.infinit.emp.test.utils.Utils;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest {
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
	public void testA_Add_Partner_Related_User() {
		RestClient.Response resp = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(HttpStatus.OK_200, resp.status);
		sid = (String) resp.body.get("sid");
		server = (String) resp.body.get("server");
		Map<String, Object> body = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz");
		resp = RestClient.POST("/api/session", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		uuid = Utils.addUser(sid, server);
		RestClient.DELETE("/api/session", sid, server);
	}

	@Test
	public void testB_Proxy_Login() {
		RestClient.Response resp = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(HttpStatus.OK_200, resp.status);
		sid = (String) resp.body.get("sid");
		server = (String) resp.body.get("server");
		Map<String, Object> body = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz", "user", uuid);
		resp = RestClient.POST("/api/session", body, sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
	}
	
	@Test
	public void testC_Get_User() { // get user
		RestClient.Response resp = RestClient.GET("/api/user", sid, server);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}

}
