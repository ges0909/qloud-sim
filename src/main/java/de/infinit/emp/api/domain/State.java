package de.infinit.emp.api.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

public class State {
	@DatabaseField(generatedId = true)
	int id;

	@NotNull
	@DatabaseField
	long recvTime;

	@NotNull
	@DatabaseField(defaultValue = "false")
	boolean eventSent;

	@NotNull
	@ForeignCollectionField(orderColumnName = "index", orderAscending = true)
	Collection<Value> values;

	@NotNull
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "sensor_id")
	private Sensor sensor;

	public State() {
		// ORMLite needs a no-arg constructor
		this.recvTime = Instant.now().getEpochSecond();
		this.values = new ArrayList<>();
		this.eventSent = false;
	}

	public State(Sensor sensor) {
		this();
		this.sensor = sensor;
	}

	public boolean isEventSent() {
		return eventSent;
	}

	public void setEventSent(boolean eventSent) {
		this.eventSent = eventSent;
	}

	public long getRecvTime() {
		return recvTime;
	}

	public Collection<Value> getValues() {
		return values;
	}
}
