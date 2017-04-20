package de.infinit.emp;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Config.Sources;

@Sources({ 
	"file:${user.home}/.qs.config.properties", 
	"classpath:qs.default.config.properties" })
public interface ApplicationConfig extends Accessible, Mutable {
	@Key("db.url")
	@DefaultValue("jdbc:h2:mem:tests")
	String url();

	@Key("db.password")
	@DefaultValue("test")
	String password();

	@Key("db.username")
	@DefaultValue("test")
	String username();

	@Key("spark.port")
	@DefaultValue("4567")
	int port();

	@Key("qloud.key")
	@DefaultValue("abcdefghijklmnopqrstuvwxyz")
	String key();

	@Key("qloud.partner")
	@DefaultValue("brightone")
	String partner();

	@Key("sensor.pattern")
	@DefaultValue("[0-9A-Z]{5}-[0-9A-Z]{5}-[0-9A-Z]{5}-[0-9A-Z]{5}")
	String devicePattern();

	@Key("sensor.recv.interval")
	@DefaultValue("900")
	int recvInterval();
	
	@Key("sensor.default.delta")
	@DefaultValue("1")
	long defaultDelta();
	
	@Key("sensor.number.threads")
	@DefaultValue("256")
	int numberOfThreads();
}
