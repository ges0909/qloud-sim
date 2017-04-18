package de.infinit.emp.api.domain;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "capabilities")
public class Capability {
	@DatabaseField(generatedId = true)
	UUID uuid;

	@DatabaseField(canBeNull = false)
	Integer order;

	@NotNull
	@DatabaseField(canBeNull = false)
	String name;

	@DatabaseField(canBeNull = false)
	Long value;

	@DatabaseField
	Long delta;

	@NotNull
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "sensor_id")
	private Sensor sensor;

	public Capability() {
		// ORMLite needs a no-arg constructor
	}

	public Capability(Sensor sensor, Integer order, String name, Long value, Long delta) {
		super();
		this.sensor = sensor;
		this.order = order;
		this.name = name;
		this.value = value;
		this.delta = delta;
	}

	public UUID getUuid() {
		return uuid;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public Long getDelta() {
		return delta;
	}

	public void setDelta(Long delta) {
		this.delta = delta;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
}
