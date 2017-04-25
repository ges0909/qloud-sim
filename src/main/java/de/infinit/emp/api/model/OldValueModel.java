package de.infinit.emp.api.model;

import de.infinit.emp.api.domain.OldValue;

public class OldValueModel extends Model<OldValue, Integer> {
	private static OldValueModel instance = null;

	private OldValueModel() {
		super(OldValue.class);
	}

	public static OldValueModel instance() {
		if (instance == null) {
			instance = new OldValueModel();
		}
		return instance;
	}
}
