package de.infinit.emp.domain;

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
