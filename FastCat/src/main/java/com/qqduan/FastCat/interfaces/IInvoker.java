package com.qqduan.FastCat.interfaces;

import com.qqduan.FastCat.core.Invocation;
import com.qqduan.FastCat.core.Result;

public interface IInvoker {
	public Result invoke(Invocation invocation);
}
