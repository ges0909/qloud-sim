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

import de.infinit.emp.Application;
import de.infinit.emp.api.model.OldStateModel;
import de.infinit.emp.api.model.OldValueModel;
import de.infinit.emp.api.model.StateModel;
import de.infinit.emp.api.model.ValueModel;

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
	User owner;

	@Expose
	@NotNull
	@DatabaseField(unique = true, canBeNull = false)
	String sdevice;

	@DatabaseField(foreign = true, columnName = "state_id", foreignAutoRefresh = true)
	State state;

	@ForeignCollectionField(orderAscending = true /* ascending */, orderColumnName = "index")
	Collection<Capability> capabilities;

	@ForeignCollectionField(orderAscending = true /* ascending */, orderColumnName = "recvTime")
	Collection<OldState> oldStates;

	ScheduledFuture<?> future;

	public Sensor() {
		// ORMLite needs a no-arg constructor
		this.capabilities = new ArrayList<>();
		this.oldStates = new ArrayList<>();
	}

	// ordering by 'order' (see @ForeignCollectionField above)
	public Collection<Capability> getCapabilities() {
		return capabilities;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getModel() {
		return model;
	}

	public User getOwner() {
		return owner;
	}

	// ordering by 'recvTime' (see @ForeignCollectionField above)
	public Collection<OldState> getOldStates() {
		return oldStates;
	}

	public int getRecvInterval() {
		return recvInterval;
	}

	public long getRecvTime() {
		return recvTime;
	}

	public String getSdevice() {
		return sdevice;
	}

	public State getState() {
		return state;
	}

	public UUID getUuid() {
		return uuid;
	}

	public boolean isBatteryOk() {
		return batteryOk;
	}

	public void setBatteryOk(boolean batteryOk) {
		this.batteryOk = batteryOk;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setRecvInterval(int recvInterval) {
		this.recvInterval = recvInterval;
	}

	public void setRecvTime(long recvTime) {
		this.recvTime = recvTime;
	}

	public void setSdevice(String sdevice) {
		this.sdevice = sdevice;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Capability getCapabilityOfIndex(Integer index) {
		return capabilities.stream().filter(c -> c.getIndex().equals(index)).findFirst().orElse(null);
	}

	public void startSimulation() {
		Runnable task = () -> {
			// add current state to history
			OldState oldState = new OldState(this, state.getRecvTime());
			OldStateModel.instance().create(oldState);
			oldStates.add(oldState);
			//
			state.setRecvTime(Instant.now().getEpochSecond());
			state.setEventSent(false);
			StateModel.instance().update(state);
			for (Value value : this.state.getValues()) {
				// add current state value to history
				OldValue oldValue = new OldValue(oldState, value);
				OldValueModel.instance().create(oldValue);
				oldState.getOldValues().add(oldValue);
				// derive new value from current state value
				Capability capability = getCapabilityOfIndex(value.getIndex());
				if (capability != null && capability.getDelta() != null) {
					value.setValue(value.getValue() + capability.getDelta());
					ValueModel.instance().update(value);
				}
			}
		};
		future = Application.getExecutor().scheduleWithFixedDelay(task, recvInterval, recvInterval, TimeUnit.SECONDS);
	}

	public void stopSimulation() {
		if (future != null) {
			future.cancel(false);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Sensor other = (Sensor) obj;
		if (uuid == null) {
			if (other.uuid != null) {
				return false;
			}
		} else if (!uuid.equals(other.uuid)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}
}
