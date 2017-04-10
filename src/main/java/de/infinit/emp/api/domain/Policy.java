package de.infinit.emp.api.domain;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "policies")
public class Policy {
	static final int READ = 1;
	static final int WRITE = 2;
	static final int PROPAGATE = 3;
	static final int OWNER = 4;
	
	@NotNull
	@DatabaseField(canBeNull = false)
	String userUuid;

	@NotNull
	@DatabaseField(defaultValue = "4" /*OWNER*/, canBeNull = false)
	int policyValue;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private transient Tag tag;

	public Policy() {
		// ORMLite needs a no-arg constructor
	}

	public Policy(Tag tag, @NotNull String userUuid, @NotNull int policyValue) {
		this.userUuid = userUuid;
		this.tag = tag;
		this.policyValue = policyValue;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public int getPolicy() {
		return policyValue;
	}

	public void setPolicy(int policy) {
		this.policyValue = policy;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}
