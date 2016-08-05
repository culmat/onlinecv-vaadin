package com.beisert.onlinecv.vaadin;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlType;

import com.beisert.onlinecv.vaadin.util.ReflectionUtil;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class GenericBeanTable extends HorizontalLayout {

	private Table table = null;
	private List<?> list;
	BeanItemContainer container = null;
	private GenericBeanFormConfig cfg;

	public GenericBeanTable init(String caption, String name, List<?> list, Class<?> type, GenericBeanFormConfig cfg) {
		this.list = list;
		this.cfg = cfg;

		table = new Table(caption);
		addComponent(table);
		container = new BeanItemContainer(type);

		Map<String, PropertyDescriptor> props = ReflectionUtil.getPropertyDescriptors(type, "class");
		Collection<String> keys = props.keySet();
		if (type.isAnnotationPresent(XmlType.class)) {
			XmlType xmlType = type.getAnnotation(XmlType.class);
			keys = Arrays.asList(xmlType.propOrder());
		}

		table.setContainerDataSource(container);

		container.addAll(list);

		table.setVisibleColumns(keys.toArray());

		table.setSelectable(true);
		table.setImmediate(true);

		table.addItemClickListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				Object id = table.getValue();
				BeanItem beanItem = ((BeanItem<?>) table.getItem(id));
				if (beanItem != null)
					openPopup("Details", beanItem.getBean());
			}
		});
		return this;

	}

	private void openPopup(String caption, Object bean) {
		final Window window = new Window();
		window.setCaption(caption);
		window.center();
		window.setHeight("100");
		window.setWidth("300");
		window.setModal(true);

		GenericBeanForm form = new GenericBeanForm();
		form.init(caption, bean, cfg);

		form.setMargin(true);

		window.setContent(form);
		CloseListener closeListener = new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				UI.getCurrent().removeWindow(window);
			}
		};
		window.addCloseListener(closeListener);

		UI.getCurrent().addWindow(window);

		window.setVisible(true);
	}

}
