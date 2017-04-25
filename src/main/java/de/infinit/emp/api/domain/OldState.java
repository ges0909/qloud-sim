package de.infinit.emp.api.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

public class OldState {
	@DatabaseField(generatedId = true)
	int id;

	@NotNull
	@DatabaseField
	long recvTime;

	@NotNull
	@ForeignCollectionField(orderColumnName = "index", orderAscending = true)
	Collection<OldValue> oldValues;

	@NotNull
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "sensor_id")
	private Sensor sensor;

	public OldState() {
		// ORMLite needs a no-arg constructor
		this.oldValues = new ArrayList<>();
	}

	public OldState(Sensor sensor, long recvTime) {
		this();
		this.sensor = sensor;
		this.recvTime = recvTime;
	}

	public long getRecvTime() {
		return recvTime;
	}

	public Collection<OldValue> getOldValues() {
		return oldValues;
	}
}
