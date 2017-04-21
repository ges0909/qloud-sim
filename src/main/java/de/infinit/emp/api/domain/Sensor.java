package de.infinit.emp.api.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import de.infinit.emp.Application;
import de.infinit.emp.api.model.CapabilityModel;
import de.infinit.emp.api.model.SensorModel;

@DatabaseTable(tableName = "sensors")
public class Sensor {
	@Expose
	@DatabaseField(generatedId = true)
	UUID uuid;

	@Expose
	@NotNull
	@Pattern(regexp = "^.{10,50}$")
	@DatabaseField(unique = true, canBeNull = false)
	String code;

	@Expose
	@NotNull
	@DatabaseField(unique = true, canBeNull = false)
	String sdevice;

	@Expose
	@SerializedName("description")
	@Pattern(regexp = "^.{0,200}$")
	@DatabaseField
	String description;

	@Expose
	@DatabaseField(defaultValue = "EnergyCam")
	String model;

	@Expose
	@SerializedName("recv_interval")
	@DatabaseField()
	int recvInterval;

	@Expose
	@SerializedName("recv_time")
	@DatabaseField
	long recvTime;

	@Expose
	@SerializedName("battery_ok")
	@DatabaseField()
	boolean batteryOk;

	@DatabaseField(foreign = true, columnName = "owner_id", foreignAutoRefresh = true)
	private User owner;

	@ForeignCollectionField(orderColumnName = "order", orderAscending = true)
	private Collection<Capability> capabilities;

	@SerializedName("is_event_sent")
	@DatabaseField
	boolean eventSent;

	private ScheduledFuture<?> future;

	public Sensor() {
		// ORMLite needs a no-arg constructor
		this.capabilities = new ArrayList<>();
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSdevice() {
		return sdevice;
	}

	public void setSdevice(String sdevice) {
		this.sdevice = sdevice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getRecvInterval() {
		return recvInterval;
	}

	public void setRecvInterval(int recvInterval) {
		this.recvInterval = recvInterval;
	}

	public long getRecvTime() {
		return recvTime;
	}

	public void setRecvTime(long recvTime) {
		this.recvTime = recvTime;
	}

	public boolean isBatteryOk() {
		return batteryOk;
	}

	public void setBatteryOk(boolean batteryOk) {
		this.batteryOk = batteryOk;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public boolean isEventSent() {
		return eventSent;
	}

	public void setEventSent(boolean sentAsEvent) {
		this.eventSent = sentAsEvent;
	}

	public Collection<Capability> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Collection<Capability> capabilities) {
		this.capabilities = capabilities;
	}

	// ordering is ensured by ORMLite; see @ForeignCollectionField above
	public Collection<Capability> getCapabilitiesByOrder() {
		return getCapabilities();
	}

	public void startSimulation() {
		Runnable task = () -> {
			if (isEventSent()) {
				return;
			}
			for (Capability c : getCapabilitiesByOrder()) {
				if (c.getDelta() != null) {
					Long value = c.getValue();
					c.setValue(value + c.getDelta());
					CapabilityModel.instance().update(c);
				}
			}
			setRecvTime(Instant.now().getEpochSecond());
			setEventSent(false);
			SensorModel.instance().update(this);
		};
		future = Application.getExecutor().scheduleWithFixedDelay(task, 0, getRecvInterval(), TimeUnit.SECONDS);
	}

	public void stopSimulation() {
		if (future != null)
			future.cancel(false);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sensor other = (Sensor) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
