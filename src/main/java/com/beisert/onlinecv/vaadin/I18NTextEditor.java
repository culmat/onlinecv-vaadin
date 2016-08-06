package com.beisert.onlinecv.vaadin;

import java.beans.PropertyDescriptor;

import com.beisert.onlinecv.vaadin.generic.BeanPropertyEditor;
import com.beisert.onlinecv.vaadin.generic.GenericBeanFormConfig;
import com.beisert.onlinecv.vaadin.generic.GenericBeanTable;
import com.beisert.onlinecv.vaadin.util.ReflectionUtil;
import com.beisert.onlinecv.vaadin.xsd.I18NText;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class I18NTextEditor implements BeanPropertyEditor{
	
	Object bean;
	PropertyDescriptor property;
	String caption;
	
	I18NTextWrapper valueWrapper;
	
	public Object getBean() {
		return bean;
	}
	public PropertyDescriptor getProperty() {
		return property;
	}
	public String getCaption() {
		return caption;
	}
	
	public class I18NTextWrapper {
		I18NText txt;
		
		public I18NTextWrapper(I18NText txt) {
			super();
			this.txt = txt;
		}
		public String getDefaultText(){
			return txt == null? null: txt.getDefaultText();
		}
		public void setDefaultText(String t){
			if(txt == null)
				throw new IllegalStateException("I18NText is null therefore cannot set");
			txt.setDefaultText(t);
		}
	}
	
	@Override
	public Component createComponent(final GenericBeanFormConfig cfg, final Object bean, final PropertyDescriptor property, final String caption) {
		
		final HorizontalLayout layout = new HorizontalLayout();
		layout.setCaption(caption);
		
		I18NText txt = (I18NText) ReflectionUtil.getPropertyValue(bean,property);
		//Make sure an instance exists.
		if(txt == null) {
			txt = new I18NText();
			ReflectionUtil.setPropertyValue(bean,property,txt);
		}
		
		this.valueWrapper = new I18NTextWrapper(txt);
		
		BeanItem<Object> item = new BeanItem<Object>(valueWrapper, new String[] { "defaultText" });
		com.vaadin.ui.TextField textField = new TextField();
		textField.setPropertyDataSource(item.getItemProperty("defaultText"));
		textField.setNullRepresentation("");
		textField.setBuffered(false);
		layout.addComponent(textField);
		
		Button openPopup = new Button("...");
		ClickListener listener = new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				GenericBeanTable.openPopup(layout, "Edit", valueWrapper.txt, cfg);
			}
		};
		openPopup.addClickListener(listener );
		layout.addComponent(openPopup);
		
		return layout;
		
		
	}

}
