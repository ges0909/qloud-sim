package de.infinit.emp.api.controller;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import de.infinit.emp.Status;
import de.infinit.emp.api.domain.Policy;
import de.infinit.emp.api.domain.Sensor;
import de.infinit.emp.api.domain.Session;
import de.infinit.emp.api.domain.Tag;
import de.infinit.emp.api.domain.User;
import de.infinit.emp.api.model.PolicyModel;
import de.infinit.emp.api.model.SensorModel;
import de.infinit.emp.api.model.TagModel;
import de.infinit.emp.api.model.TagSensorModel;
import de.infinit.emp.api.model.UserModel;
import de.infinit.emp.utils.Json;
import spark.Request;
import spark.Response;

public class TagController extends Controller {
	private static TagController instance = null;
	static final Logger log = Logger.getLogger(TagController.class.getName());
	final TagModel tagModel = TagModel.instance();
	final UserModel userModel = UserModel.instance();
	final PolicyModel policyModel = PolicyModel.instance();
	final SensorModel sensorModel = SensorModel.instance();
	final TagSensorModel tagSensorModel = TagSensorModel.instance();

	private TagController() {
		super();
	}

	public static TagController instance() {
		if (instance == null) {
			instance = new TagController();
		}
		return instance;
	}

	class CreateTagRequest {
		@Expose
		String label;
		@Expose
		@SerializedName("foreign_use")
		boolean foreignUse;
		@Expose
		@SerializedName("policy")
		Map<UUID, Integer> policies;
	}

	class UpdateTagRequest {
		class PolicyList {
			@Expose
			Map<UUID, Integer> update;
			@Expose
			List<UUID> delete;
		}

		@Expose
		String label;
		@Expose
		@SerializedName("foreign_use")
		boolean foreignUse;
		@Expose
		@SerializedName("policy")
		PolicyList policies;
	}

	private Map<UUID, Integer> getPolicies(Tag tag) {
		Map<UUID, Integer> policies = new HashMap<>();
		for (Policy policy : tag.getPolicies()) {
			policies.put(policy.getUserUuid(), policy.getPolicy());
		}
		return policies;
	}

	private void updateExistingPolicies(Tag tag, Map<UUID, Integer> policies) {
		for (Policy policy : tag.getPolicies()) {
			Integer value = policies.get(policy.getUserUuid());
			if (value != null) {
				policy.setPolicy(value);
			}
		}
	}

	private void createMissingPolicies(Tag tag, Map<UUID, Integer> policies) {
		for (Map.Entry<UUID, Integer> entry : policies.entrySet()) {
			UUID userUuid = entry.getKey();
			if (tag.getPolicies().stream().noneMatch(p -> p.getUserUuid().equals(userUuid))) {
				Integer value = policies.get(userUuid);
				tag.getPolicies().add(new Policy(tag, userUuid, value));
			}
		}
	}

	private void deletePolicies(Tag tag, List<UUID> policies) {
		for (UUID userUuid : policies) {
			Optional<Policy> o = tag.getPolicies().stream().filter(p -> p.getUserUuid().equals(userUuid)).findFirst();
			if (o.isPresent()) {
				policies.remove(o.get());
			}
		}
	}

	public Object createTag(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		CreateTagRequest req = decode(request.body(), CreateTagRequest.class);
		Tag tag = new Tag(user, req.label, req.foreignUse);
		if (tagModel.create(tag) == null) {
			return fail();
		}
		Collection<Policy> policies = tag.getPolicies();
		for (UUID uuid : req.policies.keySet()) {
			Policy policy = new Policy(tag, uuid, req.policies.get(uuid));
			if (policyModel.create(policy) == null) {
				return fail();
			}
			policies.add(policy);
		}
		return result("uuid", tag.getUuid());
	}

	// POST /api/tag/:uuid
	public Object updateTag(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		UUID uuid = UUID.fromString(request.params(":uuid"));
		Tag tag = tagModel.queryForId(uuid);
		if (tag == null) {
			return status(Status.WRONG_TAG);
		}
		if (!user.equals(tag.getOwner())) {
			return status(Status.ACCESS_DENIED);
		}
		UpdateTagRequest req = decode(request.body(), UpdateTagRequest.class);
		tag.setLabel(req.label);
		tag.setForeignUse(req.foreignUse);
		if (req.policies != null && req.policies.update != null) {
			updateExistingPolicies(tag, req.policies.update);
			createMissingPolicies(tag, req.policies.update);
		}
		if (req.policies != null && req.policies.delete != null) {
			deletePolicies(tag, req.policies.delete);
		}
		if (tagModel.update(tag) == null) {
			fail();
		}
		return ok();
	}

	// GET /api/tag
	// Get list of tags associated with current user.
	public Object getTag(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		UUID uuid = UUID.fromString(request.params(":uuid"));
		Tag tag = tagModel.queryForId(uuid);
		if (tag == null) {
			return status(Status.WRONG_TAG);
		}
		if (!user.equals(tag.getOwner())) {
			return status(Status.ACCESS_DENIED);
		}
		return result("owner", tag.getOwner().getUuid(), "label", tag.getLabel(), "foreign_use", tag.isForeignUse(),
				"policy", getPolicies(tag));
	}

	public Object getTags(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		List<Tag> ownerTags = tagModel.queryForUserTags(user);
		Map<String, Object> tags = new HashMap<>();
		for (Tag tag : ownerTags) {
			tags.put(tag.getUuid().toString(), Json.obj("owner", tag.getOwner().getUuid(), "label", tag.getLabel(),
					"foreign_use", tag.isForeignUse(), "policy", getPolicies(tag)));
		}
		return result("tag", tags);
	}

	public Object deleteTag(Request request, Response response) {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		UUID uuid = UUID.fromString(request.params(":uuid"));
		Tag tag = tagModel.queryForId(uuid);
		if (tag == null) {
			return status(Status.WRONG_TAG);
		}
		if (!user.equals(tag.getOwner())) {
			return status(Status.ACCESS_DENIED);
		}
		for (Policy policy : tag.getPolicies()) {
			if (policyModel.delete(policy.getId()) != 1) {
				return fail();
			}
		}
		if (tagModel.delete(uuid) != 1) {
			return fail();
		}
		return ok();
	}

	// GET /api/tag/:uuid/object
	public Object getTaggedObjects(Request request, Response response) throws SQLException {
		Session session = request.session().attribute(SessionController.SESSION);
		User user = session.getUser();
		if (user == null) {
			return status(Status.NO_AUTH);
		}
		UUID uuid = UUID.fromString(request.params(":uuid"));
		Tag tag = tagModel.queryForId(uuid);
		if (tag == null) {
			return status(Status.WRONG_TAG);
		}
		String filter = request.queryParams("filter");
		if (filter != null && !filter.equals("sensor")) {
			log.warning("query parameter 'filter': value '" + filter + "' not supported");
			return fail();
		}
		String count = request.queryParams("count");
		if (count != null) {
			log.warning("query parameter 'count': ignored");
		}
		List<Sensor> sensors = tagSensorModel.findSensorsByTag(tag);
		Map<UUID, Object> objects = new HashMap<>();
		for (Sensor sensor : sensors) {
			objects.put(sensor.getUuid(), Json.obj());
		}
		return result("object", objects);
	}
}
