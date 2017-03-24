package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class SensorTest extends TestClient {
	@Test
	public void createSensor() {
		Map<String, Object> body = new HashMap<>();
		body.put("description", "Testsensor");
		body.put("code", "ABCDE-FGHIJ-KLMNI-OPQRS");
		Response res = post("/api/sensor", body);
		assertEquals(201, res.status);
		assertEquals("ok", res.body.get("status"));
		assertNotNull(res.body.get("uuid"));
	}
}
