package de.infinit.emp.api.domain;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
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

	@DatabaseField
	String user; // user uuid in case of proxy session; otherwise null

	public Session() {
		// ORMLite needs a no-arg constructor
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
