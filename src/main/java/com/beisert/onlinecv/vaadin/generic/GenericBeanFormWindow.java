package com.beisert.onlinecv.vaadin.generic;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Represents the popup window to edit a single Bean in a popup. It is a wrapper around
 * a {@link GenericBeanForm}.
 * 
 * @author dbe
 *
 */
public class GenericBeanFormWindow extends Window {

	private static final long serialVersionUID = 1L;

	public static class InitParameter  {
		private Component parent;

		private String caption;
		private Object bean;
		private GenericBeanFormConfig cfg;

		public InitParameter(Component parent, String caption, Object bean, GenericBeanFormConfig cfg) {
			this.parent = parent;
			this.caption = caption;
			this.bean = bean;
			this.cfg = cfg;
		}
		

		public Component getParent() {
			return parent;
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


		public void setParent(Component parent) {
			this.parent = parent;
		}
	}

	private Button cancelButton = new Button("Cancel");
	private Button okButton = new Button("OK");
	private InitParameter initParam;

	public GenericBeanFormWindow showWindow(InitParameter parameterObject) {

		this.initParam = parameterObject;
		
		String formCaption = parameterObject.bean.getClass().getSimpleName();
		formCaption = GenericBeanFormConfig.camelCaseToHumanReadable(formCaption);

		this.setCaption(parameterObject.getCaption() + " - " + formCaption);
		this.center();
		this.setModal(true);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeUndefined();

		GenericBeanForm form = new GenericBeanForm();
		
		
		GenericBeanForm.InitParameter formInit = new GenericBeanForm.InitParameter(null, parameterObject.bean, parameterObject.cfg);
		form.init(formInit );
		form.setMargin(true);
		form.setSizeUndefined();
		layout.addComponent(form);

		
		HorizontalLayout buttonRow = initButtonRow(parameterObject);

		layout.addComponent(buttonRow);

		this.setContent(layout);
		CloseListener closeListener = new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				closeWindow();
			}

		};
		this.addCloseListener(closeListener);

		openWindow();

		return this;

	}

	public void openWindow() {
		UI.getCurrent().addWindow(this);
		this.setVisible(true);
	}

	public void closeWindow() {
		this.initParam.getParent().markAsDirtyRecursive();
		UI.getCurrent().removeWindow(this);
	}

	public HorizontalLayout initButtonRow(InitParameter parameterObject) {
		HorizontalLayout buttonRow = new HorizontalLayout();
		okButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		
		Label spacer = new Label();

		buttonRow.addComponents(spacer,okButton,cancelButton);
		buttonRow.setExpandRatio(spacer,1);
		buttonRow.setStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		buttonRow.setWidth("100%");
		buttonRow.setSpacing(true);

		okButton.addClickListener(evt -> closeWindow());
		cancelButton.addClickListener(evt -> closeWindow());
		return buttonRow;
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public Button getOkButton() {
		return okButton;
	}

}
