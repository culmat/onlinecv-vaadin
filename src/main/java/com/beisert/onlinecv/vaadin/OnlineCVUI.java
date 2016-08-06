package com.beisert.onlinecv.vaadin;

import java.util.List;

import javax.ws.rs.core.Response;

import com.beisert.onlinecv.vaadin.generic.GenericBeanForm;
import com.beisert.onlinecv.vaadin.generic.GenericBeanFormConfig;
import com.beisert.onlinecv.vaadin.generic.PlainIntegerConverter;
import com.beisert.onlinecv.vaadin.xsd.AddressData;
import com.beisert.onlinecv.vaadin.xsd.CommunicationData;
import com.beisert.onlinecv.vaadin.xsd.GenericContainer;
import com.beisert.onlinecv.vaadin.xsd.I18NText;
import com.beisert.onlinecv.vaadin.xsd.LanguageText;
import com.beisert.onlinecv.vaadin.xsd.OnlineCV;
import com.beisert.onlinecv.vaadin.xsd.PersonalData;
import com.beisert.onlinecv.vaadin.xsd.ProjectData;
import com.beisert.onlinecv.vaadin.xsd.SimpleDate;
import com.beisert.onlinecv.vaadin.xsd.UserSkill;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Title("Online CV")
@Theme("valo")
public class OnlineCVUI extends UI {

	private Table cvList = new Table();
	private TextField searchField = new TextField();
	private Button addNewCVButton = new Button("New", FontAwesome.FILE_O);
	// private Button removeCVButton = new Button("Remove this CV");
	private Button saveCVButton = new Button("Save", FontAwesome.SAVE);
	private Button cancelButton = new Button("Cancel", FontAwesome.TIMES);

	private VerticalLayout editAreaLayout = new VerticalLayout();
	private GenericBeanForm beanForm = new GenericBeanForm();

	private static final String NAME = "name";
	private static final String USER = "user";
	private static final String ID = "id";

	private OnlineCVRestClient restClient = new OnlineCVRestClient();

	private static final String[] fieldNamesList = new String[] { USER, NAME };
	private static final String[] fieldHeaderList = new String[] { "User", "Name" };

	BeanItemContainer<OnlineCV> cvListContainer = createDatasource(OnlineCV.class);
	private GenericBeanFormConfig cfg;

	protected void init(VaadinRequest request) {

		// Configure the edit screen
		this.cfg = new GenericBeanFormConfig();
		cfg.givePropertyHint(OnlineCV.class, "projects", ProjectData.class);
		cfg.setPropertyCaption(OnlineCV.class, "cvName", "CV Name");

		cfg.givePropertyHint(ProjectData.class, "additionalInfo", GenericContainer.class);
		cfg.givePropertyHint(ProjectData.class, "usedSkills", UserSkill.class);
		cfg.setShownPropertiesInList(ProjectData.class, "from", "to", "title", "customer", "key");

		cfg.setPropertyEditor(SimpleDate.class, SimpleDateEditor.class);
		cfg.setPropertyEditor(I18NText.class, I18NTextEditor.class);

		cfg.setTableColumnConverter(I18NText.class, I18NTableColumnConverter.class);
		cfg.setTableColumnConverter(SimpleDate.class, SimpleDateTableColumnConverter.class);
		cfg.setTableColumnConverter(int.class, PlainIntegerConverter.class);
		cfg.setFormFieldConverter(int.class, PlainIntegerConverter.class);
		cfg.setTableColumnConverter(Integer.class, PlainIntegerConverter.class);
		cfg.setFormFieldConverter(Integer.class, PlainIntegerConverter.class);

		cfg.givePropertyHint(OnlineCV.class, "userSkills", UserSkill.class);
		cfg.givePropertyHint(PersonalData.class, "communicationData", CommunicationData.class);
		cfg.givePropertyHint(GenericContainer.class, "children", GenericContainer.class);
		cfg.givePropertyHint(I18NText.class, "languageTexts", LanguageText.class);

		initLayout();

		initCVList();
		loadCVList();

		initEditor();
		initSearch();
		initButtons();
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
		// bottomLeftLayout.addComponent(addNewCVButton);

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
		HorizontalLayout buttonRow = new HorizontalLayout();
		// buttonRow.addComponent(removeCVButton);
		buttonRow.addComponent(addNewCVButton);
		buttonRow.addComponent(saveCVButton);
		buttonRow.addComponent(cancelButton);
		editAreaLayout.addComponent(buttonRow);
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
			String haystack = ("" + item.getItemProperty(USER).getValue() + item.getItemProperty(NAME).getValue())
					.toLowerCase();
			return haystack.contains(needle);
		}

		public boolean appliesToProperty(Object id) {
			return true;
		}
	}

	private void initButtons() {
		addNewCVButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		addNewCVButton.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {

				cvListContainer.removeAllContainerFilters();

				OnlineCV cv = new OnlineCV();

				PersonalData pd = new PersonalData();
				pd.setAddress(new AddressData());
				pd.setBirthday(new SimpleDate());

				cv.setPersonalData(pd);
				cvListContainer.addBean(cv);
				cvList.select(cv);
			}
		});

		// removeCVButton.addClickListener(new ClickListener() {
		// public void buttonClick(ClickEvent event) {
		// Notification.show("Delete not implemented yet","Not implemented
		// yet!", Type.WARNING_MESSAGE);
		// }
		// });
		this.saveCVButton.setIcon(FontAwesome.SAVE);
		saveCVButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.saveCVButton.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				OnlineCV cv = (OnlineCV) beanForm.getBean();
				if (cv != null) {
					Response r = restClient.saveCV(cv);

					Notification.show("Online CV save returned " + r, Type.TRAY_NOTIFICATION);
					loadCVList();
					Notification.show("List reloaded", Type.TRAY_NOTIFICATION);
				} else {
					Notification.show("No CV selected", Type.ERROR_MESSAGE);
				}
			}
		});
		this.cancelButton.setStyleName(ValoTheme.BUTTON_DANGER);
		this.cancelButton.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadCVList();
			}
		});
	}

	private void initCVList() {
		cvList.setContainerDataSource(cvListContainer);
		cvList.setVisibleColumns((Object[]) fieldNamesList);
		cvList.setColumnHeaders(fieldHeaderList);
		cvList.setSelectable(true);
		cvList.setImmediate(true);

		cvList.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object contactId = cvList.getValue();

				if (contactId != null) {
					Object bean = ((BeanItem<?>) cvList.getItem(contactId)).getBean();
					beanForm.init(new GenericBeanForm.InitParameter(null, bean, cfg));
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
		this.cvListContainer.removeAllItems();
		this.cvListContainer.addAll(list);
		// Select selected in editor in list
		if (beanForm != null) {
			OnlineCV selected = (OnlineCV) beanForm.getBean();
			if (selected != null) {
				final String id = selected.getId();
				if (id != null) {
					OnlineCV foundInList = list.stream().filter(cv -> id.equals(cv.getId())).findFirst().get();
					if (foundInList != null) {
						cvList.select(foundInList);
					}
				} else {
					editAreaLayout.setVisible(false);
				}
			}
		}
	}
}