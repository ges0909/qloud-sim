package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.infinit.emp.Main;
import de.infinit.emp.test.util.WebClient;
import spark.Spark;

public class SessionControllerTest extends WebClient {

	@BeforeClass
	public static void setUp() {
		Main.main(null);
	}

	@AfterClass
	public static void tearDown() {
		Spark.stop();
	}

	@Test
	public void getSession() {
		Response res = request("GET", "/api/session");
		Map<String, String> json = res.json();
		assertEquals(200, res.status);
		assertEquals("/api/session", json.get("server"));
		assertNotNull(json.get("sid"));
	}

}
