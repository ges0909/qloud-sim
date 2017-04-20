package de.infinit.emp.api.domain;

import com.j256.ormlite.field.DatabaseField;

public class TagSensor {
	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField(foreign = true, columnName = "tag_id")
	Tag tag;

	@DatabaseField(foreign = true, columnName = "sensor_id")
	Sensor sensor;

	public TagSensor() {
		// ORMLite needs a no-arg constructor
	}

	public TagSensor(Tag tag, Sensor sensor) {
		this.tag = tag;
		this.sensor = sensor;
	}
	
	public int getId() {
		return id;
	}
}
