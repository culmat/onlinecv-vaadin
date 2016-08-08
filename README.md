# onlinecv-vaadin
Vaadin UI for the online CV

The Online CV is a showcase to show the combination of different technologies like jersey rest, jaxb, mongo db, vaadin and jongo(http://jongo.org).

This project contains only the UI part that accesses the online CV rest services.
The rest services are implemented here : (https://github.com/beisdog/onlinecv.git). Please not the documentation there.

## Noteworthy Architecture Design Decisions of the UI project
### domain object generation from xsd file
The UI project generates the domain objects for the online cv from a schema file that was provided by the [rest services project](https://github.com/beisdog/onlinecv). The java generator from xsd is invoked from the maven pom.xml

You can get the xsd by calling the rest service ["/rest/onlinecv/xsd"](http://localhost:8888/rest/onlinecv/xsd).
## Dynamic UI
The UI is very generic and introspect the domain object and annotations to actually build the forms and tables.

Because some customization is needed you can pass options and generation features to the dynamic UI.
In this way you can add just new properties or objects in the domain layer and they will automatically and magically appear in the UI.

The implementation details can be found in the following places:

* OnlineCVUI (/src/main/java/com/beisert/onlinecv/vaadin/OnlineCVUI.java)
In the method `init(VaadinRequest)` you can find how the generic UI is customized and used.

Method: `init(VaadinRequest)`
```java
this.cfg = new GenericBeanFormConfig();
cfg.givePropertyHint(OnlineCV.class, "projects", Project.class);
cfg.givePropertyHint(OnlineCV.class, "userSkills", UserSkill.class);
cfg.givePropertyHint(OnlineCV.class, "jobs", Job.class);
cfg.givePropertyHint(OnlineCV.class, "education", Education.class);
cfg.givePropertyHint(OnlineCV.class, "certifications", Certification.class);
cfg.givePropertyHint(OnlineCV.class, "languageSkills", LanguageSkill.class);

cfg.givePropertyHint(PersonalData.class, "communicationData", CommunicationData.class);

cfg.givePropertyHint(Project.class, "additionalInfos", GenericContainer.class);
cfg.givePropertyHint(Project.class, "usedSkills", UserSkill.class);

cfg.givePropertyHint(GenericContainer.class, "children", GenericContainer.class);

cfg.givePropertyHint(I18NText.class, "languageTexts", LanguageText.class);

cfg.setPropertyCaption(OnlineCV.class, "cvName", "CV Name");
cfg.setShownPropertiesInList(Project.class, "from", "to", "title", "customer", "key");
cfg.setPropertyEditor(SimpleDate.class, SimpleDateEditor.class);
cfg.setPropertyEditor(I18NText.class, I18NTextEditor.class);

cfg.setTableColumnConverter(I18NText.class, I18NTableColumnConverter.class);
cfg.setTableColumnConverter(SimpleDate.class, SimpleDateTableColumnConverter.class);
cfg.setTableColumnConverter(int.class, PlainIntegerConverter.class);
cfg.setFormFieldConverter(int.class, PlainIntegerConverter.class);
cfg.setTableColumnConverter(Integer.class, PlainIntegerConverter.class);
cfg.setFormFieldConverter(Integer.class, PlainIntegerConverter.class);

```
In the method `initEditor()` the GenericBeanForm is initialized.


# Getting it to run
* Before you can run the UI you must install and run the Rest server part: 
 * Described here: http://github.com/beisdog/onlinecv and running it. (As described in the README.md) 
* Checkout the git repository on your local machine: 
 * git clone http://github.com/beisdog/onlinecv-vaadin.git
* Run maven:
	* $ cd onlinecv-vaadin
	* $ mvn clean install jetty:run
* Open your browser on: http://localhost:8080

# Debugging
To debug set these MAVEN_OPTS in your console:

$ export MAVEN_OPTS -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n

Then in your eclipse create a new Remote Debugging configuration and connect to port 8000

# Technologies involved
The following technologies are shown:
* Vaadin
* Java 8
* Jersey Rest client
* A self developed framework to quickly generate UIs
* Jaxb Java class  generation from schema file according to rest layer.