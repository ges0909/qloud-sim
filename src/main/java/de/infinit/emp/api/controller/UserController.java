package de.infinit.emp.api.controller;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
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
		List<UUID> userUuids;
	}

	class AcceptInvitationRequest {
		@SerializedName("invitation")
		List<UUID> invitationsToAccept;
	}

	public Object updateUser(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		user = userModel.queryForId(user.getUuid());
		if (user == null) {
			return status(Status.WRONG_USER);
		}
		UpdateUserRequest req = decode(request.body(), UpdateUserRequest.class);
		user.setFirstName(req.firstname);
		user.setLastName(req.lastname);
		user.setDisplayName(req.displayName);
		user.setPassword(req.password);
		user.setEmail(req.email);
		if (userModel.update(user) == null) {
			return fail();
		}
		return ok();
	}

	// GET /api/user
	public Object getUser(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		user = userModel.queryForId(user.getUuid());
		if (user == null) {
			return status(Status.WRONG_USER);
		}
		return result("user",
				Json.obj("uuid", user.getUuid(), "username", user.getUserName(), "firstname", user.getFirstName(),
						"lastname", user.getLastName(), "display_name", user.getDisplayName(), "email", user.getEmail(),
						"partner", user.getPartner(), "tag_all", user.getTagAll().getUuid()));
	}

	public Object getUserInvitations(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		user = userModel.queryForId(user.getUuid());
		if (user == null) {
			return status(Status.WRONG_USER);
		}
		List<String> invitations = user.getInvitations().stream().map(i -> i.getUuid().toString()).collect(Collectors.toList());
		return result("in", invitations);
	}

	public Object inviteUser(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		InviteUserRequest req = decode(request.body(), InviteUserRequest.class);
		for (UUID uuid : req.userUuids) {
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
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		Collection<Invitation> storedInvitations = user.getInvitations();
		if (storedInvitations.isEmpty()) {
			return status(Status.WRONG_INVITATION);
		}
		AcceptInvitationRequest req = decode(request.body(), AcceptInvitationRequest.class);
		int numberAccepted = 0;
		for (UUID uuid : req.invitationsToAccept) {
			for (Invitation invitation : storedInvitations) {
				if (uuid.equals(invitation.getUuid())) {
					numberAccepted = +1;
					storedInvitations.remove(invitation);
				}
			}
		}
		if (numberAccepted > 0) {
			userModel.update(user); // update because of removed invitations
		}
		if (req.invitationsToAccept.size() != numberAccepted) {
			return fail();
		}
		return ok();
	}
}
