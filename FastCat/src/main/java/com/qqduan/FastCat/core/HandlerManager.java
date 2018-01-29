package com.qqduan.FastCat.core;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.qqduan.FastCat.annotation.Handler;
import com.qqduan.FastCat.annotation.Service;
import com.qqduan.FastCat.exception.MySystemException;
import com.qqduan.FastCat.filter.FilterManager;
import com.qqduan.FastCat.handler.BaseHandler;
import com.qqduan.FastCat.handler.HttpMethod;
import com.qqduan.FastCat.interfaces.IInvoker;
import com.qqduan.FastCat.interfaces.IService;
import com.qqduan.FastCat.util.FileUtil;

public class HandlerManager {
	private static final Logger LOGGER = Logger.getLogger(HandlerManager.class);
	private static ConcurrentHashMap<String, BaseHandler> workers = new ConcurrentHashMap<>();

	public static void init() {
		String path = FileUtil.getAppRoot() + File.separator + "src" + File.separator + "main" + File.separator + "java"
				+ File.separator + Definiens.SERVICE_PACKAGE.replaceAll("\\.", "/");

		_scan(new File(path), "");
		LOGGER.info("scan the package to find handler in " + path);

		workers.forEach((k, v) -> {
			String methods = "";
			for (HttpMethod method : v.getMethods()) {
				methods += " " + method.name();
				LOGGER.info("handler of " + k + " is loaded , method = {" + methods + " }");
			}
		});
	}

	private static void _scan(File root, String parent) {
		FileUtil.subFile(root).forEach(p -> {
			addworker(parent + "." + p.getName());
		});

		FileUtil.subDir(root).forEach(p -> {
			_scan(p, parent + "." + p.getName());
		});
	}

	private static void addworker(String name) {
		try {
			Class<?> clz = Class.forName(Definiens.SERVICE_PACKAGE + name.replace(".java", ""));
			if (!clz.isAssignableFrom(IService.class)) {
				return;
			}

			Service annotation = clz.getAnnotation(Service.class);
			if (annotation == null) {
				return;
			}
			IService service = ApplicationContext.instance().getService((Class<IService>) clz);

			String value = annotation.value();

			Method[] methods = clz.getMethods();
			for (Method method : methods) {
				Handler handler = method.getAnnotation(Handler.class);
				if (handler != null) {
					String hvalue = handler.value();
					IInvoker invoker = FilterManager.link(invocation -> {
						try {
							return (Result) method.invoke(service, invocation);
						} catch (Exception e) {
							throw new MySystemException(e);
						}
					});
					workers.put(hvalue + value, new BaseHandler(handler.method(), invoker));
				}
			}

		} catch (ClassNotFoundException e) {
			throw new MySystemException(e);
		}
	}

	public static Result handler(Invocation invocation) {
		String mapping = (String) invocation.getAttachment(Invocation.MAPPING);
		IInvoker invoker = workers.get(mapping).getInvoker();
		if (invoker == null) {
			throw new MySystemException("mapping " + mapping + " is not found");
		}
		return invoker.invoke(invocation);
	}

	public static ConcurrentMap<String, BaseHandler> getWorkers() {
		return workers;
	}

}
