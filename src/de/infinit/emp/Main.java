package de.infinit.emp;

import de.infinit.emp.rest.SessionController;
import de.infinit.emp.rest.SessionService;

public class Main {
	public static void main(String[] args) {
		new SessionController(new SessionService());
	}
}
