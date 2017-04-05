package de.infinit.emp;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Config.Sources;

@Sources({
	"file:${user.home}/.qloud-simulator.properties",
"classpath:de/infinit/emp/ApplicationConfig.properties" })
public interface ApplicationConfig extends Accessible, Mutable {
	@Key("device.pattern")
	@DefaultValue("[0-9A-Z]{5}-[0-9A-Z]{5}-[0-9A-Z]{5}-[0-9A-Z]{5}")
	String devicePattern();

	@Key("qloud.key")
	@DefaultValue("abcdefghijklmnopqrstuvwxyz")
	String key();

	@Key("qloud.partner")
	@DefaultValue("brightone")
	String partner();

	@Key("db.password")
	@DefaultValue("test")
	String password();

	@Key("db.url")
	@DefaultValue("jdbc:h2:mem:tests")
	String url();

	@Key("db.username")
	@DefaultValue("test")
	String username();
}
