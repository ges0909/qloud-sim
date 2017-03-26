package de.infinit.emp;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Config {
	static PropertiesConfiguration config;

	public Config() throws ConfigurationException {
		Configurations configs = new Configurations();
		config = configs.properties(getClass().getResource("config.properties").getFile());
	}

	public static String getString(String name) {
		return config.getString(name);
	}

	public static int getInt(String name) {
		return config.getInt(name);
	}
}
