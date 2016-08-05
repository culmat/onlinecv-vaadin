package com.beisert.onlinecv.vaadin;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import com.beisert.onlinecv.vaadin.xsd.OnlineCV;

/**
 * Rest interface for the online cv
 * @author dbe
 *
 */
public class OnlineCVRestClient {

	String serverUrl = "http://localhost:8888";
	String rootPath = "/rest";

	public OnlineCVRestClient() {
	}

	public OnlineCVRestClient(String serverUrl, String rootPath) {
		super();
		this.serverUrl = serverUrl;
		this.rootPath = rootPath;
	}

	public static void main(String[] args) throws Exception {
		OnlineCVRestClient cl = new OnlineCVRestClient();

		OnlineCV cv = cl.findCVByUser("dbe");
		
		System.out.println(cv);
		
		List<OnlineCV> cvs = cl.getAllCVs();
		System.out.println(cvs);
		
	}

	public List<OnlineCV> getAllCVs() {
	 Client client = ClientBuilder.newClient();
	 client.register(OnlineCV.class);
	 WebTarget target = client.target(serverUrl).path(rootPath +"/onlinecv");
	 Builder builder = target.request();
	 List<OnlineCV> result =
	 builder.accept(MediaType.APPLICATION_JSON).get(new GenericType<List<OnlineCV>>(){});
	 return result;
	
	 }

	public OnlineCV findCVByUser(String user) throws JAXBException {

		Client client = ClientBuilder.newClient();
		client.register(OnlineCV.class);
		WebTarget target = client.target(serverUrl).path(rootPath + "/onlinecv/dbe");

		Builder builder = target.request();
		// Response response = builder.get();
		// String result =
		// builder.accept(MediaType.APPLICATION_XML).get(String.class);
		// System.out.println(result);
		// OnlineCV cv = (OnlineCV) XMLUtils.unmarshal(OnlineCV.class, result);

		OnlineCV cv = builder.accept(MediaType.APPLICATION_JSON).get(OnlineCV.class);
		return cv;
	}

}
