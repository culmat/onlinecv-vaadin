package com.beisert.onlinecv.vaadin;

import java.util.List;

import com.beisert.onlinecv.vaadin.generic.GenericBeanForm;
import com.beisert.onlinecv.vaadin.generic.GenericBeanFormConfig;
import com.beisert.onlinecv.vaadin.xsd.CommunicationData;
import com.beisert.onlinecv.vaadin.xsd.GenericContainer;
import com.beisert.onlinecv.vaadin.xsd.I18NText;
import com.beisert.onlinecv.vaadin.xsd.LanguageText;
import com.beisert.onlinecv.vaadin.xsd.OnlineCV;
import com.beisert.onlinecv.vaadin.xsd.PersonalData;
import com.beisert.onlinecv.vaadin.xsd.ProjectData;
import com.beisert.onlinecv.vaadin.xsd.SimpleDate;
import com.beisert.onlinecv.vaadin.xsd.UserSkill;
import com.vaadin.annotations.Title;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Title("Online CV")
public class OnlineCVUI extends UI {

	private Table cvList = new Table();
	private TextField searchField = new TextField();
	private Button addNewCVButton = new Button("New");
	private Button removeCVButton = new Button("Remove this CV");
	
	private VerticalLayout editAreaLayout = new VerticalLayout();
	private GenericBeanForm beanForm = new GenericBeanForm();

	private static final String CV_NAME = "cvName";
	private static final String USER = "user";
	private static final String ID = "id";
	

	private OnlineCVRestClient restClient = new OnlineCVRestClient();

	private static final String[] fieldNamesList = new String[] { ID, USER, CV_NAME };

	BeanItemContainer<OnlineCV> cvListContainer = createDatasource(OnlineCV.class);
	private GenericBeanFormConfig cfg;

	protected void init(VaadinRequest request) {
		
		// Configure the edit screen
		this.cfg = new GenericBeanFormConfig();
		cfg.givePropertyHint(OnlineCV.class, "projects", ProjectData.class);
		cfg.setPropertyCaption(OnlineCV.class, "cvName", "CV Name");
		
		cfg.givePropertyHint(ProjectData.class, "additionalInfo", GenericContainer.class);
		cfg.givePropertyHint(ProjectData.class, "usedSkills", UserSkill.class);
		cfg.setShownPropertiesInList(ProjectData.class, "from","to","title","customer","key");
		
		cfg.setPropertyEditor(SimpleDate.class, SimpleDateEditor.class);
		cfg.setPropertyEditor(I18NText.class, I18NTextEditor.class);
		
		cfg.setTableColumnConverter(I18NText.class,I18NTableColumnConverter.class);
		
		cfg.setTableColumnConverter(SimpleDate.class,SimpleDateTableColumnConverter.class);
		
		
		cfg.givePropertyHint(OnlineCV.class, "userSkills", UserSkill.class);
		cfg.givePropertyHint(PersonalData.class, "communicationData", CommunicationData.class);
		cfg.givePropertyHint(GenericContainer.class, "children", GenericContainer.class);
		cfg.givePropertyHint(I18NText.class, "languageTexts", LanguageText.class);
		
		initLayout();
		
		initCVList();
		loadCVList();
		
		initEditor();
		initSearch();
		initAddRemoveButtons();
	}

	private void initLayout() {

		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		setContent(splitPanel);

		VerticalLayout leftLayout = new VerticalLayout();
		splitPanel.addComponent(leftLayout);
		
		splitPanel.addComponent(editAreaLayout);
		leftLayout.addComponent(cvList);
		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		leftLayout.addComponent(bottomLeftLayout);
		bottomLeftLayout.addComponent(searchField);
		bottomLeftLayout.addComponent(addNewCVButton);

		leftLayout.setSizeFull();

		leftLayout.setExpandRatio(cvList, 1);
		cvList.setSizeFull();

		bottomLeftLayout.setWidth("100%");
		searchField.setWidth("100%");
		bottomLeftLayout.setExpandRatio(searchField, 1);

		editAreaLayout.setMargin(true);
		editAreaLayout.setVisible(false);
	}

	private void initEditor() {
		editAreaLayout.addComponent(removeCVButton);
		editAreaLayout.addComponent(beanForm);

	}

	private void initSearch() {
		searchField.setInputPrompt("Search CVs");

		searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);

		searchField.addTextChangeListener(new TextChangeListener() {
			public void textChange(final TextChangeEvent event) {

				loadCVList();
				cvListContainer.removeAllContainerFilters();
				cvListContainer.addContainerFilter(new CVFilter(event.getText()));

			}
		});

	}

	private class CVFilter implements Filter {
		private String needle;

		public CVFilter(String needle) {
			this.needle = needle.toLowerCase();
		}

		public boolean passesFilter(Object itemId, Item item) {
			String haystack = ("" + item.getItemProperty(ID).getValue() + item.getItemProperty(USER).getValue()
					+ item.getItemProperty(USER).getValue()).toLowerCase();
			return haystack.contains(needle);
		}

		public boolean appliesToProperty(Object id) {
			return true;
		}
	}

	private void initAddRemoveButtons() {
		addNewCVButton.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {

				cvListContainer.removeAllContainerFilters();
				Object contactId = cvListContainer.addItemAt(0);
				//TODO
//				cvList.getContainerProperty(contactId, FNAME).setValue("New");
//				cvList.getContainerProperty(contactId, LNAME).setValue("Contact");
//
//				cvList.select(contactId);
			}
		});

		removeCVButton.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				Object contactId = cvList.getValue();
				cvList.removeItem(contactId);
			}
		});
	}

	private void initCVList() {
		cvList.setContainerDataSource(cvListContainer);
		cvList.setVisibleColumns(fieldNamesList);
		cvList.setSelectable(true);
		cvList.setImmediate(true);
		
		cvList.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object contactId = cvList.getValue();

				if (contactId != null){
					Object bean = ((BeanItem<?>)cvList.getItem(contactId)).getBean();
					beanForm.init("Online CV",bean,cfg);
				}
				editAreaLayout.setVisible(contactId != null);
			}
		});
	}

	private static <T> BeanItemContainer<T> createDatasource(Class<T> beanClass) {
		BeanItemContainer<T> ic = new BeanItemContainer<T>(beanClass);
		return ic;
	}

	private void loadCVList() {
		List<OnlineCV> list = restClient.getAllCVs();
		this.cvListContainer.addAll(list);
	}
}