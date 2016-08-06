package com.beisert.onlinecv.vaadin.generic;

import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Map;

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
	
	
	private PropertyDescriptor property;
	private Object parentBean; 
	
	public GenericBeanEnumComboBox init(final String caption, final Object parentBean, final PropertyDescriptor property, final GenericBeanFormConfig cfg){
		
		this.parentBean = parentBean;
		this.property = property;
		
		Class<?> enumClazz = property.getPropertyType();
		Object[] enumConstants = enumClazz.getEnumConstants();
		
		setCaption(caption);
		setBuffered(false);
		
		
		for(Object enumVal : enumConstants){
			
			addItem(enumVal);
			setItemCaption(enumVal, enumVal.toString());
		}
		
		Object val = ReflectionUtil.getPropertyValue(parentBean, property);
		select(val);
		
		
		ValueChangeListener listener = new ValueChangeListener() {
			
			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				//This is the enum
				Object value = event.getProperty().getValue();
				ReflectionUtil.setPropertyValue(parentBean, property, value);
				select(value);
			}
		};
		addValueChangeListener(listener);
		
		return this;
	}
	
	

}
