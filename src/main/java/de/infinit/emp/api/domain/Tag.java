package de.infinit.emp.api.domain;

import javax.validation.constraints.NotNull;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

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
	boolean foreignUse;
	
	@ForeignCollectionField
	private ForeignCollection<Policy> policies;

	public Tag() {
		// ORMLite needs a no-arg constructor
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public ForeignCollection<Policy> getPolicies() {
		return policies;
	}

	public void setPolicies(ForeignCollection<Policy> policies) {
		this.policies = policies;
	}
}
