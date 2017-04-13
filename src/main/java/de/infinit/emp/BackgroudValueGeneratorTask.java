package de.infinit.emp;

import java.util.logging.Logger;

import de.infinit.emp.api.domain.Capability;
import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.model.CapabilityModel;
import de.infinit.emp.api.model.SensorModel;

public class BackgroudValueGeneratorTask implements Runnable {
	static final Logger log = Logger.getLogger(BackgroudValueGeneratorTask.class.getName());
	final SensorModel sensorModel = SensorModel.instance();
	final CapabilityModel capabilityModel = CapabilityModel.instance();

	@Override
	public void run() {
		log.info("*** background task ***");
		for (Sensor sensor : sensorModel.queryForAll()) {
			for (Capability c : sensor.getCapabilities()) {
				Integer value = c.getValue();
				c.setValue(value + 1);
				if (capabilityModel.update(c) == null) {
					log.severe("sensor: " + sensor.getSdevice() + ", capability: " + c.getOrder()
					+ ", : periodic value update failed");
				}
			}
		}
	}
}