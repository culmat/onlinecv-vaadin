package com.beisert.onlinecv.vaadin;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Because type information is erased from Collections at runtime we need to
 * give the forms a little hint. This can be place for additional customization
 * of the view.
 * 
 * @author dbe
 *
 */
public class GenericBeanFormConfig {

	public static class BeanConfig {

		public static class PropertyConfig {
			String name;
			Class<?> type;

			public PropertyConfig(String name, Class<?> type) {
				super();
				this.name = name;
				this.type = type;
			}

		}

		Class<?> ownerClass;

		Map<String, PropertyConfig> propertyMap = new LinkedHashMap<String, GenericBeanFormConfig.BeanConfig.PropertyConfig>();

		public BeanConfig(Class<?> ownerClass) {
			super();
			this.ownerClass = ownerClass;
		}

		public BeanConfig givePropertyHint(String property, Class<?> type) {
			propertyMap.put(property, new PropertyConfig(property, type));
			return this;
		}

		public Class<?> getPropertyClassHint(String name) {
			PropertyConfig p = propertyMap.get(name);
			if (p == null)
				return null;
			return p.type;
		}
	}

	Map<Class<?>, BeanConfig> configMap = new LinkedHashMap<Class<?>, BeanConfig>();

	public GenericBeanFormConfig givePropertyHint(Class<?> clazz, String property, Class<?> propertyType) {

		BeanConfig cfg = configMap.get(clazz);
		if (cfg == null) {
			cfg = new BeanConfig(clazz);
			configMap.put(clazz, cfg);
		}
		cfg.givePropertyHint(property, propertyType);
		return this;

	}

	public Class<?> getPropertyClassHint(Object bean, String name) {
		BeanConfig beanCfg = configMap.get(bean.getClass());
		if (beanCfg == null)
			return null;
		return beanCfg.getPropertyClassHint(name);

	}

}
