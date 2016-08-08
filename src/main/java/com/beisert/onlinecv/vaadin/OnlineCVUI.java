package com.beisert.onlinecv.vaadin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Response;

import com.beisert.onlinecv.vaadin.generic.GenericBeanForm;
import com.beisert.onlinecv.vaadin.generic.GenericBeanFormConfig;
import com.beisert.onlinecv.vaadin.generic.PlainIntegerConverter;
import com.beisert.onlinecv.vaadin.xsd.AddressData;
import com.beisert.onlinecv.vaadin.xsd.Certification;
import com.beisert.onlinecv.vaadin.xsd.CommunicationData;
import com.beisert.onlinecv.vaadin.xsd.Education;
import com.beisert.onlinecv.vaadin.xsd.GenericContainer;
import com.beisert.onlinecv.vaadin.xsd.I18NText;
import com.beisert.onlinecv.vaadin.xsd.Job;
import com.beisert.onlinecv.vaadin.xsd.LanguageSkill;
import com.beisert.onlinecv.vaadin.xsd.LanguageText;
import com.beisert.onlinecv.vaadin.xsd.OnlineCV;
import com.beisert.onlinecv.vaadin.xsd.PersonalData;
import com.beisert.onlinecv.vaadin.xsd.Project;
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
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Entry point of the application. This is the main UI.
 * 
 * @author dbe
 *
 */
@Title("Online CV")
@Theme("valo")
public class OnlineCVUI extends UI {

	private Table cvList = new Table();
	private TextField searchField = new TextField();
	private Button addNewCVButton = new Button("New", FontAwesome.FILE_O);
	// private Button removeCVButton = new Button("Remove this CV");
	private Button saveCVButton = new Button("Save", FontAwesome.SAVE);
	private Button cancelButton = new Button("Reload");

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
		cfg.givePropertyHint(OnlineCV.class, "projects", Project.class);
		cfg.givePropertyHint(OnlineCV.class, "userSkills", UserSkill.class);
		cfg.givePropertyHint(OnlineCV.class, "jobs", Job.class);
		cfg.givePropertyHint(OnlineCV.class, "education", Education.class);
		cfg.givePropertyHint(OnlineCV.class, "certifications", Certification.class);
		cfg.givePropertyHint(OnlineCV.class, "languageSkills", LanguageSkill.class);
		cfg.givePropertyHint(PersonalData.class, "additionalInfos", GenericContainer.class);
		cfg.givePropertyHint(PersonalData.class, "communicationData", CommunicationData.class);
		cfg.givePropertyHint(Project.class, "additionalInfos", GenericContainer.class);
		cfg.givePropertyHint(Project.class, "usedSkills", UserSkill.class);
		cfg.givePropertyHint(GenericContainer.class, "children", GenericContainer.class);
		cfg.givePropertyHint(I18NText.class, "languageTexts", LanguageText.class);
		cfg.setPropertyCaption(OnlineCV.class, "name", "CV Name");
		cfg.setShownPropertiesInList(GenericContainer.class,"title","value");
		cfg.setShownPropertiesInList(Project.class, "from", "to", "title", "customer", "key");
		cfg.setPropertyEditor(SimpleDate.class, SimpleDateEditor.class);
		cfg.setPropertyEditor(I18NText.class, I18NTextEditor.class);
		cfg.setTableColumnConverter(I18NText.class, I18NTableColumnConverter.class);
		cfg.setTableColumnConverter(SimpleDate.class, SimpleDateTableColumnConverter.class);
		cfg.setTableColumnConverter(int.class, PlainIntegerConverter.class);
		cfg.setFormFieldConverter(int.class, PlainIntegerConverter.class);
		cfg.setTableColumnConverter(Integer.class, PlainIntegerConverter.class);
		cfg.setFormFieldConverter(Integer.class, PlainIntegerConverter.class);


		initLayout();

		initCVList();
		loadCVList();

		initEditor();
		initSearch();
		initButtons();
	}

	private void initLayout() {

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setHeight("100%");
		mainLayout.setSizeFull();
		
		// Header
		HorizontalLayout header = initHeader();
		mainLayout.addComponent(header);
		
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		mainLayout.addComponent(splitPanel);
		mainLayout.setExpandRatio(splitPanel, 1);
		
		splitPanel.setSplitPosition(25,Sizeable.Unit.PERCENTAGE);
		splitPanel.setSizeFull();
		splitPanel.setHeight("100%");
		
		setContent(mainLayout);

		VerticalLayout leftLayout = new VerticalLayout();
		splitPanel.addComponent(leftLayout);

		splitPanel.addComponent(editAreaLayout);
		leftLayout.addComponent(cvList);
		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		leftLayout.addComponent(bottomLeftLayout);
		bottomLeftLayout.addComponent(searchField);
		// bottomLeftLayout.addComponent(addNewCVButton);

		leftLayout.setSizeFull();
		leftLayout.setHeight("100%");

		leftLayout.setExpandRatio(cvList, 1);
		cvList.setSizeFull();

		bottomLeftLayout.setWidth("100%");
		searchField.setWidth("100%");
		bottomLeftLayout.setExpandRatio(searchField, 1);

		editAreaLayout.setMargin(true);
		editAreaLayout.setVisible(false);
	}

	public HorizontalLayout initHeader() {
		HorizontalLayout header = new HorizontalLayout();
		
		header.setSpacing(true);
		
		String[] fields = { "serverUrl", "rootPath" };
		
		BeanItem<OnlineCVRestClient> item = new BeanItem<OnlineCVRestClient>(this.restClient,fields);
		
		for(String fieldName: fields){
			Label l = new Label(GenericBeanFormConfig.camelCaseToHumanReadable(fieldName));
			l.setStyleName("v-align-middle", true);
			header.addComponent(l);
			TextField tf = new TextField( item.getItemProperty(fieldName));
			tf.setNullRepresentation("");
			tf.setBuffered(false);
			tf.setStyleName(ValoTheme.TEXTFIELD_SMALL);
			header.addComponent(tf);
		}
		
		Button buttonLoad = new Button("Load CVs",FontAwesome.REFRESH);
		buttonLoad.setStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonLoad.setStyleName(ValoTheme.BUTTON_SMALL, true);
		
		
		buttonLoad.addClickListener(evt -> {
			loadCVList();
		});
		header.addComponent(buttonLoad);
		
		
		final String urlMain = restClient.serverUrl + restClient.rootPath + "/onlinecv";
		
		final String[][] urls = {
				{"Open REST: All CVs",urlMain +"?format=json"},
				{"Open REST: CV xsd",urlMain +"/xsd"},
				{"Restinterface Startpage",restClient.serverUrl},
				
		};
		for(int i=0;i<urls.length;i++){
			String caption = urls[i][0];
			final String url = urls[i][1];
			final String window = "window"+i;
			Button button = new Button(caption,FontAwesome.EXTERNAL_LINK);
			button.setStyleName(ValoTheme.BUTTON_LINK);
			
			button.addClickListener(evt -> {
				getUI().getPage().open(url,window);
			});
			header.addComponent(button);
		}
		return header;
	}

	private void initEditor() {
		HorizontalLayout buttonRow = new HorizontalLayout();
		buttonRow.setSpacing(true);
		buttonRow.setWidth("100%");
		buttonRow.setStyleName(ValoTheme.WINDOW_TOP_TOOLBAR);
		// buttonRow.addComponent(removeCVButton);
		Label spacer = new Label();
		buttonRow.addComponents(addNewCVButton,saveCVButton,spacer, cancelButton);
		editAreaLayout.addComponent(buttonRow);
		buttonRow.setExpandRatio(spacer, 1);
		editAreaLayout.addComponent(beanForm);

	}

	private void initSearch() {
		searchField.setInputPrompt("Search CVs");

		searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);

		searchField.addTextChangeListener(evt -> {
			loadCVList();
			cvListContainer.removeAllContainerFilters();
			cvListContainer.addContainerFilter(new CVFilter(evt.getText()));

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
		addNewCVButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
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
		saveCVButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		this.saveCVButton.addClickListener( event-> {
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
		);
		this.cancelButton.setStyleName(ValoTheme.BUTTON_DANGER);
		this.cancelButton.addClickListener( event -> {
				loadCVList();
			}
		);
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
		List<OnlineCV> list = new ArrayList<>();
		try {
			list = restClient.getAllCVs();
			
		} catch (Exception e) {
			Notification.show(e.toString() , Type.ERROR_MESSAGE);
			e.printStackTrace();
		}
		if(list == null){
			Notification.show("Load data failed! returned list is null"  , Type.ERROR_MESSAGE);
			return;
		}
		this.cvListContainer.removeAllItems();
		this.cvListContainer.addAll(list);
		// Select selected in editor in list
		if (beanForm != null) {
			OnlineCV selected = (OnlineCV) beanForm.getBean();
			if (selected != null) {
				final String id = selected.getId();
				if (id != null) {
					Optional<OnlineCV> foundInList = list.stream().filter(cv -> id.equals(cv.getId())).findFirst();
					if (foundInList.isPresent()) {
						cvList.select(foundInList.get());
					}
				} else {
					editAreaLayout.setVisible(false);
				}
			}
		}
	}
}