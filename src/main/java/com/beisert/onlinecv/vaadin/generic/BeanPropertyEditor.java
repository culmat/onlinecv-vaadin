package com.beisert.onlinecv.vaadin.generic;

import java.beans.PropertyDescriptor;
import com.vaadin.ui.Component;

/**
 * Custom component creator for a class.
 * @author dbe
 *
 */
public interface BeanPropertyEditor {

	Object getBean();

	PropertyDescriptor getProperty();

	String getCaption();

	Component createComponent(GenericBeanFormConfig cfg, Object bean, PropertyDescriptor property, String caption);

}
