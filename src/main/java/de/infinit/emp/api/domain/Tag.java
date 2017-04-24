package de.infinit.emp.api.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

public class Tag {
	@DatabaseField(generatedId = true)
	UUID uuid;

	@DatabaseField(foreign = true, canBeNull = false)
	User owner;

	@DatabaseField
	String label;

	@DatabaseField(defaultValue = "true")
	Boolean foreignUse;

	@ForeignCollectionField
	private Collection<Policy> policies;

	public Tag() {
		// ORMLite needs a no-arg constructor
		this.policies = new ArrayList<>();
	}

	public Tag(User owner, String label, Boolean foreignUse) {
		this();
		this.owner = owner;
		this.label = label;
		this.foreignUse = foreignUse;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getLabel() {
		return label;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isForeignUse() {
		return foreignUse;
	}

	public void setForeignUse(boolean foreignUse) {
		this.foreignUse = foreignUse;
	}

	public Collection<Policy> getPolicies() {
		return policies;
	}

	public void setPolicies(Collection<Policy> policies) {
		this.policies = policies;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Tag other = (Tag) obj;
		if (uuid == null) {
			if (other.uuid != null) {
				return false;
			}
		} else if (!uuid.equals(other.uuid)) {
			return false;
		}
		return true;
	}
}
