package de.infinit.emp.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "sensors")
public class Sensor {
	@NotNull
	@DatabaseField(id = true)
	private String uuid;

	@NotNull
	@Pattern(regexp = "^.{10,50}$")
	@DatabaseField(unique = true, canBeNull = false)
	private String code;

	@Pattern(regexp = "^.{0,200}$")
	@DatabaseField
	private String description;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
