package de.infinit.emp.api.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "sensors")
public class Sensor {
	@DatabaseField(generatedId = true)
	UUID uuid;

	@NotNull
	@Pattern(regexp = "^.{10,50}$")
	@DatabaseField(unique = true, canBeNull = false)
	String code;

	@NotNull
	@DatabaseField(unique = true, canBeNull = false)
	String sdevice;

	@Pattern(regexp = "^.{0,200}$")
	@SerializedName("description")
	@DatabaseField
	String description;

	@DatabaseField(defaultValue = "EnergyCam")
	String model;

	@SerializedName("recv_interval")
	@DatabaseField()
	int recvInterval;

	@DatabaseField
	@SerializedName("recv_time")
	long recvTime;

	@SerializedName("battery_ok")
	@DatabaseField()
	boolean batteryOk;

	@DatabaseField(foreign = true, columnName = "owner_id", foreignAutoRefresh = true)
	private transient User owner;

	@ForeignCollectionField
	private transient Collection<Tag> tags;

	@ForeignCollectionField(orderColumnName = "order", orderAscending = true)
	private transient Collection<Capability> capabilities;

	public Sensor() {
		// ORMLite needs a no-arg constructor
		this.tags = new ArrayList<>();
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

	public Collection<Tag> getTags() {
		return this.tags;
	}

	public void setTags(Collection<Tag> tags) {
		this.tags = tags;
	}

	public Collection<Capability> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Collection<Capability> capabilities) {
		this.capabilities = capabilities;
	}

	public UUID getOwnerUuid() {
		for (Tag tag : tags) {
			for (Policy policy : tag.getPolicies()) {
				if (policy.getPolicy() == Policy.OWNER) {
					return tag.getUuid();
				}
			}
		}
		return null;
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
