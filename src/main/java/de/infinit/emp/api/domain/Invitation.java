package de.infinit.emp.api.domain;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "invitations")
public class Invitation {
	@NotNull
	@DatabaseField(id = true)
	String uuid;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User user;

	public Invitation() {
		// ORMLite needs a no-arg constructor
	}

	public Invitation(User user, @NotNull String uuid) {
		super();
		this.uuid = uuid;
		this.user = user;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
