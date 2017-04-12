package de.infinit.emp.api.domain;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "invitations")
public class Invitation {
	@DatabaseField(generatedId = true)
	UUID uuid;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User user;

	public Invitation() {
		// ORMLite needs a no-arg constructor
	}

	public Invitation(User user) {
		this();
		this.user = user;
	}

	public UUID getUuid() {
		return uuid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
