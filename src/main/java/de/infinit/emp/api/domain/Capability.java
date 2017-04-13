package de.infinit.emp.api.domain;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "capabilities")
public class Capability {
	@DatabaseField(generatedId = true)
	UUID uuid;
	
	@NotNull
	@DatabaseField(canBeNull = false)
	String name;

	@NotNull
	@DatabaseField(canBeNull = false, defaultValue = "data")
	String type;

	@DatabaseField(canBeNull = false)
	Integer value;

	@DatabaseField(canBeNull = false)
	Integer order;

	@NotNull
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "sensor_id")
	private Sensor sensor;

	public Capability() {
		// ORMLite needs a no-arg constructor
	}

	public Capability(Sensor sensor, @NotNull String name, String type, Integer value, Integer order) {
		super();
		this.sensor = sensor;
		this.name = name;
		this.type = type;
		this.value = value;
		this.order = order;
	}

	public UUID getUuid() {
		return uuid;
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

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
}
