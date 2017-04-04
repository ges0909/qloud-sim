package de.infinit.emp.api.model;

import de.infinit.emp.api.domain.Tag;

public class TagModel extends Model<Tag, String> {
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
}
