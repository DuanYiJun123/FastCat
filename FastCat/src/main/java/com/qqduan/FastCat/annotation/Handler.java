package com.qqduan.FastCat.annotation;

import com.qqduan.FastCat.handler.HttpMethod;

public @interface Handler {
	public String value();

	public HttpMethod[] method() default { HttpMethod.GET };
}
