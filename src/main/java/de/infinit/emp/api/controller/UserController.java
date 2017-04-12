package de.infinit.emp.api.controller;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.gson.annotations.SerializedName;

import de.infinit.emp.Status;
import de.infinit.emp.api.domain.Invitation;
import de.infinit.emp.api.domain.Session;
import de.infinit.emp.api.domain.User;
import de.infinit.emp.api.model.InvitationModel;
import de.infinit.emp.api.model.SensorModel;
import de.infinit.emp.api.model.UserModel;
import de.infinit.emp.utils.Json;
import spark.Request;
import spark.Response;

public class UserController extends Controller {
	private static UserController instance = null;
	static final Logger log = Logger.getLogger(UserController.class.getName());
	final UserModel userModel = UserModel.instance();
	final InvitationModel invitationModel = InvitationModel.instance();
	final SensorModel sensorModel = SensorModel.instance();

	private UserController() {
		super();
	}

	public static UserController instance() {
		if (instance == null) {
			instance = new UserController();
		}
		return instance;
	}

	class UpdateUserRequest {
		String firstname;
		String lastname;
		@SerializedName("display_name")
		String displayName;
		String password;
		String email;
	}

	class InviteUserRequest {
		@SerializedName("invite")
		List<String> userUuids;
	}

	class AcceptInvitationRequest {
		@SerializedName("invitation")
		List<String> invitationsToAccept;
	}

	public Object updateUser(Request request, Response response) {
		if (!isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.SESSION);
		User own = session.getUser();
		if (own == null) {
			return status(Status.WRONG_USER);
		}
		User other = userModel.findFirstByColumn("email", session.getUser());
		if (other != null) {
			return status(Status.ALREADY_EXIST);
		}
		UpdateUserRequest req = decode(request.body(), UpdateUserRequest.class);
		own.setFirstName(req.firstname);
		own.setLastName(req.lastname);
		own.setDisplayName(req.displayName);
		own.setPassword(req.password);
		own.setEmail(req.email);
		if (userModel.update(own) == null) {
			return fail();
		}
		return ok();
	}

	// GET /api/user
	public Object getUser(Request request, Response response) {
		if (!isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.SESSION);
		User own = session.getUser();
		if (own == null) {
			return fail();
		}
		return result("user",
				Json.obj("uuid", own.getUuid(), "username", own.getUserName(), "firstname", own.getFirstName(),
						"lastname", own.getLastName(), "display_name", own.getDisplayName(), "email", own.getEmail(),
						"partner", own.getPartner(), "tag_all", own.getTagAll().getUuid()));
	}

	public Object getUserInvitations(Request request, Response response) {
		if (!isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.SESSION);
		User own = session.getUser();
		if (own == null) {
			return fail();
		}
		List<String> invitations = own.getInvitations().stream().map(Invitation::getUuid).collect(Collectors.toList());
		return result("in", invitations);
	}

	public Object inviteUser(Request request, Response response) {
		if (!isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.SESSION);
		User own = session.getUser();
		if (own == null) {
			return fail();
		}
		InviteUserRequest req = decode(request.body(), InviteUserRequest.class);
		for (String uuid : req.userUuids) {
			User other = userModel.queryForId(uuid);
			if (other == null) {
				return status(Status.WRONG_USER);
			}
			Invitation invitation = new Invitation(other); // create invitation
			other.getInvitations().add(invitation);
			// update because of added invitation
			if (userModel.update(other) == null) {
				return fail();
			}
		}
		return ok();
	}

	public Object acceptInvitation(Request request, Response response) {
		if (!isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.SESSION);
		User own = session.getUser();
		if (own == null) {
			return fail();
		}
		Collection<Invitation> storedInvitations = own.getInvitations();
		if (storedInvitations.isEmpty()) {
			return status(Status.WRONG_INVITATION);
		}
		AcceptInvitationRequest req = decode(request.body(), AcceptInvitationRequest.class);
		int numberAccepted = 0;
		for (String uuid : req.invitationsToAccept) {
			for (Invitation invitation : storedInvitations) {
				if (uuid.equals(invitation.getUuid())) {
					numberAccepted = +1;
					storedInvitations.remove(invitation);
				}
			}
		}
		if (numberAccepted > 0) {
			userModel.update(own); // update because of removed invitations
		}
		if (req.invitationsToAccept.size() != numberAccepted) {
			return fail();
		}
		return ok();
	}
}
