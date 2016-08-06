package com.beisert.onlinecv.vaadin.generic;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlType;

import com.beisert.onlinecv.vaadin.util.ReflectionUtil;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * Generates a table of bean property that has the type of list.
 * 
 * @author dbe
 *
 */
public class GenericBeanTable extends VerticalLayout {

	private Table table = null;
	private List<? super Object> list;
	BeanItemContainer<? super Object> container = null;
	private GenericBeanFormConfig cfg;

	private static int MAX_ROWS = 50;

	public GenericBeanTable init(final String caption, final String name, final List<? super Object> list,
			final Class<? extends Object> type, final GenericBeanFormConfig cfg) {
		this.list = list;
		this.cfg = cfg;
		setCaption(caption);
		this.table = new Table();
		calcTablePageLength();

		// Buttons
		HorizontalLayout buttonRow = new HorizontalLayout();
		Button add = new Button("New");
		ClickListener addListener = new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Object newBean = ReflectionUtil.newInstance(type);
				list.add(newBean);
				container.addBean(newBean);
				calcTablePageLength();
				openPopup(GenericBeanTable.this, "New", newBean, cfg);
			}
		};
		add.addClickListener(addListener);
		// REMOVE selected
		Button remove = new Button("Remove");
		ClickListener removeListener = new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Object row = table.getValue();
				list.remove(row);
				container.removeItem(row);
				calcTablePageLength();
				markAsDirtyRecursive();
			}
		};
		remove.addClickListener(removeListener);

		buttonRow.addComponent(add);
		buttonRow.addComponent(remove);
		addComponent(buttonRow);
		addComponent(table);

		container = new BeanItemContainer(type);

		//Get keys from config, xml annotation or reflection
		Map<String, PropertyDescriptor> props = ReflectionUtil.getPropertyDescriptors(type, "class");
		
		Collection<String>keys = cfg.getShownPropertiesInList(type);
		
		if (keys == null && type.isAnnotationPresent(XmlType.class)) {
			XmlType xmlType = type.getAnnotation(XmlType.class);
			keys = Arrays.asList(xmlType.propOrder());
		}
		if(keys == null)
			keys = props.keySet();
		//-- 
		
		
		table.setContainerDataSource(container);
		table.setVisibleColumns(keys.toArray());
		
		// set converters
		keys.forEach(key -> {
			PropertyDescriptor d = props.get(key);
			if (d != null) {
				Class<? extends Converter<String, ?>> tableColumnConverter = cfg
						.getTableColumnConverter(d.getPropertyType());
				if (tableColumnConverter != null) {
					table.setConverter(key, ReflectionUtil.newInstance(tableColumnConverter));
				}
			}
		});

		

		container.addAll(list);

		table.setSelectable(true);
		table.setImmediate(true);

		table.addItemClickListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				Object id = table.getValue();
				BeanItem<?> beanItem = ((BeanItem<?>) table.getItem(id));
				if (beanItem != null)
					openPopup(GenericBeanTable.this, "Details", beanItem.getBean(), cfg);
			}
		});
		return this;

	}

	public void calcTablePageLength() {
		int count = this.list.size();
		if (count < MAX_ROWS)
			this.table.setPageLength(count);
		else
			this.table.setPageLength(MAX_ROWS);
	}

	public static void openPopup(final Component parent, String caption, Object bean, GenericBeanFormConfig cfg) {
		final Window window = new Window();
		window.setCaption(caption);
		window.center();
		window.setHeight("100");
		window.setWidth("300");
		window.setModal(true);

		GenericBeanForm form = new GenericBeanForm();
		form.init(caption, bean, cfg);
		// form.setMargin(true);

		window.setContent(form);
		CloseListener closeListener = new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				parent.markAsDirtyRecursive();
				UI.getCurrent().removeWindow(window);
			}
		};
		window.addCloseListener(closeListener);

		UI.getCurrent().addWindow(window);

		window.setVisible(true);
	}

}
