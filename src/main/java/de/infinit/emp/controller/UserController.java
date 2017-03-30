package de.infinit.emp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;

import de.infinit.emp.Status;
import de.infinit.emp.Uuid;
import de.infinit.emp.domain.Invitation;
import de.infinit.emp.domain.Session;
import de.infinit.emp.domain.User;
import de.infinit.emp.model.InvitationModel;
import de.infinit.emp.model.UserModel;
import de.infinit.emp.utils.Json;
import spark.Request;
import spark.Response;

public class UserController extends Controller {
	static final Logger log = Logger.getLogger(UserController.class.getName());
	static InvitationModel invitationModel = new InvitationModel();
	static UserModel userModel = new UserModel();

	class GetUserResponse {
		String uuid;
		String username;
		String firstname;
		String lastname;
		String display_name;
		String email;
		String partner;
		String tag_all;
	}

	class UpdateUserRequest {
		String firstname;
		String lastname;
		String display_name;
		String password;
		String email;
	}

	class InviteUserRequest {
		@SerializedName("invite")
		List<String> invites;
	}

	class AcceptInvitationRequest {
		@SerializedName("invitation")
		List<String> invitations;
	}
	
	public static Object getUser(Request request, Response response) {
		if (isPartnerSession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User own = userModel.findById(session.getUserUuid());
		if (own == null) {
			return status(Status.FAIL);
		}
		GetUserResponse resp = convert(own, GetUserResponse.class);
		resp.partner = config.partner();
		return result("user", resp);
	}

	public static Object updateUser(Request request, Response response) {
		if (isPartnerSession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User own = userModel.findById(session.getUserUuid());
		if (own == null) {
			return status(Status.FAIL);
		}
		User other = userModel.findByEmail(session.getUserUuid());
		if (other != null) {
			return status(Status.ALREADY_EXIST);
		}
		UpdateUserRequest req = decode(request.body(), UpdateUserRequest.class);
		own.setFirstName(req.firstname);
		own.setLastName(req.lastname);
		own.setDisplayName(req.display_name);
		own.setPassword(req.password);
		own.setEmail(req.email);
		if (userModel.update(own) == null) {
			return status(Status.FAIL);
		}
		return status(Status.OK);
	}

	public static Object getUserInvitations(Request request, Response response) {
		if (isPartnerSession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User own = userModel.findById(session.getUserUuid());
		if (own == null) {
			return status(Status.FAIL);
		}
		Map<String, Object> invitation = new HashMap<>();
		for (Invitation i : own.getInvitations()) {
			invitation.put(i.getUuid(), Json.obj("description", "Simulator generated description for invitations."));
		}
		return result("invitation", invitation);
	}

	public static Object inviteUser(Request request, Response response) {
		if (isPartnerSession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User own = userModel.findById(session.getUserUuid());
		if (own == null) {
			return status(Status.FAIL);
		}
		InviteUserRequest req = decode(request.body(), InviteUserRequest.class);
		for (String uuid : req.invites) {
			User other = userModel.findById(uuid);
			if (other == null) {
				return status(Status.WRONG_USER);
			}
		}
		Invitation invitation = new Invitation();
		invitation.setUuid(Uuid.get());
		if (invitationModel.create(invitation) == null) {
			return status(Status.FAIL);
		}
		return status(Status.OK);
	}

	public static Object acceptInvitation(Request request, Response response) {
		if (isPartnerSession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User own = userModel.findById(session.getUserUuid());
		if (own == null) {
			return status(Status.FAIL);
		}
		ForeignCollection<Invitation> invitations = own.getInvitations();
		if (invitations.isEmpty()) {
			return status(Status.WRONG_INVITATION);
		}
		AcceptInvitationRequest req = decode(request.body(), AcceptInvitationRequest.class);
		for (Invitation i : invitations) {
			if (!req.invitations.stream().anyMatch(uuid -> uuid.equals(i.getUuid()))) {
				return status(Status.FAIL);
			}
		}
		return status(Status.OK);
	}
}
