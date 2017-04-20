package de.infinit.emp.api.controller;

import java.util.List;
import java.util.UUID;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import de.infinit.emp.Status;
import de.infinit.emp.api.domain.Policy;
import de.infinit.emp.api.domain.Tag;
import de.infinit.emp.api.domain.User;
import de.infinit.emp.api.model.PolicyModel;
import de.infinit.emp.api.model.TagModel;
import de.infinit.emp.api.model.UserModel;
import spark.Request;
import spark.Response;

public class SignupController extends Controller {
	private static SignupController instance = null;
	final UserModel userModel = UserModel.instance();
	final TagModel tagModel = TagModel.instance();
	final PolicyModel policyModel = PolicyModel.instance();

	private SignupController() {
		super();
	}

	public static SignupController instance() {
		if (instance == null) {
			instance = new SignupController();
		}
		return instance;
	}

	class ReserveUserAccountRequest {
		class Obj {
			List<String> companyId;
		}

		Obj info;
	}

	class AddUserAccountRequest {
		@Expose
		String email;
		@Expose
		String username;
		@Expose
		String firstname;
		@Expose
		String lastname;
		@Expose
		@SerializedName("display_name")
		String displayName;
		@Expose
		String password;
		@Expose
		UUID verification;
	}

	public Object reserveAccount(Request request, Response response) {
		ReserveUserAccountRequest body = decode(request.body(), ReserveUserAccountRequest.class);
		User user = new User();
		if (userModel.create(user) == null) {
			return fail();
		}
		return result("uuid", user.getUuid(), "verification", user.getVerification(), "info", body.info);
	}

	public Object addAccount(Request request, Response response) {
		AddUserAccountRequest req = decode(request.body(), AddUserAccountRequest.class);
		User user = userModel.findFirstByColumn("verification", req.verification);
		if (user == null) {
			return status(Status.UNKNOWN_VERIFICATION);
		}
		user.setDisplayName(req.displayName);
		user.setEmail(req.email);
		user.setFirstName(req.firstname);
		user.setLastName(req.lastname);
		user.setPassword(req.password);
		user.setUserName(req.username);
		user.setPartner(config.partner());
		// create new tag for user's 'tag_all'
		Tag tag = new Tag(user, null, null);
		// create owner policy ...
		Policy policy = new Policy(tag, user.getUuid(), Policy.OWNER);
		if (policyModel.create(policy) == null) {
			return fail();
		}
		// ... and add to tag
		tag.getPolicies().add(policy);
		// create tag and ...
		if (tagModel.create(tag) == null) {
			return fail();
		}
		// ... assign to user's 'tag_all'
		user.setTagAll(tag);
		if (userModel.update(user) == null) {
			return fail();
		}
		return ok();
	}
}
