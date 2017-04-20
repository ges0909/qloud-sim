package de.infinit.emp.api.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "sessions")
public class Session {
	@DatabaseField(generatedId = true)
	UUID sid;

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

	public UUID getSid() {
		return sid;
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

	public Collection<Event> getSubcribedEvents() {
		return getEvents();
	}

	public void setEvents(Collection<Event> events) {
		this.events = events;
	}
}
