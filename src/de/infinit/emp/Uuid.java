package de.infinit.emp;

import java.util.UUID;

public class Uuid {
	public static String get() {
		return UUID.randomUUID().toString();
	}
}
