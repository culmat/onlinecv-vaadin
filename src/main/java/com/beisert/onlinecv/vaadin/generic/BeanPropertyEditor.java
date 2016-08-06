package com.beisert.onlinecv.vaadin.generic;

import java.beans.PropertyDescriptor;
import com.vaadin.ui.Component;

/**
 * Custom component creator for a class.
 * 
 * @author dbe
 *
 */
public interface BeanPropertyEditor {
	public static class CreateComponentParameter {
		private String caption;
		private Object bean;
		private PropertyDescriptor property;
		private GenericBeanFormConfig cfg;

		public CreateComponentParameter(String caption, Object bean, PropertyDescriptor property,
				GenericBeanFormConfig cfg) {
			this.caption = caption;
			this.bean = bean;
			this.property = property;
			this.cfg = cfg;
		}

		public String getCaption() {
			return caption;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		public Object getBean() {
			return bean;
		}

		public void setBean(Object bean) {
			this.bean = bean;
		}

		public PropertyDescriptor getProperty() {
			return property;
		}

		public void setProperty(PropertyDescriptor property) {
			this.property = property;
		}

		public GenericBeanFormConfig getCfg() {
			return cfg;
		}

		public void setCfg(GenericBeanFormConfig cfg) {
			this.cfg = cfg;
		}
	}

	Object getBean();

	PropertyDescriptor getProperty();

	String getCaption();

	Component createComponent(CreateComponentParameter parameterObject);

}
