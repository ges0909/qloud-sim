package de.infinit.emp.api.model;

import de.infinit.emp.api.domain.Policy;

public class PolicyModel extends Model<Policy, Void> {
	private static PolicyModel instance = null;

	private PolicyModel() {
		super(Policy.class);
	}

	public static PolicyModel instance() {
		if (instance == null) {
			instance = new PolicyModel();
		}
		return instance;
	}
}
