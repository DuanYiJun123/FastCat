package com.qqduan.FastCat.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Result {
	public static final String RESPONSE = "constants_response";
	private Map<String, Object> attachment = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T getAttachment(String key) {
		return (T) attachment.get(key);
	}

	public void setAttachment(String key, Object value) {
		attachment.put(key, value);
	}
}
