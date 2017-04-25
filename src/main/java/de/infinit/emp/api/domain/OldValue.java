package de.infinit.emp.api.domain;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;

public class OldValue {
	@DatabaseField(generatedId = true)
	int id;

	@NotNull
	@DatabaseField(canBeNull = false)
	Integer index;

	@NotNull
	@DatabaseField(canBeNull = false)
	Long value;

	@NotNull
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "old_state_id")
	private OldState oldState;

	public OldValue() {
		// ORMLite needs a no-arg constructor
	}

	public OldValue(OldState oldState, Value value) {
		this.oldState = oldState;
		this.index = value.getIndex();
		this.value = value.getValue();
	}

	public Integer getIndex() {
		return index;
	}

	public Long getValue() {
		return value;
	}
}
