package com.qqduan.FastCat.interfaces;

import com.qqduan.FastCat.core.Invocation;
import com.qqduan.FastCat.core.Result;

public interface IFilter {
	public Result invoke(IInvoker invoker, Invocation invocation);
}
