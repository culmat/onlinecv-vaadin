package com.beisert.onlinecv.vaadin.generic;

import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import com.beisert.onlinecv.vaadin.util.ReflectionUtil;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

/**
 * Generates a dropdown for an enum property.
 * 
 * TODO. 
 * 
 * @author dbe
 *
 */
public class GenericBeanEnumComboBox extends ComboBox{
	
	private static final long serialVersionUID = 1L;
	
	private InitParameter initParam;
	
	public static class InitParameter {
		private String caption;
		private Object parentBean;
		private PropertyDescriptor property;
		private GenericBeanFormConfig cfg;

		public InitParameter(String caption, Object parentBean, PropertyDescriptor property,
				GenericBeanFormConfig cfg) {
			this.caption = caption;
			this.parentBean = parentBean;
			this.property = property;
			this.cfg = cfg;
		}

		public String getCaption() {
			return caption;
		}

		public Object getParentBean() {
			return parentBean;
		}

		public PropertyDescriptor getProperty() {
			return property;
		}

		public GenericBeanFormConfig getCfg() {
			return cfg;
		}
	}
	
	public GenericBeanEnumComboBox init(final InitParameter param){
		
		this.initParam = param;
		
		Class<?> enumClazz = param.getProperty().getPropertyType();
		Object[] enumConstants = enumClazz.getEnumConstants();
		
		setCaption(param.getCaption());
		setBuffered(false);
		
		
		for(Object enumVal : enumConstants){
			
			addItem(enumVal);
			String itemCaption = enumVal.toString();
			itemCaption = WordUtils.capitalizeFully(itemCaption, new char[]{'_'}).replaceAll("_", " ");
			setItemCaption(enumVal, itemCaption);
		}
		
		Object val = ReflectionUtil.getPropertyValue(param.getParentBean(), param.getProperty());
		select(val);
		
		
		ValueChangeListener listener = new ValueChangeListener() {
			
			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				//This is the enum
				Object value = event.getProperty().getValue();
				ReflectionUtil.setPropertyValue(param.getParentBean(), param.getProperty(), value);
				select(value);
			}
		};
		addValueChangeListener(listener);
		
		return this;
	}

	public InitParameter getInitParam() {
		return initParam;
	}
	
	

}
