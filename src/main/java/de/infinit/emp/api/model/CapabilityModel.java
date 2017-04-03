package de.infinit.emp.api.model;

import de.infinit.emp.api.domain.Capability;

public class CapabilityModel extends Model<Capability, String> {
	private static CapabilityModel instance = null;

	private CapabilityModel() {
		super(Capability.class);
	}

	public static CapabilityModel instance() {
		if (instance == null) {
			instance = new CapabilityModel();
		}
		return instance;
	}
}
