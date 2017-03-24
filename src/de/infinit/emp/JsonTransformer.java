package de.infinit.emp;

import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

	private static final Logger LOGGER = Logger.getLogger(JsonTransformer.class.getName());
	// private static Gson gson = new Gson();
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public String render(Object model) throws Exception {
		String json = gson.toJson(model);
		LOGGER.info(json);
		return json;
	}
}
