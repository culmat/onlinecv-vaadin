package com.beisert.onlinecv.vaadin.generic;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlType;

import com.beisert.onlinecv.vaadin.util.ReflectionUtil;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Generates a table of bean property that has the type of list.
 * 
 * @author dbe
 *
 */
public class GenericBeanTable extends VerticalLayout {

	private Table table = null;
	private BeanItemContainer<? super Object> container = null;
	private InitParameter initParam;
	private static int MAX_ROWS = 50;


	public static class InitParameter {
		private String caption;
		private String name;
		private List<? super Object> list;
		private Class<? extends Object> type;
		private GenericBeanFormConfig cfg;

		public InitParameter(String caption, String name, List<? super Object> list, Class<? extends Object> type,
				GenericBeanFormConfig cfg) {
			this.caption = caption;
			this.name = name;
			this.list = list;
			this.type = type;
			this.cfg = cfg;
		}

		public String getCaption() {
			return caption;
		}

		public String getName() {
			return name;
		}

		public List<? super Object> getList() {
			return list;
		}

		public Class<? extends Object> getType() {
			return type;
		}

		public GenericBeanFormConfig getCfg() {
			return cfg;
		}
	}

	public GenericBeanTable init(InitParameter param) {
		this.initParam = param;
		setCaption(param.getCaption());
		this.table = new Table();
		calcTablePageLength();

		initToolbar(param);
		addComponent(table);
		container = new BeanItemContainer(param.getType());

		Map<String, PropertyDescriptor> props = ReflectionUtil.getPropertyDescriptors(param.getType(), "class");
		Collection<String> keys = getPropertiesToBeShown(param, props);

		table.setContainerDataSource(container);
		table.setVisibleColumns(keys.toArray());

		findAndInstallConverters(param, props, keys);

		container.addAll(param.getList());

		table.setSelectable(true);
		table.setImmediate(true);

		setColumnHeaders(param, keys);
		addSelectRowOpenPopup(param);
		return this;

	}

	public Collection<String> getPropertiesToBeShown(InitParameter parameterObject,
			Map<String, PropertyDescriptor> props) {
		Collection<String> keys = parameterObject.getCfg().getShownPropertiesInList(parameterObject.getType());

		if (keys == null && parameterObject.getType().isAnnotationPresent(XmlType.class)) {
			XmlType xmlType = parameterObject.getType().getAnnotation(XmlType.class);
			keys = Arrays.asList(xmlType.propOrder());
		}
		if (keys == null)
			keys = props.keySet();
		return keys;
	}

	public void addSelectRowOpenPopup(InitParameter parameterObject) {
		table.addItemClickListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				Object id = table.getValue();
				BeanItem<?> beanItem = ((BeanItem<?>) table.getItem(id));
				if (beanItem != null) {

					final GenericBeanFormWindow popup = new GenericBeanFormWindow();
					popup.showWindow(new GenericBeanFormWindow.InitParameter(GenericBeanTable.this, "Details",
							beanItem.getBean(), parameterObject.getCfg()));
					popup.getOkButton().setVisible(true);
					popup.getCancelButton().setVisible(false);

					popup.getOkButton().addClickListener(evt -> {
						popup.closeWindow();
					});

				}
			}
		});
	}

	public void setColumnHeaders(InitParameter parameterObject, Collection<String> keys) {
		String[] headers = keys.stream().map(key -> {
			return parameterObject.getCfg().generateCaptionForProperty(parameterObject.getType(), key);
		}).toArray(size -> new String[size]);

		table.setColumnHeaders(headers);
	}

	public void findAndInstallConverters(InitParameter parameterObject, Map<String, PropertyDescriptor> props,
			Collection<String> keys) {
		keys.forEach(key -> {
			PropertyDescriptor d = props.get(key);
			if (d != null) {
				Class<? extends Converter<String, ?>> tableColumnConverter = parameterObject.getCfg()
						.getTableColumnConverter(d.getPropertyType());
				if (tableColumnConverter != null) {
					table.setConverter(key, ReflectionUtil.newInstance(tableColumnConverter));
				}
			}
		});
	}

	public void initToolbar(InitParameter parameterObject) {
		// Buttons
		HorizontalLayout buttonRow = new HorizontalLayout();
		Button add = new Button("New", FontAwesome.PLUS);
		add.setStyleName(ValoTheme.BUTTON_LINK);

		add.addClickListener(evt -> {
			
			final Object newBean = ReflectionUtil.newInstance(parameterObject.getType());
			final GenericBeanFormWindow popup = new GenericBeanFormWindow();
			popup.showWindow(new GenericBeanFormWindow.InitParameter(GenericBeanTable.this, "New", newBean,
					parameterObject.getCfg()));
			popup.getOkButton().setVisible(true);
			popup.getCancelButton().setVisible(true);
			
			popup.getOkButton().addClickListener(okEvt -> {
				parameterObject.getList().add(newBean);
				container.addBean(newBean);
				calcTablePageLength();
				markAsDirtyRecursive();
				popup.closeWindow();
			});
			

		});
		// REMOVE selected
		Button remove = new Button("Remove", FontAwesome.TRASH);
		remove.setStyleName(ValoTheme.BUTTON_LINK);
		ClickListener removeListener = new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Object row = table.getValue();
				parameterObject.getList().remove(row);
				container.removeItem(row);
				calcTablePageLength();
				markAsDirtyRecursive();
			}
		};
		remove.addClickListener(removeListener);

		buttonRow.addComponent(add);
		buttonRow.addComponent(remove);
		addComponent(buttonRow);
	}

	public void calcTablePageLength() {
		int count = this.initParam.getList().size();
		if (count < MAX_ROWS)
			this.table.setPageLength(count);
		else
			this.table.setPageLength(MAX_ROWS);
	}

	public InitParameter getInitParam() {
		return initParam;
	}

}
