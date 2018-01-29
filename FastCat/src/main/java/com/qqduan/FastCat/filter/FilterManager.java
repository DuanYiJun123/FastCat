package com.qqduan.FastCat.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.qqduan.FastCat.annotation.Filter;
import com.qqduan.FastCat.core.Definiens;
import com.qqduan.FastCat.core.Invocation;
import com.qqduan.FastCat.core.Result;
import com.qqduan.FastCat.interfaces.IFilter;
import com.qqduan.FastCat.interfaces.IInvoker;
import com.qqduan.FastCat.util.FileUtil;

public class FilterManager {
	private static final Logger LOGGER = Logger.getLogger(FilterManager.class);
	private static List<IFilter> filters;

	public FilterManager() {
	}

	public static void init() {
		List<BaseFilter> inners = new ArrayList<>();
		String path = FileUtil.getAppRoot() + File.separator + "src" + File.separator + "main" + File.separator + "java"
				+ File.separator + Definiens.FILTER_PACKAGE.replaceAll("\\.", "/");

		LOGGER.info("scaning the package " + path);
		_scan(new File(path), "", inners);
	}

	private static void _scan(File root, String parent, List<BaseFilter> inners) {
		FileUtil.subFile(root).forEach(p -> {
			String name = p.getName();
			addFilter(parent + "." + name, inners);
		});

		FileUtil.subDir(root).forEach(p -> {
			String name = p.getName();
			_scan(p, parent + "", inners);
		});
	}

	private static void addFilter(String name, List<BaseFilter> inners) {
		try {
			Class<?> clz = Class.forName(Definiens.FILTER_PACKAGE + name.replace(".java", ""));
			if (!IFilter.class.isAssignableFrom(clz)) {
				return;
			}

			Filter filter = clz.getAnnotation(Filter.class);

			if (filter == null) {
				return;
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static IInvoker link(final IInvoker invoker) {
		IInvoker last = invoker;
		if (!filters.isEmpty()) {
			for (int i = filters.size() - 1; i >= 0; i--) {
				IFilter filter = filters.get(i);
				IInvoker next = last;
				last = new IInvoker() {
					@Override
					public Result invoke(Invocation invocation) {
						return filter.invoke(next, invocation);
					}
				};
			}
		}
		return last;
	}

}
