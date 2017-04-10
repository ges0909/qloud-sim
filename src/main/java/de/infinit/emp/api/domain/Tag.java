package de.infinit.emp.api.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import de.infinit.emp.Uuid;

@DatabaseTable(tableName = "tags")
public class Tag {
	@NotNull
	@DatabaseField(id = true)
	String uuid;

	@DatabaseField
	String label;

	@DatabaseField(defaultValue = "true")
	Boolean foreignUse;

	@ForeignCollectionField
	private Collection<Policy> policies;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private transient Sensor sensor;

	public Tag() {
		// ORMLite needs a no-arg constructor
		this.uuid = Uuid.next();
		this.policies = new ArrayList<>();
	}

	public Tag(User owner, String label, Boolean foreignUse) {
		this();
		this.label = label;
		this.foreignUse = foreignUse;
		this.policies.add(new Policy(this, owner.getUuid(), Policy.OWNER));
	}

	public String getUuid() {
		return uuid;
	}

	public String getLabel() {
		return label;
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

	/*
	 * returns the user uuid of the owner of the attached policy list
	 */
	public String getOwnerUuid() {
		for (Policy policy : policies) {
			if (policy.policyValue == Policy.OWNER) {
				return policy.getUserUuid();
			}
		}
		return null;
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
		Tag other = (Tag) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
