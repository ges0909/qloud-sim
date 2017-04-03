package de.infinit.emp.api.domain;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "capabilities")
public class Capability {
	@NotNull
	@DatabaseField(canBeNull = false)
	String name;

	@NotNull
	@DatabaseField(canBeNull = false, defaultValue = "data")
	String type;

	@NotNull
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Sensor sensor;

	public Capability() {
		// ORMLite needs a no-arg constructor
	}

	public Capability(Sensor sensor, @NotNull String name, String type) {
		super();
		this.sensor = sensor;
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
}
