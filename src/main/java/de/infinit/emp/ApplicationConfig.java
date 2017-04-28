package de.infinit.emp;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.Mutable;

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
	Integer port();

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
	Integer recvInterval();

	@Key("sensor.default.delta")
	@DefaultValue("1")
	Long defaultDelta();

	@Key("sensor.number.threads")
	@DefaultValue("256")
	Integer numberOfThreads();

	@Key("event.timeout.seconds")
	@DefaultValue("55")
	Integer eventTimout();

	@Key("session.timeout.seconds")
	@DefaultValue("172800") /* 48 h */
	Integer sessionTimout();
}
