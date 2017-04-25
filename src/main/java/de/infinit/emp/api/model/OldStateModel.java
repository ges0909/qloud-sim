package de.infinit.emp.api.model;

import de.infinit.emp.api.domain.OldState;

public class OldStateModel extends Model<OldState, Integer> {
	private static OldStateModel instance = null;

	private OldStateModel() {
		super(OldState.class);
	}

	public static OldStateModel instance() {
		if (instance == null) {
			instance = new OldStateModel();
		}
		return instance;
	}

}
