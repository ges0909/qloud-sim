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
	
	@NotNull
	@DatabaseField
	String owner;
	
	@DatabaseField
	String label;

	@DatabaseField(defaultValue = "true")
	Boolean foreignUse;
	
	@ForeignCollectionField
	private Collection<Policy> policies;

	public Tag() {
		// ORMLite needs a no-arg constructor
		this.uuid = Uuid.next();
		this.policies = new ArrayList<>();
	}
	
	public Tag(String owner, String label, Boolean foreignUse) {
		this();
		this.owner = owner;
		this.label = label;
		this.foreignUse = foreignUse;
		this.policies.add(new Policy(this, owner, 4/* owner */));
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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
}
