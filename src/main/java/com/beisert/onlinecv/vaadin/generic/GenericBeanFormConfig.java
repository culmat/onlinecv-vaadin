package com.beisert.onlinecv.vaadin.generic;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.beisert.onlinecv.vaadin.generic.GenericBeanFormConfig.BeanConfig.PropertyConfig;
import com.vaadin.data.util.converter.Converter;

/**
 * This class serves hints and UI customization to the {@link GenericBeanForm} classes in this package.
 * 
 * @author dbe
 *
 */
public class GenericBeanFormConfig {

	public static class BeanConfig {

		public static class PropertyConfig {
			String name;
			Class<?> type;
			String caption;
			
			

			public PropertyConfig(String name){
				super();
				this.name = name;
			}
			public PropertyConfig(String name, Class<?> type) {
				super();
				this.name = name;
				this.type = type;
			}

		}

		Class<?> ownerClass;

		Map<String, PropertyConfig> propertyMap = new LinkedHashMap<String, GenericBeanFormConfig.BeanConfig.PropertyConfig>();
		
		List<String> shownPropertiesInList = null;
		

		public BeanConfig(Class<?> ownerClass) {
			super();
			this.ownerClass = ownerClass;
		}

		public BeanConfig givePropertyTypeHint(String property, Class<?> type) {
			PropertyConfig p = getOrCreatePropertyConfig(property);
			p.type = type;
			
			return this;
		}

		private PropertyConfig getOrCreatePropertyConfig(String property) {
			PropertyConfig p = propertyMap.get(property);
			if (p == null){
				p = new PropertyConfig(property);
				propertyMap.put(property, p);
			}
			return p;
		}

		public Class<?> getPropertyTypeHint(String name) {
			PropertyConfig p = propertyMap.get(name);
			if (p == null)
				return null;
			return p.type;
		}
	}

	Map<Class<?>, BeanConfig> configMap = new LinkedHashMap<Class<?>, BeanConfig>();
	Map<Class<?>,Class<? extends BeanPropertyEditor> >propertyEditorMap = new LinkedHashMap<Class<?>,Class<? extends BeanPropertyEditor>>();
	
	/**
	 * Can adapt a bean in a list, for easier display
	 */
	Map<Class<?>,Class<? extends Converter<String, ?>>> tableColumnConverter = new LinkedHashMap<Class<?>,Class<? extends Converter<String,?>>>();
	Map<Class<?>,Class<? extends Converter<String, ?>>>  formFieldConverter = new LinkedHashMap<Class<?>,Class<? extends Converter<String, ?>>> ();
	

	public GenericBeanFormConfig givePropertyHint(Class<?> clazz, String property, Class<?> propertyType) {

		BeanConfig cfg = getOrCreateBeanConfig(clazz);
		cfg.givePropertyTypeHint(property, propertyType);
		return this;
	}
	
	public GenericBeanFormConfig setPropertyCaption(Class<?> clazz, String property, String caption) {

		BeanConfig cfg = getOrCreateBeanConfig(clazz);
		cfg.getOrCreatePropertyConfig(property).caption = caption;
		return this;
	}
	
	
	private String getPropertyCaptionOrNull(Class<?> clazz, String property){
		BeanConfig cfg = configMap.get(clazz);
		if (cfg == null) {
			return null;
		}
		PropertyConfig propertyConfig = cfg.propertyMap.get(property);
		if(propertyConfig == null){
			return null;
		}
		return propertyConfig.caption;
	}
	
	public GenericBeanFormConfig setPropertyEditor(Class<?> clazz, Class<? extends BeanPropertyEditor> editorClazz){
		this.propertyEditorMap.put(clazz, editorClazz);
		return this;
	}
	
	public BeanPropertyEditor getPropertyEditorOrNull(Class<?> clazz){
		
		Class<? extends BeanPropertyEditor> editorClazz = propertyEditorMap.get(clazz);
		if(editorClazz == null){
			return null;
		}
		try {
			return editorClazz.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	

	private BeanConfig getOrCreateBeanConfig(Class<?> clazz) {
		BeanConfig cfg = configMap.get(clazz);
		if (cfg == null) {
			cfg = new BeanConfig(clazz);
			configMap.put(clazz, cfg);
		}
		return cfg;
	}

	public Class<?> getPropertyClassHint(Object bean, String name) {
		BeanConfig beanCfg = configMap.get(bean.getClass());
		if (beanCfg == null)
			return null;
		return beanCfg.getPropertyTypeHint(name);

	}
	
	/**
	 * Can be overriden to provide a custom strategy
	 * @param bean
	 * @param propertyName
	 * @return
	 */
	public String generateCaptionForProperty(Object bean, String propertyName){
		Class<?> clazz = bean.getClass();
		return generateCaptionForProperty(propertyName, clazz);
		
	}

	public String generateCaptionForProperty(String propertyName, Class<?> clazz) {
		String caption = getPropertyCaptionOrNull(clazz, propertyName);
		if(caption !=null){
			return caption;
		}
		
		return camelCaseToHumanReadable(propertyName);
	}

	public static String camelCaseToHumanReadable(String camelCase) {
		String[] arr = StringUtils.splitByCharacterTypeCamelCase(camelCase);
		
		if(arr.length>0){
			arr[0] = StringUtils.capitalize(arr[0]);
		}
		return StringUtils.join(arr," ");
	}

	public void setTableColumnConverter(Class<?> source, Class<? extends Converter<String,?>> converter) {
		this.tableColumnConverter.put(source, converter);
	}
	public Class<? extends Converter<String,?>> getTableColumnConverter(Class<?> source){
		return this.tableColumnConverter.get(source);
	}
	
	public void setFormFieldConverter(Class<?> source, Class<? extends Converter<String,?>> converter) {
		this.formFieldConverter.put(source, converter);
	}
	public Class<? extends Converter<String,?>> getFormFieldConverter(Class<?> source){
		return this.formFieldConverter.get(source);
	}
	public void setShownPropertiesInList(Class<?> clazz, String ...props){
		getOrCreateBeanConfig(clazz).shownPropertiesInList = Arrays.asList(props);
	}
	
	public List<String> getShownPropertiesInList(Class<?> clazz){
		return getOrCreateBeanConfig(clazz).shownPropertiesInList; 
	}
	
	
	
	

}
