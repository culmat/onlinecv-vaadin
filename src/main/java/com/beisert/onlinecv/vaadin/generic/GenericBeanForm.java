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

import com.beisert.onlinecv.vaadin.generic.BeanPropertyEditor.CreateComponentParameter;
import com.beisert.onlinecv.vaadin.generic.GenericBeanTable.InitParameter;
import com.beisert.onlinecv.vaadin.util.ReflectionUtil;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
/**
 * Generates a form through reflection by looping through the properties of the provided bean.
 * 
 * The UI generation is customized by the {@link GenericBeanFormConfig}.
 * 
 * The prop order can be provided by the {@link GenericBeanFormConfig} or can be taken from
 * a @XmlType(propOrder={}) of the specified bean.
 * 
 * @author dbe
 *
 */
public class GenericBeanForm extends VerticalLayout {

	private InitParameter initParam;
	
	public static class InitParameter {
		private String caption;
		private Object bean;
		private GenericBeanFormConfig cfg;

		public InitParameter(String caption, Object bean, GenericBeanFormConfig cfg) {
			this.caption = caption;
			this.bean = bean;
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

		public GenericBeanFormConfig getCfg() {
			return cfg;
		}

		public void setCfg(GenericBeanFormConfig cfg) {
			this.cfg = cfg;
		}
	}
	public GenericBeanForm init(final InitParameter param) {
		this.components.clear();
		
		this.initParam = param;
		
		FormLayout form = new FormLayout();
		form.setMargin(true);
		setCaption(param.getCaption());
		
		TabSheet tabSheet = new TabSheet();
		tabSheet.addTab(form, "Details");

		Map<String, PropertyDescriptor> props = ReflectionUtil.getPropertyDescriptors(param.getBean().getClass(), "class");
		Collection<String> keys = getPropertiesToBeShown(param, props);

		for (String name : keys) {
			
			PropertyDescriptor desc = props.get(name);
			if(desc == null) {
				throw new IllegalStateException(param.getBean() + " Property " + name + " not found");
			}
			Class<?> type = desc.getPropertyType();
			
			BeanPropertyEditor editor = param.getCfg().getPropertyEditorOrNull(type);
			
			if(editor != null){
				createFromBeanEditor(param, form, name, desc, editor);
			}
			else if (isSimpleType(type)) {
				createTextField(param, form, name, type);
			} else if (type.isEnum()) {
				createDropDown(param, form, name, desc);
			} else if (type.isAssignableFrom(List.class)) {
				createTable(param, tabSheet, name, desc);
			} else if (type.isAnnotationPresent(XmlType.class)) {
				//This is sub struct
				createSubForm(param, tabSheet, name, desc);
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

	public void createFromBeanEditor(final InitParameter param, FormLayout form, String name, PropertyDescriptor desc,
			BeanPropertyEditor editor) {
		Component comp = editor.createComponent(new CreateComponentParameter(param.getCfg().generateCaptionForProperty(param.getBean(), name), param.getBean(), desc, param.getCfg()));
		form.addComponent(comp);
	}

	public void createSubForm(final InitParameter param, TabSheet tabSheet, String name, PropertyDescriptor desc) {
		Object child = ReflectionUtil.getPropertyValue(param.getBean(), desc);
		// Avoid endless loops
		if (child != param.getBean()){
			String subCaption = param.getCfg().generateCaptionForProperty(param.getBean(), name);
			tabSheet.addTab(new GenericBeanForm().init(new InitParameter(param.getCaption(), child, param.getCfg())), subCaption);
		}
	}

	public void createTextField(final InitParameter param, FormLayout form, String name, Class<?> type) {
		BeanItem<Object> item = new BeanItem<Object>(param.getBean(), new String[] { name });
		com.vaadin.ui.TextField text = new TextField(param.getCfg().generateCaptionForProperty(param.getBean(), name), item.getItemProperty(name));
		text.setNullRepresentation("");
		Class<? extends Converter<String, ?>> formFieldConverter = param.getCfg().getFormFieldConverter(type);
		if(formFieldConverter!=null){
			text.setConverter(ReflectionUtil.newInstance(formFieldConverter));
		}
		text.setBuffered(false);
		form.addComponent(text);
	}

	public void createDropDown(final InitParameter param, FormLayout form, String name, PropertyDescriptor desc) {
		GenericBeanEnumComboBox cbo = new GenericBeanEnumComboBox();
		cbo.init(new GenericBeanEnumComboBox.InitParameter(param.getCfg().generateCaptionForProperty(param.getBean(), name), param.getBean(), desc, param.getCfg()));
		form.addComponent(cbo);
	}

	public void createTable(final InitParameter param, TabSheet tabSheet, String name, PropertyDescriptor desc) {
		List list = (List) ReflectionUtil.getPropertyValue(param.getBean(), desc);
		
		Class<?> classHint = param.getCfg().getPropertyClassHint(param.getBean(),name);
		if(classHint != null) {
			String subCaption = param.getCfg().generateCaptionForProperty(param.getBean(), name);
			tabSheet.addTab(new GenericBeanTable().init(new GenericBeanTable.InitParameter(subCaption, name, list, classHint, param.getCfg())) , subCaption);
		}else{
			System.out.println(param.getBean() + "." + name + " is ignored because no classhint available");
		}
	}

	public Collection<String> getPropertiesToBeShown(final InitParameter param, Map<String, PropertyDescriptor> props) {
		Collection<String> keys = props.keySet();
		if (param.getBean().getClass().isAnnotationPresent(XmlType.class)) {
			XmlType xmlType = param.getBean().getClass().getAnnotation(XmlType.class);
			keys = Arrays.asList(xmlType.propOrder());
		}
		return keys;
	}

	public boolean isSimpleType(Class<?> type) {
		Class<?>[] simpleTypes = { String.class, Integer.class, Double.class, int.class, double.class };
		for (Class<?> t : simpleTypes) {
			if (type.isAssignableFrom(t))
				return true;
		}
		return false;
	}

	public Object getBean() {
		return initParam == null ? null : initParam.getBean();
	}

}
