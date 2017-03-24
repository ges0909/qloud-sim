package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

public class SessionTest extends TestClient {
	@Test
	public void getSession() {
		TestResponse res = get("/api/session");
		assertEquals(HttpStatus.OK_200, res.status);
		assertEquals("http://localhost:4567", res.body.get("server"));
		assertNotNull(res.body.get("sid"));
	}
}
