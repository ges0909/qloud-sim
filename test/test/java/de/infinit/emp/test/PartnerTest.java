package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import de.infinit.emp.test.utils.RestClient;
import de.infinit.emp.utils.Json;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PartnerTest {
	static String partnerSid;
	static String partnerServer;
	static String userUuid;

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
	public void testA_Login_As_Partner() {
		RestClient.Response res = RestClient.GET("/api/session", null, "http://localhost:4567");
		assertEquals(HttpStatus.OK_200, res.status);
		partnerSid = (String) res.body.get("sid");
		partnerServer = (String) res.body.get("server");
		Map<String, Object> body = Json.obj("partner", "brightone", "key", "abcdefghijklmnopqrstuvwxyz");
		res = RestClient.POST("/api/session", body, partnerSid, partnerServer);
		assertEquals(HttpStatus.OK_200, res.status);
	}

	@Test
	public void testB_List_All_Partner_Realted_Users() {
		RestClient.Response res = RestClient.GET("/api/partner/user", partnerSid, partnerServer);
		assertEquals(HttpStatus.OK_200, res.status);
		assertEquals("ok", res.body.get("status"));
		@SuppressWarnings("unchecked")
		List<String> users = (List<String>) res.body.get("users");
		assertEquals(0, users.size());
	}

	@Test
	public void testC_Add_User() {
		Map<String, Object> body = Json.obj("info", Json.obj("companyId", Json.arr("12345")));
		RestClient.Response resp = RestClient.POST("/api/signup/verification", body, partnerSid, partnerServer);
		assertEquals(200, resp.status);
		assertEquals("ok", resp.body.get("status"));
		assertNotNull(resp.body.get("uuid"));
		assertNotNull(resp.body.get("verification"));
		userUuid = (String) resp.body.get("uuid");
		String verification = (String) resp.body.get("verification");
		body = Json.obj("email", "peter.pan@test.de", "firstname", "Peter", "lastname", "Pan", "display_name", "Peter Pan");
		body.put("verification", verification);
		resp = RestClient.POST("/api/signup/user", body, partnerSid, partnerServer);
		assertEquals(200, resp.status);
		assertEquals("ok", resp.body.get("status"));
	}

	@Test
	public void testD_List_All_Partner_Realted_Users() {
		RestClient.Response res = RestClient.GET("/api/partner/user", partnerSid, partnerServer);
		assertEquals(HttpStatus.OK_200, res.status);
		assertEquals("ok", res.body.get("status"));
		@SuppressWarnings("unchecked")
		List<String> users = (List<String>) res.body.get("users");
		assertEquals(1, users.size());
	}

	@Test
	public void testE_Get_User_Data() {
		RestClient.Response res = RestClient.GET("/api/partner/user/" + userUuid, partnerSid, partnerServer);
		assertEquals(HttpStatus.OK_200, res.status);
		assertEquals("ok", res.body.get("status"));
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) res.body.get("user");
		assertEquals("peter.pan@test.de", user.get("email"));
		assertEquals("Peter", user.get("firstname"));
		assertEquals("Pan", user.get("lastname"));
		assertEquals("Peter Pan", user.get("display_name"));
	}

	@Test
	public void testF_Delete_User() {
		RestClient.Response res =
				RestClient.POST("/api/partner/user/" + userUuid, Json.obj("deleted", true), partnerSid, partnerServer);
		assertEquals(HttpStatus.OK_200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
}
