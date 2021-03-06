package com.qqduan.FastCat.filter;

import com.qqduan.FastCat.interfaces.IFilter;

public class BaseFilter {
	private int index;
	private IFilter filter;

	public BaseFilter(int index, IFilter filter) {
		this.index = index;
		this.filter = filter;
	}

	public int getIndex() {
		return index;
	}

	public IFilter getFilter() {
		return filter;
	}
}
