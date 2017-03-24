package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SessionTest extends TestClient {
	@Test
	public void getSession() {
		Response res = get("/api/session");
		assertEquals(200, res.status);
		assertEquals("http://localhost:4567", res.body.get("server"));
		assertNotNull(res.body.get("sid"));
	}
}
