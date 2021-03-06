package com.qqduan.FastCat.handler;

import com.qqduan.FastCat.interfaces.IInvoker;

public class BaseHandler {
	private HttpMethod[] methods;
	private IInvoker invoker;

	public BaseHandler(HttpMethod[] methods, IInvoker invoker) {
		this.methods = methods;
		this.invoker = invoker;
	}
	
	public HttpMethod[] getMethods() {
		return methods;
	}

	public IInvoker getInvoker() {
		return invoker;
	}
}
