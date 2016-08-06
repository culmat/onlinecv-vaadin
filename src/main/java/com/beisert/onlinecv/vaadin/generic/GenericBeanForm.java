package com.beisert.onlinecv.vaadin.generic;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlType;

import com.beisert.onlinecv.vaadin.util.ReflectionUtil;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class GenericBeanForm extends VerticalLayout {

	private Object bean;
	private GenericBeanFormConfig cfg;
	
	
	public GenericBeanForm init(String caption, Object bean, GenericBeanFormConfig cfg) {
		
		this.bean = bean;
		this.cfg = cfg;
		FormLayout form = new FormLayout();
		form.setMargin(true);
		setCaption(caption);
		
		TabSheet tabSheet = new TabSheet();
		tabSheet.addTab(form, "Details");

		Map<String, PropertyDescriptor> props = ReflectionUtil.getPropertyDescriptors(bean.getClass(), "class");
		Collection<String> keys = props.keySet();
		if (bean.getClass().isAnnotationPresent(XmlType.class)) {
			XmlType xmlType = bean.getClass().getAnnotation(XmlType.class);
			keys = Arrays.asList(xmlType.propOrder());
		}

		for (String name : keys) {
			if (name == null || name.trim().length() == 0) {
				System.out.println(bean.getClass() + "!!!! Property is empty!");
				continue;
			}
			PropertyDescriptor desc = props.get(name);
			if(desc == null) {
				throw new IllegalStateException(bean + " Property " + name + " not found");
			}
			Class<?> type = desc.getPropertyType();
			
			BeanPropertyEditor editor = cfg.getPropertyEditorOrNull(type);
			
			if(editor != null){
				Component comp = editor.createComponent(cfg, bean, desc, cfg.generateCaptionForProperty(bean, name));
				form.addComponent(comp);
			}
			else if (isSimpleType(type)) {
				BeanItem<Object> item = new BeanItem<Object>(bean, new String[] { name });
				com.vaadin.ui.TextField text = new TextField(cfg.generateCaptionForProperty(bean, name), item.getItemProperty(name));
				text.setNullRepresentation("");
				text.setBuffered(false);
				form.addComponent(text);
			} else if (type.isEnum()) {
				GenericBeanEnumComboBox cbo = new GenericBeanEnumComboBox();
				cbo.init(cfg.generateCaptionForProperty(bean, name), bean, desc, cfg);
				form.addComponent(cbo);
			} else if (type.isAssignableFrom(List.class)) {
				// table
				List list = (List) ReflectionUtil.getPropertyValue(bean, desc);
				
				Class<?> classHint = cfg.getPropertyClassHint(bean,name);
				if(classHint != null) {
					String subCaption = cfg.generateCaptionForProperty(bean, name);
					tabSheet.addTab(new GenericBeanTable().init(subCaption, name, list, classHint, cfg) , subCaption);
				}else{
					System.out.println(bean + "." + name + " is ignored because no classhint available");
				}

			} else if (type.isAnnotationPresent(XmlType.class)) {
				//This is sub struct
				Object child = ReflectionUtil.getPropertyValue(bean, desc);
				// Avoid endless loops
				if (child != bean){
					String subCaption = cfg.generateCaptionForProperty(bean, name);
					tabSheet.addTab(new GenericBeanForm().init(caption, child, cfg), subCaption);
				}
			}
		}
		if(tabSheet.getComponentCount()==1){
			//Only one compnent we do not need a tabsheet
			addComponent(form);
		}else{
			addComponent(tabSheet);
		}
		return this;
	}

	public boolean isSimpleType(Class<?> type) {
		Class<?>[] simpleTypes = { String.class, Integer.class, Double.class, int.class, double.class };
		for (Class<?> t : simpleTypes) {
			if (type.isAssignableFrom(t))
				return true;
		}
		return false;
	}
}
