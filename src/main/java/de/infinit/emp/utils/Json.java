package de.infinit.emp.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Json {
	public static List<Object> arr(Object... values) {
		List<Object> list = new ArrayList<>();
		for (Object value : values) {
			list.add(value);
		}
		return list;
	}

	public static Map<String, Object> obj(Object... keyValuePairs) {
		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < keyValuePairs.length; i = i + 2) {
			map.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
		}
		return map;
	}
}
