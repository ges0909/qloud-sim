package de.infinit.emp.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Json {
	@SuppressWarnings("unchecked")
	public static <T> List<T> arr(T... values) {
		List<T> list = new ArrayList<>();
		for (T value : values) {
			list.add(value);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> obj(Object... keyValuePairs) {
		Map<String, T> map = new HashMap<>();
		for (int i = 0; i < keyValuePairs.length; i = i + 2) {
			map.put((String) keyValuePairs[i], (T) keyValuePairs[i + 1]);
		}
		return map;
	}
}
