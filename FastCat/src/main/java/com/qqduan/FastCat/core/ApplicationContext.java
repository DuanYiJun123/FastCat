package com.qqduan.FastCat.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
import com.qqduan.FastCat.interfaces.IFilter;
import com.qqduan.FastCat.interfaces.IService;

public class ApplicationContext {

	private static ApplicationContext instance = new ApplicationContext();
	private static final Logger LOGGER = Logger.getLogger(ApplicationContext.class);
	public static final Map<Class<?>, Object> BEANS = new ConcurrentHashMap<>();
	private static final Map<Class<?>, Element> ELEMENTS = new ConcurrentHashMap<>();

	private ApplicationContext() {
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

	public static ApplicationContext instance() {
		return instance;
	}

	public <T extends IFilter> T getFilter(Class<T> clz) {
		LOGGER.debug("get bean of " + clz.getName());
		if (BEANS.containsKey(clz)) {
			return (T) BEANS.get(clz);
		}
		LOGGER.debug("bean of " + clz.getName() + " is not found ,prepare to create");

		try {
			T newInstance = newInstance(clz);

			HashMap<String, Element> properties = getProperties(clz);

			Field[] fields = clz.getDeclaredFields();

			for (Field field : fields) {
				if (field.getAnnotation(AutoCycleLife.class) != null) {
					if (properties == null) {
						throw new ContextXmlError(clz.getName() + " is not found in context xml");
					}
					field.setAccessible(true);
					Element propertyEle = properties.get(field.getName());
					setField(field, newInstance, propertyEle);
				}
			}

			LOGGER.debug("bean of " + clz.getName() + " created");

			BEANS.put(clz, newInstance);
			return newInstance;
		} catch (SecurityException | IllegalArgumentException e) {
			throw new MySystemException(e);
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
				if (field.isAnnotationPresent(AutoCycleLife.class)) {
					if (properties == null) {
						throw new ContextXmlError(clz.getName() + " is not found in context xml");
					}
					field.setAccessible(true);
					Element propertyEly = properties.get(field.getName());
					setField(field, newInstance, propertyEly);
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> T getBean(Class<T> clz) {
		LOGGER.debug("get bean of " + clz.getName());
		if (BEANS.containsKey(clz)) {
			return (T) BEANS.get(clz);
		}
		LOGGER.debug("bean of " + clz.getName() + " is not found ,prepare to create");

		try {
			T newInstance = newInstance(clz);

			HashMap<String, Element> properties = getProperties(clz);

			Field[] fields = clz.getDeclaredFields();

			for (Field field : fields) {
				if (field.getAnnotation(AutoCycleLife.class) != null) {
					if (properties == null) {
						throw new ContextXmlError(clz.getName() + " is not found in context xml");
					}
					field.setAccessible(true);
					Element propertyEle = properties.get(field.getName());
					setField(field, newInstance, propertyEle);
				}
			}

			LOGGER.debug("bean of " + clz.getName() + " created");

			BEANS.put(clz, newInstance);
			return newInstance;
		} catch (SecurityException | IllegalArgumentException e) {
			throw new MySystemException(e);
		}
	}

	private <T> void setField(Field field, T newInstance, Element propertyEle) {
		if (propertyEle.attribute("value") != null) {
			setValue(field, newInstance, propertyEle);
		} else if (propertyEle.attribute("ref") != null) {
			setRef(field, newInstance, propertyEle);
		}
	}

	private <T> void setRef(Field field, T newInstance, Element propertyEle) {
		String className = propertyEle.attributeValue("ref");
		try {
			Class<?> clz = (Class<?>) Class.forName(className);
			field.set(newInstance, getBean(clz));
		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
			throw new ContextXmlError(className + " is not found");
		}
	}

	private <T> T newInstance(Class<T> clz) {
		try {
			Constructor<T> constructor = clz.getConstructor();
			constructor.setAccessible(true);
			T newInstance = constructor.newInstance();
			return newInstance;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new MySystemException(e);
		}
	}

	private <T> void setValue(Field field, T newInstance, Element propertyEle) {
		String value = propertyEle.attributeValue("value");
		try {
			Class<?> type = field.getType();
			if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
				field.set(newInstance, Integer.valueOf(value));
			} else if (type.isAssignableFrom(Character.class) || type.isAssignableFrom(char.class)) {
				if (value.length() != 1) {
					throw new ContextXmlError(field.getName() + " is not a character");
				}
				field.set(newInstance, value.charAt(0));
			} else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class)) {
				field.set(newInstance, Boolean.valueOf(value));
			} else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
				field.set(newInstance, Long.valueOf(value));
			} else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(short.class)) {
				field.set(newInstance, Short.valueOf(value));
			} else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class)) {
				field.set(newInstance, Float.valueOf(value));
			} else if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class)) {
				field.set(newInstance, Double.valueOf(value));
			} else if (type.isAssignableFrom(String.class)) {
				field.set(newInstance, value);
			} else {
				throw new ContextXmlError("type of " + field.getName() + " is not support");
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
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
