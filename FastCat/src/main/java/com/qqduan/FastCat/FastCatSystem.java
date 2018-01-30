package com.qqduan.FastCat;

import org.apache.log4j.Logger;

import com.qqduan.FastCat.core.Definiens;
import com.qqduan.FastCat.core.HandlerManager;
import com.qqduan.FastCat.core.LoggerManager;
import com.qqduan.FastCat.filter.FilterManager;
import com.qqduan.FastCat.http.HttpService;

public class FastCatSystem {

	private static Logger LOGGER = Logger.getLogger(FastCatSystem.class);

	private static void start(String path) {
		Definiens.init(path);
		LoggerManager.init(Definiens.LOG4J_PATH);
		LOGGER.info("System init...");
		FilterManager.init();
		HandlerManager.init();
		HttpService.init();
	}

}
