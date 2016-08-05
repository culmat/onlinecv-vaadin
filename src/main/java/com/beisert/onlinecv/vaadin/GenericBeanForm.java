package com.beisert.onlinecv.vaadin;

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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

public class GenericBeanForm extends HorizontalLayout {

	private Object bean;
	private GenericBeanFormConfig cfg;

	public GenericBeanForm init(String title, Object bean, GenericBeanFormConfig cfg) {
		
		this.bean = bean;
		this.cfg = cfg;
		FormLayout form = new FormLayout();
		Panel p = new Panel(title);
		p.setContent(form);
		addComponent(p);

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
			Class<?> type = desc.getPropertyType();
			if (isSimpleType(type)) {
				BeanItem<Object> item = new BeanItem<Object>(bean, new String[] { name });
				com.vaadin.ui.TextField text = new TextField(name, item.getItemProperty(name));
				text.setNullRepresentation("");
				form.addComponent(text);
			} else if (type.isEnum()) {
//				BeanItem<Object> item = new BeanItem<Object>(bean, new String[] { name });
//				com.vaadin.ui.TextField text = new TextField(name, item.getItemProperty(name));
//				form.addComponent(text);
			} else if (type.isAssignableFrom(List.class)) {
				// table
				List list = (List) invokeGet(bean, desc);
				
				Class<?> classHint = cfg.getPropertyClassHint(bean,name);
				if(classHint != null) {
					form.addComponent(new GenericBeanTable().init(name, name, list, classHint, cfg) );
				}else{
					System.out.println(bean + "." + name + " is ignored because no classhint available");
				}

			} else if (type.isAnnotationPresent(XmlType.class)) {
				Object child = invokeGet(bean, desc);
				// Avoid endless loops
				if (child != bean)
					form.addComponent(new GenericBeanForm().init(name, child, cfg));

			}
		}
		return this;
	}

	private Object invokeGet(Object bean, PropertyDescriptor desc) throws IllegalStateException {
		try {
			Object child = desc.getReadMethod().invoke(bean, null);
			return child;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
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
