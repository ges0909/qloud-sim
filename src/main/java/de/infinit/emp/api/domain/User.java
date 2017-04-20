package de.infinit.emp.api.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.validation.constraints.Pattern;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {
	@DatabaseField(generatedId = true)
	UUID uuid;

	@Expose
	@SerializedName("email")
	@DatabaseField(unique = true)
	String email;

	@Expose
	@Pattern(regexp = "^[a-zA-Z0-9]{2,20}$")
	@SerializedName("username")
	@DatabaseField
	String userName;

	@Expose
	@Pattern(regexp = "^.{1,50}$")
	@SerializedName("firstname")
	@DatabaseField
	String firstName;

	@Expose
	@Pattern(regexp = "^.{1,50}$")
	@SerializedName("lastname")
	@DatabaseField
	String lastName;

	@Expose
	@Pattern(regexp = "^.{1,100}$")
	@SerializedName("display_name")
	@DatabaseField
	String displayName;

	@Expose
	@Pattern(regexp = "^.{5,50}$")
	@DatabaseField
	String password;

	@DatabaseField()
	UUID verification;

	@DatabaseField
	String partner;

	@DatabaseField(foreign = true, columnName = "tag_id")
	Tag tagAll;

	@ForeignCollectionField
	private Collection<Invitation> invitations;

	public User() {
		// ORMLite needs a no-arg constructor
		this.verification = UUID.randomUUID();
		this.invitations = new ArrayList<>();
	}

	public UUID getUuid() {
		return uuid;
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

	public UUID getVerification() {
		return verification;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public Tag getTagAll() {
		return tagAll;
	}

	public void setTagAll(Tag tagAll) {
		this.tagAll = tagAll;
	}

	public Collection<Invitation> getInvitations() {
		return invitations;
	}

	public void setInvitations(Collection<Invitation> invitations) {
		this.invitations = invitations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
