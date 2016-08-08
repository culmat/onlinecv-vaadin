# onlinecv-vaadin
Vaadin UI for the online CV

The Online CV database is provided with rest services.
The special thing about this UI is that:

* The UI calls the Online CV rest webservice to show and edit the online CVs.
* The object model is generated from a schema file that is provided by the Online CV rest service.
* The UI uses a framework for generating dynamic UIs from POJOs. 
	* This UI layout can be customized easily

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