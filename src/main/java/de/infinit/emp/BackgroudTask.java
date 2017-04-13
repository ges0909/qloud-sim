package de.infinit.emp;

import java.util.logging.Logger;
import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.model.SensorModel;

public class BackgroudTask implements Runnable {
	static final Logger log = Logger.getLogger(BackgroudTask.class.getName());
	final SensorModel sensorModel = SensorModel.instance();

	@Override
	public void run() {
		log.info("*** background task ***");
		for (Sensor sensor : sensorModel.queryForAll()) {
		}
	}
}