package de.infinit.emp;

import java.io.IOException;
import java.sql.SQLException;

import de.infinit.emp.controller.SensorController;
import de.infinit.emp.controller.SessionController;

public class Main {
	public static void main(String[] args) throws IOException, SQLException {
		new SessionController();
		new SensorController();
	}
}
