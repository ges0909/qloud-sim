package de.infinit.emp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public String render(Object model) throws Exception {
		return GSON.toJson(model);
	}
}
