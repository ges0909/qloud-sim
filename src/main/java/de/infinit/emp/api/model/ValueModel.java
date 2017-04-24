package de.infinit.emp.api.model;

import de.infinit.emp.api.domain.Value;

public class ValueModel extends Model<Value, Integer> {
	private static ValueModel instance = null;

	private ValueModel() {
		super(Value.class);
	}

	public static ValueModel instance() {
		if (instance == null) {
			instance = new ValueModel();
		}
		return instance;
	}
}
