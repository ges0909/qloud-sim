package de.infinit.emp.api.domain;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;

public class Value {
	@DatabaseField(generatedId = true)
	int id;
	
	@NotNull
	@DatabaseField(canBeNull = false)
	Integer index;

	@NotNull
	@DatabaseField(canBeNull = false)
	Long value;
	
	@NotNull
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "state_id")
	private State state;

	public Value() {
		// ORMLite needs a no-arg constructor
	}
	
	public Value(@NotNull State state, @NotNull Integer index, @NotNull Long value) {
		this.state = state;
		this.index = index;
		this.value = value;
	}

	public Long getValue() {
		return value;
	}
	
	public Integer getIndex() {
		return index;
	}
}
