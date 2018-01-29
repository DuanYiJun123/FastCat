package com.qqduan.FastCat.http;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.qqduan.FastCat.core.Result;
import com.qqduan.FastCat.exception.ErrorCodeException;

public class ResultBuilder {
	public ResultBuilder() {
	}

	private static final String RESULT = "result";
	private static final String INFO = "info";

	public static JSONObject build(int code) {
		JSONObject json = new JSONObject();
		json.put(RESULT, String.valueOf(code));
		json.put(INFO, ErrorCode.getErrorMsg(code));
		return json;
	}

	public static JSONObject build(int code, JSONObject json) {
		json.put(RESULT, String.valueOf(code));
		json.put(INFO, ErrorCode.getErrorMsg(code));
		return json;
	}

	public static Result buildResult(int code) {
		Result result = new Result();
		result.setAttachment(Result.RESPONSE, build(code));
		return result;
	}

	public static Result buildResult(int code, JSONObject json) {
		Result result = new Result();
		result.setAttachment(Result.RESPONSE, build(code, json));
		return result;
	}

	public static void addErrorMsg(int code, String msg) {
		if (ErrorCode.map.containsKey(code)) {
			throw new ErrorCodeException("error code already exists");
		}
	}

	private static class ErrorCode {
		private static Map<Integer, String> map = new HashMap<>();

		static {
			map.put(0, "success");
		}

		public static String getErrorMsg(int code) {
			if (map.containsKey(code)) {
				return map.get(code);
			} else {
				return "Unknow error";
			}
		}
	}
}
