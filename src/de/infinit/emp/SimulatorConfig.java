package de.infinit.emp;

import org.aeonbits.owner.Config;

public interface SimulatorConfig extends Config {
	@Key("db.url")
	@DefaultValue("jdbc:h2:mem:test")
	String url();

	@Key("db.username")
	@DefaultValue("test")
	String username();

	@Key("db.password")
	@DefaultValue("test")
	String password();
	
	@Key("device.code.pattern")
	@DefaultValue("SIMUL-.*")
	String deviceCodePattern();
}
