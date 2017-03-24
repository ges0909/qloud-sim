package de.infinit.emp;

import de.infinit.emp.controller.SensorController;
import de.infinit.emp.controller.SessionController;

public class Main {
	public static void main(String[] args) {
		new SessionController();
		new SensorController();
	}
}
