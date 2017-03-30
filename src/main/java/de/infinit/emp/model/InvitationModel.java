package de.infinit.emp.model;

import de.infinit.emp.domain.Invitation;

public class InvitationModel extends Model<Invitation, String> {
	public InvitationModel() {
		super(Invitation.class);
	}

	public Invitation create(Invitation invitation) {
		return create(super.dao, invitation);
	}

	public int delete(String uuid) {
		return delete(super.dao, uuid);
	}
}
