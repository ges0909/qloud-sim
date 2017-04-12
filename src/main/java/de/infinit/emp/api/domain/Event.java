package de.infinit.emp.api.domain;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.infinit.emp.Uuid;

@DatabaseTable(tableName = "events")
public class Event {
	@DatabaseField(id = true)
	String id;
	
	@DatabaseField(canBeNull = false)
	Date expiresAt;

	@NotNull
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Sensor sensor;

	@NotNull
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Session session;

	public Event() {
		// ORMLite needs a no-arg constructor
		this.id = Uuid.next();
	}

	public Event(@NotNull Session session, Sensor sensor, Date expiresAt) {
		this();
		this.session = session;
		this.sensor = sensor;
		this.expiresAt = expiresAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
}
