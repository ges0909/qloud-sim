package de.infinit.emp.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.google.gson.annotations.SerializedName;

@Entity
public class User2 {
	@NotNull
	@Id
	String uuid;

	@SerializedName("email")
	String email;

	@Pattern(regexp = "^[a-zA-Z0-9]{2,20}$")
	@SerializedName("username")
	String userName;

	@Pattern(regexp = "^.{1,50}$")
	@SerializedName("firstname")
	String firstName;

	@Pattern(regexp = "^.{1,50}$")
	@SerializedName("lastname")
	String lastName;

	@Pattern(regexp = "^.{1,100}$")
	@SerializedName("display_name")
	String displayName;

	@Pattern(regexp = "^.{5,50}$")
	String password;

	@NotNull
	@Pattern(regexp = "^[a-zA-Z0-9-]{1,50}$")
	String verification;
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVerification() {
		return verification;
	}

	public void setVerification(String verification) {
		this.verification = verification;
	}
}
