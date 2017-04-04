package de.infinit.emp.api.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

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
		String label;
		@SerializedName("foreign_use")
		boolean foreignUse;
		@SerializedName("policy")
		Map<String, Integer> policies;
	}

	class UpdateTagRequest {
		class PolicyList {
			Map<String, Integer> update;
			List<String> delete;
		}

		String label;
		@SerializedName("foreign_use")
		boolean foreignUse;
		@SerializedName("policy")
		PolicyList policies;
	}

	private Map<String, Integer> getPolicies(Tag tag) {
		Map<String, Integer> policies = new HashMap<>();
		for (Policy policy : tag.getPolicies()) {
			policies.put(policy.getUuid(), policy.getPolicy());
		}
		return policies;
	}

	private void updateExistingPolicies(Tag tag, Map<String, Integer> policies) {
		for (Policy policy : tag.getPolicies()) {
			Integer value = policies.get(policy.getUuid());
			if (value != null) {
				policy.setPolicy(value);
			}
		}
	}

	private void createMissingPolicies(Tag tag, Map<String, Integer> policies) {
		for (Map.Entry<String, Integer> entry : policies.entrySet()) {
			String userUuid = entry.getKey();
			if (tag.getPolicies().stream().noneMatch(p -> p.getUuid().equals(userUuid))) {
				Integer value = policies.get(userUuid);
				tag.getPolicies().add(new Policy(tag, userUuid, value));
			}
		}
	}

	private void deletePolicies(Tag tag, List<String> policies) {
		for (String userUuid : policies) {
			Optional<Policy> o = tag.getPolicies().stream().filter(p -> p.getUuid().equals(userUuid)).findFirst();
			if (o.isPresent()) {
				policies.remove(o.get());
			}
		}
	}

	public Object createTag(Request request, Response response) {
		if (!isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User own = userModel.queryForId(session.getUser());
		if (own == null) {
			return fail();
		}
		CreateTagRequest req = decode(request.body(), CreateTagRequest.class);
		Tag tag = new Tag(own.getUuid(), req.label, req.foreignUse);
		Collection<Policy> policies = tag.getPolicies();
		for (String uuid : req.policies.keySet()) {
			policies.add(new Policy(tag, uuid, req.policies.get(uuid)));
		}
		tag.setPolicies(policies);
		if (tagModel.create(tag) == null) {
			return fail();
		}
		return result("uuid", tag.getUuid());
	}

	// POST /api/tag/:uuid
	public Object updateTag(Request request, Response response) {
		if (!isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User own = userModel.queryForId(session.getUser());
		if (own == null) {
			return fail();
		}
		String uuid = request.params(":uuid");
		Tag tag = tagModel.queryForId(uuid);
		if (tag == null) {
			return status(Status.WRONG_TAG);
		}
		if (!own.getUuid().equals(tag.getOwner())) {
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
		if (!isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User own = userModel.queryForId(session.getUser());
		if (own == null) {
			return status(Status.WRONG_USER);
		}
		String uuid = request.params(":uuid");
		Tag tag = tagModel.queryForId(uuid);
		if (tag == null) {
			return status(Status.WRONG_TAG);
		}
		if (!own.getUuid().equals(tag.getOwner())) {
			return status(Status.ACCESS_DENIED);
		}
		return result("owner", tag.getOwner(), "label", tag.getLabel(), "foreign_use", tag.isForeignUse(), "policy",
				getPolicies(tag));
	}

	public Object getTags(Request request, Response response) {
		if (!isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User own = userModel.queryForId(session.getUser());
		if (own == null) {
			return status(Status.WRONG_USER);
		}
		List<Tag> ownerTags = tagModel.findByColumn("owner", own.getUuid());
		Map<String, Object> tags = new HashMap<>();
		for (Tag tag : ownerTags) {
			tags.put(tag.getUuid(), Json.obj("owner", tag.getOwner(), "label", tag.getLabel(), "foreign_use",
					tag.isForeignUse(), "policy", getPolicies(tag)));
		}
		return result("tag", tags);
	}

	public Object deleteTag(Request request, Response response) {
		if (!isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User own = userModel.queryForId(session.getUser());
		if (own == null) {
			return status(Status.WRONG_USER);
		}
		String uuid = request.params(":uuid");
		Tag tag = tagModel.queryForId(uuid);
		if (tag == null) {
			return status(Status.WRONG_TAG);
		}
		if (!own.getUuid().equals(tag.getOwner())) {
			return status(Status.ACCESS_DENIED);
		}
		if (tagModel.delete(uuid) != 1) {
			return fail();
		}
		return ok();
	}

	// GET /api/tag/:uuid/object
	public Object getTaggedObjects(Request request, Response response) {
		if (!isProxySession(request)) {
			return status(Status.NO_AUTH);
		}
		Session session = request.session().attribute(SessionController.QLOUD_SESSION);
		User own = userModel.queryForId(session.getUser());
		if (own == null) {
			return status(Status.WRONG_USER);
		}
		String uuid = request.params(":uuid");
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
		Tag tagAll = own.getTagAll();
		List<Sensor> sensors = sensorModel.queryForAll();
		Map<String, Object> objects = new HashMap<>();
		for (Sensor sensor : sensors) {
			if (sensor.getTag().getUuid().equals(tagAll.getUuid())) {
				objects.put(sensor.getUuid(), Json.obj());
			}
		}
		return result("object", objects);
	}
}
