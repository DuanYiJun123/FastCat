package com.qqduan.FastCat.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Element;

import com.qqduan.FastCat.annotation.AutoCycleLife;
import com.qqduan.FastCat.exception.ContextXmlError;
import com.qqduan.FastCat.exception.MySystemException;
import com.qqduan.FastCat.interfaces.IService;

public class ApplicationContext {

	private static ApplicationContext instance = new ApplicationContext();
	private static final Logger LOGGER = Logger.getLogger(ApplicationContext.class);
	public static final Map<Class<?>, Object> BEANS = new ConcurrentHashMap<>();
	private static final Map<Class<?>, Element> ELEMENTS = new ConcurrentHashMap<>();

	private ApplicationContext() {
	}

	public static ApplicationContext newInstance() {
		return instance;
	}

	static {
		Element beansEle = Definiens.ROOT_ELEMENT.element("beans");
		if (beansEle != null) {
			@SuppressWarnings("unchecked")
			List<Element> beanList = (List<Element>) beansEle.element("bean");
			if (beanList != null && !beanList.isEmpty()) {
				beanList.forEach(p -> {
					String className = p.attributeValue("class");
					if (className == null) {
						throw new ContextXmlError("attribute of class is not found in bean");
					}

					try {
						Class<?> clz = Class.forName(className);
						ELEMENTS.put(clz, p);
					} catch (Exception e) {
						throw new ContextXmlError("class of " + className + " is not found");
					}
				});
			}
		}
	}

	public <T extends IService> T getService(Class<T> clz) {
		LOGGER.debug("getting " + clz.getName() + "service from object pool");
		if (BEANS.containsKey(clz)) {
			return (T) BEANS.get(clz);
		}
		LOGGER.debug("object pool not found " + clz.getName());
		try {
			T newInstance = clz.newInstance();
			HashMap<String, Element> properties = getProperties(clz);
			Field[] fields = clz.getDeclaredFields();
			for (Field field : fields) {
				if(field.isAnnotationPresent(AutoCycleLife.class)){
					if(properties==null){
						throw new ContextXmlError(clz.getName() + " is not found in context xml");
					}
					field.setAccessible(true);
					Element propertyEly = properties.get(field.getName());
					setField(field,newInstance,propertyEly);
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private <T> void setField(Field field, T newInstance, Element propertyEly) {
		
	}

	private <T> HashMap<String, Element> getProperties(Class<T> clz) {
		Element element = ELEMENTS.get(clz);
		if (element == null) {
			return null;
		}
		List<Element> propertiesList = element.elements("property");
		HashMap<String, Element> propertyMap = new HashMap<>();
		for (Element property : propertiesList) {
			Attribute key = property.attribute("key");
			if (key == null) {
				throw new MySystemException("key is not found in property , class = " + clz.getName());
			}
			propertyMap.put(key.getData().toString(), property);
		}
		return propertyMap;
	}
}
