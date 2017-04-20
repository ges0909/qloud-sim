package de.infinit.emp.api.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import de.infinit.emp.Status;
import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.domain.Session;
import de.infinit.emp.api.domain.Tag;
import de.infinit.emp.api.domain.TagSensor;
import de.infinit.emp.api.domain.User;
import de.infinit.emp.api.model.SensorModel;
import de.infinit.emp.api.model.TagModel;
import de.infinit.emp.api.model.TagSensorModel;
import de.infinit.emp.api.model.UserModel;
import spark.Request;
import spark.Response;

public class ObjectController extends Controller {
	private static ObjectController instance = null;
	static final Logger log = Logger.getLogger(PartnerController.class.getName());
	final UserModel userModel = UserModel.instance();
	final SensorModel sensorModel = SensorModel.instance();
	final TagModel tagModel = TagModel.instance();
	final TagSensorModel tagSensorModel = TagSensorModel.instance();

	class UpdateTagAttchamentRequest {
		List<UUID> add;
		List<UUID> delete;
	}

	private ObjectController() {
		super();
	}

	public static ObjectController instance() {
		if (instance == null) {
			instance = new ObjectController();
		}
		return instance;
	}

	private boolean containsUnknownTag(List<UUID> tagUuids) {
		for (UUID uuid : tagUuids) {
			Tag tag = tagModel.queryForId(uuid);
			if (tag == null) {
				return true;
			}
		}
		return false;
	}

	// POST /api/object/:uuid/tag
	public Object updateTagAttachment(Request request, Response response) throws SQLException {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		// get all sensors belonging the user
		Tag tagAll = user.getTagAll();
		List<Sensor> sensors = sensorModel.queryForTaggedWith(tagAll);
		// find sensor requested to be tagged
		String sensorUuid = request.params(":uuid");
		Optional<Sensor> optional = sensors.stream().filter(s -> s.getUuid().equals(sensorUuid)).findFirst();
		if (!optional.isPresent()) {
			return status(Status.WRONG_OBJECT);
		}
		Sensor sensor = optional.get();
		// check input
		UpdateTagAttchamentRequest req = decode(request.body(), UpdateTagAttchamentRequest.class);
		if (req.add != null && containsUnknownTag(req.add)) {
			return status(Status.WRONG_TAG);
		}
		if (req.delete != null && containsUnknownTag(req.delete)) {
			return status(Status.WRONG_TAG);
		}
		//
		if (req.add != null) {
			for (UUID tagUuid : req.add) {
				Tag tag = tagModel.queryForId(tagUuid);
				TagSensor tagSensor = new TagSensor(tag, sensor);
				tagSensorModel.create(tagSensor);
			}
		}
		if (req.delete != null) {
			for (UUID tagUuid : req.delete) {
				Tag tag = tagModel.queryForId(tagUuid);
				TagSensor tagSensor = new TagSensor(tag, sensor);
				tagSensorModel.delete(tagSensor.getId());
			}
		}
		return ok();
	}
}
