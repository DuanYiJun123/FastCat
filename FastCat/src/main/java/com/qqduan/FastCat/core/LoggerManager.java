package com.qqduan.FastCat.core;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.qqduan.FastCat.exception.MySystemException;

public class LoggerManager {
	public static void init(String path){
		try (FileInputStream istream = new FileInputStream(path)) {
			Properties props = new Properties();
			props.load(istream);
			PropertyConfigurator.configure(props);
		} catch (Exception e) {
			throw new MySystemException(e);
		}
	
		
	}
}
