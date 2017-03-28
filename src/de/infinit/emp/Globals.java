package de.infinit.emp;

import java.util.UUID;

import com.google.gson.Gson;

public class Globals {
	public static final Gson GSON = new Gson();

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
}
