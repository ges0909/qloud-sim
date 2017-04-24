package de.infinit.emp.api.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

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

	@DatabaseField(canBeNull = false, dataType = DataType.DATE_LONG)
	Date expiresAt;

	@ForeignCollectionField
	Collection<Event> events;

	public Session() {
		// ORMLite needs a no-arg constructor
		this.events = new ArrayList<>();
	}

	public Session(@NotNull String server, Date expiresAt) {
		this();
		this.server = server;
		this.expiresAt = expiresAt;
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

	public Date getExpiresAt() {
		return expiresAt;
	}

	public boolean isExpired() {
		return this.expiresAt.toInstant().isBefore(Instant.now());
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

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}
}
