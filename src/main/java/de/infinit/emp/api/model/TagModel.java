package de.infinit.emp.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.infinit.emp.api.domain.Policy;
import de.infinit.emp.api.domain.Tag;
import de.infinit.emp.api.domain.User;

public class TagModel extends Model<Tag, UUID> {
	private static TagModel instance = null;

	private TagModel() {
		super(Tag.class);
	}

	public static TagModel instance() {
		if (instance == null) {
			instance = new TagModel();
		}
		return instance;
	}

	public int delete(Tag tag) {
		tag.getPolicies().clear();
		return super.delete(tag.getUuid());
	}

	public List<Tag> queryForUserTags(User user) {
		List<Tag> tags = new ArrayList<>();
		for (Tag tag : super.queryForAll()) {
			for (Policy policy : tag.getPolicies()) {
				if (policy.getUserUuid().equals(user.getUuid())) {
					tags.add(tag);
				}
			}
		}
		return tags;
	}
}
