package de.infinit.emp.api.domain;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "events")
public class Event {
	@DatabaseField(id = true)
	String sensorUuid;
	
	@DatabaseField(canBeNull = false)
	Date expiresAt;

	public Event() {
		// ORMLite needs a no-arg constructor
	}

	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getSensorUuid() {
		return this.sensorUuid;
	}

	public void setSensorUuid(String sensorUuid) {
		this.sensorUuid = sensorUuid;
	}
}
