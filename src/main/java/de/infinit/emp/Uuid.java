package de.infinit.emp;

import java.util.UUID;

public class Uuid {
	public static String next() {
		return UUID.randomUUID().toString();
	}
}
