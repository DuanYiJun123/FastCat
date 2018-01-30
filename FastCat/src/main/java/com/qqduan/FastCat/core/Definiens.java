package com.qqduan.FastCat.core;

import java.io.File;

import org.dom4j.Element;

import com.qqduan.FastCat.util.FileUtil;
import com.qqduan.FastCat.util.XmlUtil;

public class Definiens {
	public static Element ROOT_ELEMENT;

	public static String PORT;
	public static String SERVICE_PACKAGE;
	public static String FILTER_PACKAGE;
	public static String HTTP_CHANNEL_SIZE;
	public static String LOG4J_PATH;
	public static String WEB_PATH;
	public static String DB_PATH;
	public static String UPLOAD_PATH;
	public static String DB_CLEAR;

	public static String get(String key) {
		return ROOT_ELEMENT.elementText(key);
	}

	public static void init(String path) {
		ROOT_ELEMENT = XmlUtil.get(FileUtil.getAppRoot() + File.separator + path);
		WEB_PATH = get("web-path");
		UPLOAD_PATH = get("upload-path");
		PORT = get("port");
		SERVICE_PACKAGE = get("service-package");
		FILTER_PACKAGE = get("filter-package");
		HTTP_CHANNEL_SIZE = get("http-channel-size");
		LOG4J_PATH = get("log4j-path");
		DB_CLEAR = get("db-clear");
		DB_PATH=get("data-path");
	}

}
