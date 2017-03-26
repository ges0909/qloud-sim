package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;

import org.eclipse.jetty.http.HttpStatus;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.infinit.emp.Main;
import spark.Spark;

public class SessionTest extends WebClient {
	private static Server server; // database server

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
	public void getSession() {
		WebResponse res = get("/api/session");
		assertEquals(HttpStatus.OK_200, res.status);
		assertEquals("http://localhost:4567", res.body.get("server"));
		assertNotNull(res.body.get("sid"));
	}
}
