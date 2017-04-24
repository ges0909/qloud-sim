package de.infinit.emp.api.domain;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;

public class Capability {
	@DatabaseField(generatedId = true)
	UUID uuid;

	@NotNull
	@DatabaseField(canBeNull = false)
	Integer index;

	@NotNull
	@DatabaseField(canBeNull = false)
	String name;

	@DatabaseField
	Long delta;

	@NotNull
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "sensor_id")
	private Sensor sensor;

	public Capability() {
		// ORMLite needs a no-arg constructor
	}

	public Capability(Sensor sensor, Integer index, String name, Long delta) {
		this.sensor = sensor;
		this.index = index;
		this.name = name;
		this.delta = delta;
	}

	public String getName() {
		return name;
	}

	public Long getDelta() {
		return delta;
	}
}
