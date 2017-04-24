package de.infinit.emp.api.model;

import de.infinit.emp.api.domain.State;

public class StateModel extends Model<State, Integer> {
	private static StateModel instance = null;

	private StateModel() {
		super(State.class);
	}

	public static StateModel instance() {
		if (instance == null) {
			instance = new StateModel();
		}
		return instance;
	}

}
