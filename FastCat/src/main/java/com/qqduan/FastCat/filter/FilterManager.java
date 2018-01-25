package com.qqduan.FastCat.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.qqduan.FastCat.core.Definiens;
import com.qqduan.FastCat.interfaces.IFilter;
import com.qqduan.FastCat.util.FileUtil;

public class FilterManager {
	private static final Logger LOGGER = Logger.getLogger(FilterManager.class);
	private static List<IFilter> filters;
	
	public FilterManager() {
	}
	
	public static void init(){
		List<BaseFilter> inners=new ArrayList<>();
		String path = FileUtil.getAppRoot() + File.separator + "src" + File.separator + "main" + File.separator + "java"
				+ File.separator + Definiens.FILTER_PACKAGE.replaceAll("\\.", "/");
		
		LOGGER.info("scaning the package "+path);
	}
	
	
}
