package de.infinit.emp.api.model;

import de.infinit.emp.api.domain.Invitation;

public class InvitationModel extends Model<Invitation, String> {
	private static InvitationModel instance = null;

	private InvitationModel() {
		super(Invitation.class);
	}

	public static InvitationModel instance() {
		if (instance == null) {
			instance = new InvitationModel();
		}
		return instance;
	}
}
