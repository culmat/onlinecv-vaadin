package com.beisert.onlinecv.vaadin;

import java.beans.PropertyDescriptor;
import java.util.Date;

import com.beisert.onlinecv.vaadin.generic.BeanPropertyEditor;
import com.beisert.onlinecv.vaadin.generic.GenericBeanFormConfig;
import com.beisert.onlinecv.vaadin.util.DateUtil;
import com.beisert.onlinecv.vaadin.util.ReflectionUtil;
import com.beisert.onlinecv.vaadin.xsd.SimpleDate;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;

public class SimpleDateEditor extends DateField implements BeanPropertyEditor{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Object bean;
	PropertyDescriptor property;
	String caption;
	
	public Object getBean() {
		return bean;
	}
	public PropertyDescriptor getProperty() {
		return property;
	}
	public String getCaption() {
		return caption;
	}
	
	/**
	 * Called after the setter have bean called
	 */
	public Component createComponent(GenericBeanFormConfig cfg, final Object bean, final PropertyDescriptor property, final String caption) {
		this.bean = bean;
		this.property = property;
		this.caption = caption;
		
		setCaption(caption);
		
		
		SimpleDate simpleDate = (SimpleDate)ReflectionUtil.getPropertyValue(bean, property);
		
		Date date = null;
		if(simpleDate !=null){
			date = DateUtil.createDate(simpleDate.getYear(),simpleDate.getMonth(),simpleDate.getDay());
		}
		setValue(date);
		
		ValueChangeListener listener = new ValueChangeListener() {
			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				Date val = (Date)event.getProperty().getValue();
				if(val == null){
					ReflectionUtil.setPropertyValue(bean, property, null);
				}else{
					SimpleDate simpleDate = new SimpleDate();
					simpleDate.setYear(DateUtil.getYear(val));
					simpleDate.setMonth(DateUtil.getMonth(val));
					simpleDate.setDay(DateUtil.getDayOfMonth(val));
					
					ReflectionUtil.setPropertyValue(bean, property, simpleDate);
				}
			}
			
		};
		addValueChangeListener(listener );
		
		return this;
	}

}
