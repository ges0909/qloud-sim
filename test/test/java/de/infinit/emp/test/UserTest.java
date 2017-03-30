package de.infinit.emp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.infinit.emp.Application;
import de.infinit.emp.test.utils.RestClient;
import de.infinit.emp.test.utils.Utils;
import de.infinit.emp.utils.Json;
import spark.Spark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest {
	static String ownUserUuid;
	static String otherUserUuid;

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
	public void testA_Login_As_Partner_And_Add_User() {
		Utils.loginAsPartner();
		ownUserUuid = Utils.addUser("email", "max.mustermann@mail.de", "firstname", "Max", "lastname", "Mustermann");
		Utils.logout();
	}

	@Test
	public void testB_Login_As_User_Added_User() {
		Utils.loginAsUser(ownUserUuid);
	}

	@Test
	public void testC_Get_User_Data_Of_LoggedIn_User() {
		RestClient.Response res = RestClient.GET("/api/user", Utils.sid, Utils.server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
		@SuppressWarnings("unchecked")
		Map<String, Object> user = (Map<String, Object>) res.body.get("user");
		assertEquals("max.mustermann@mail.de", user.get("email"));
		assertEquals("Max", user.get("firstname"));
		assertEquals("Mustermann", user.get("lastname"));
		assertNotNull(user.get("tag_all"));
	}

	@Test
	public void testD_Update_LoggedIn_User() {
		Map<String, Object> req = Json.obj("email", "max.mustermann@mail.de", "firstname", "Peter", "lastname", "Pan");
		RestClient.Response res = RestClient.POST("/api/user", req, Utils.sid, Utils.server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}

	@Test
	public void testE_Get_User_Invitations() {
		RestClient.Response res = RestClient.GET("/api/user/invitation", Utils.sid, Utils.server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
		assertNotNull(res.body.get("invitation"));
	}

	@Test
	public void testF_Invite_User() {
		otherUserUuid = Utils.addUser("email", "angela.merkel@mail.de");
		Map<String, Object> req = Json.obj("invite", Json.arr(otherUserUuid));
		RestClient.Response res = RestClient.POST("/api/user/invitation", req, Utils.sid, Utils.server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}

	@Test
	public void testG_Get_Accept_Invitation() {
		Utils.loginAsUser(otherUserUuid);
		Map<String, Object> req = Json.obj("invitation", Json.arr(otherUserUuid));
		RestClient.Response res = RestClient.POST("/api/user/link", req, Utils.sid, Utils.server);
		assertEquals(200, res.status);
		assertEquals("ok", res.body.get("status"));
	}
}
