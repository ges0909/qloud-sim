package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

public class SensorTest extends TestClient {
	static String uuid;

	@Test
	public void testA() {
		Map<String, Object> body = new HashMap<>();
		body.put("description", "Testsensor");
		body.put("code", "ABCDE-FGHIJ-KLMNI-OPQRS");
		TestResponse resp = post("/api/sensor", body);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		assertNotNull(resp.body.get("uuid"));
		uuid = (String) resp.body.get("uuid");
	}

	@Test
	public void testB() {
		TestResponse resp = get("/api/sensor/" + uuid);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}

	@Test
	public void testC() {
		TestResponse resp = delete("/api/sensor/" + uuid);
		assertEquals(HttpStatus.OK_200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}
}
