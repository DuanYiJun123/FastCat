package com.qqduan.FastCat.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Invocation {
	private Map<String,Object> attachment=new ConcurrentHashMap<>();
	public static final String MAPPING = "constants_mapping";
	public static final String REQUEST = "constants_request";
	public static final String UPLOAD_FILES = "constants_upload_files";
	
	@SuppressWarnings("unchecked")
	public <T> T getAttachment(String key) {
		return (T) attachment.get(key);
	}

	public <T> void setAttachment(String key, T value) {
		attachment.put(key, value);
	}
}
