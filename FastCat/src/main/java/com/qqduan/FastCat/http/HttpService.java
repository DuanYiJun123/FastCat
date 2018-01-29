package com.qqduan.FastCat.http;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.qqduan.FastCat.core.Definiens;
import com.qqduan.FastCat.core.HandlerManager;
import com.qqduan.FastCat.core.Invocation;
import com.qqduan.FastCat.core.Result;
import com.qqduan.FastCat.handler.BaseHandler;
import com.qqduan.FastCat.handler.HttpMethod;
import com.qqduan.FastCat.util.FileUtil;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class HttpService extends AbstractVerticle {
	private static final Logger LOGGER = Logger.getLogger(HttpService.class);
	private static Vertx gVertx = null;
	private static String webPath = Definiens.WEB_PATH;
	private static String uploadPath = Definiens.UPLOAD_PATH;

	public static void init() {
		VertxOptions vp = new VertxOptions();
		vp.setEventLoopPoolSize(64);
		vp.setMaxEventLoopExecuteTime(20000000);
		vp.setBlockedThreadCheckInterval(20000000);
		gVertx = Vertx.vertx(vp);
		DeploymentOptions p = new DeploymentOptions();
		p.setInstances(Integer.valueOf(Definiens.HTTP_CHANNEL_SIZE));
		gVertx.deployVerticle(HttpService.class.getName(), p);

		new File(webPath).mkdirs();
		new File(uploadPath).mkdirs();

		LOGGER.info("http channel size = " + Definiens.HTTP_CHANNEL_SIZE);
		LOGGER.info("system upload root = " + uploadPath);
		LOGGER.info("system web root = " + FileUtil.getAppRoot() + File.separator + webPath);
	}

	@Override
	public void start() {
		Router router = Router.router(gVertx);

		router.route().handler(BodyHandler.create().setUploadsDirectory(uploadPath));

		ConcurrentMap<String, BaseHandler> workers = HandlerManager.getWorkers();
		workers.forEach((mapping, handler) -> {
			HttpMethod[] methods = handler.getMethods();
			if (methods != null && methods.length > 0) {
				List<HttpMethod> asList = Arrays.asList(methods);
				if (asList.contains(HttpMethod.GET)) {
					get(router, mapping);
				}
				if (asList.contains(HttpMethod.POST)) {
					post(router, mapping);
				}
				if (asList.contains(HttpMethod.FILE)) {
					file(router, mapping);
				}
			}
		});

		router.route().handler(StaticHandler.create().setWebRoot(webPath).setCachingEnabled(false));

		HttpServerOptions options = new HttpServerOptions();
		options.setReuseAddress(true);
		HttpServer server = gVertx.createHttpServer(options);
		server.requestHandler(router::accept).listen(Integer.valueOf(Definiens.PORT));
	}

	private void file(Router router, String mapping) {
		// TODO Auto-generated method stub
		
	}

	private void post(Router router, String mapping) {
		router.post(mapping).handler(context -> {
			Map<String, String> param = new HashMap<>();
			MultiMap httpParam = context.request().params();
			List<Map.Entry<String, String>> list = httpParam.entries();
			for (Map.Entry<String, String> e : list) {
				String key = e.getKey();
				String value = e.getValue();
				if (value == null || value.length() == 0) {
					continue;
				}
				param.put(key, value);
			}

			Invocation invocation = new Invocation();
			invocation.setAttachment(Invocation.MAPPING, mapping);
			invocation.setAttachment(Invocation.REQUEST, param);
			Result result = HandlerManager.handler(invocation);
			context.response().end(result.getAttachment(Result.RESPONSE).toString());
		});
	}

	private void get(Router router, String mapping) {
		// TODO Auto-generated method stub
		
	}
}
