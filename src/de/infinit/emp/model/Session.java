package de.infinit.emp.model;

public class Session {
	private boolean auth;
	private String email;
	private String key;
	private String partner;
	private String password;
	private String server;
	private String sid;
	private String username;

	public String getEmail() {
		return email;
	}

	public String getKey() {
		return key;
	}

	public String getPartner() {
		return partner;
	}

	public String getPassword() {
		return password;
	}

	public String getServer() {
		return server;
	}
	public String getSid() {
		return sid;
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean isAuth() {
		return auth;
	}
	
	public void setAuth(boolean auth) {
		this.auth = auth;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
