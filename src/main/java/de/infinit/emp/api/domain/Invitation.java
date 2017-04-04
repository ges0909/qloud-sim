package de.infinit.emp.api.domain;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.infinit.emp.Uuid;

@DatabaseTable(tableName = "invitations")
public class Invitation {
	@NotNull
	@DatabaseField(id = true)
	String uuid;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User user;

	public Invitation() {
		// ORMLite needs a no-arg constructor
		this.uuid = Uuid.next();
	}

	public Invitation(User user) {
		this();
		this.user = user;
	}

	public String getUuid() {
		return uuid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
