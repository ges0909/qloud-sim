package de.infinit.emp;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class Global {
	public static final Gson GSON = new Gson();

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
}
