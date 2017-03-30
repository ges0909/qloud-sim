package de.infinit.emp.domain;

public class Session {
	String sid;
	String server;
	String partner;
	String key;
	String uuid; // user uuid in case of proxy session; otherwise null

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

	public String getUserUuid() {
		return uuid;
	}

	public void setUserUuid(String userUuid) {
		this.uuid = userUuid;
	}
	
	public boolean isPartnerSession() {
		return uuid == null;
	}
}
