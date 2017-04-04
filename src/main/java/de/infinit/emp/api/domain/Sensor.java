package de.infinit.emp.api.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import de.infinit.emp.Uuid;

@DatabaseTable(tableName = "sensors")
public class Sensor {
	@NotNull
	@DatabaseField(id = true)
	String uuid;

	@NotNull
	@Pattern(regexp = "^.{10,50}$")
	@DatabaseField(unique = true, canBeNull = false)
	String code;

	@Pattern(regexp = "^.{0,200}$")
	@SerializedName("description")
	@DatabaseField
	String description;

	@DatabaseField
	long time;

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

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private transient Tag tag;

	// One-to-many
	@ForeignCollectionField
	private transient Collection<Capability> capabilities;

	public Sensor() {
		// ORMLite needs a no-arg constructor
		this.uuid = Uuid.next();
		this.capabilities = new ArrayList<>();
	}

	public String getUuid() {
		return uuid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
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

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public Collection<Capability> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Collection<Capability> capabilities) {
		this.capabilities = capabilities;
	}
}