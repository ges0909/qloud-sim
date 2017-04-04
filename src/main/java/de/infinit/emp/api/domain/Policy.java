package de.infinit.emp.api.domain;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "policies")
public class Policy {
	@NotNull
	@DatabaseField(id = true, canBeNull = false)
	String uuid; // user uuid

	@NotNull
	@DatabaseField(defaultValue = "4", canBeNull = false) // 4 = owner
	int policy;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private transient Tag tag;
	
	public Policy() {
		// ORMLite needs a no-arg constructor
	}

	public Policy(Tag tag, @NotNull String uuid, @NotNull int policy) {
		this.uuid = uuid;
		this.tag = tag;
		this.policy = policy;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getPolicy() {
		return policy;
	}

	public void setPolicy(int policy) {
		this.policy = policy;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}
