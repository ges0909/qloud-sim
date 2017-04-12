package de.infinit.emp.api.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "sessions")
public class Session {
	@NotNull
	@DatabaseField(id = true)
	String sid;

	@NotNull
	@DatabaseField(canBeNull = false)
	String server;

	@DatabaseField
	String partner;

	@DatabaseField
	String key;

	@DatabaseField(foreign = true, columnName = "user_id", foreignAutoRefresh = true)
	User user;

	@ForeignCollectionField
	Collection<Event> events;

	public Session() {
		// ORMLite needs a no-arg constructor
		this.events = new ArrayList<>();
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Collection<Event> getEvents() {
		return events;
	}

	public void setEvents(Collection<Event> events) {
		this.events = events;
	}
}
