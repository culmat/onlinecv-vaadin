package com.beisert.onlinecv.vaadin;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import com.beisert.onlinecv.vaadin.xsd.OnlineCV;

/**
 * Rest interface for the online cv.
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
	 System.out.println(target.getUri());
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
		OnlineCV cv = builder.accept(MediaType.APPLICATION_JSON).get(OnlineCV.class);
		return cv;
	}

	public Response saveCV(OnlineCV cv) {
		Client client = ClientBuilder.newClient();
		client.register(OnlineCV.class);
		WebTarget target = client.target(serverUrl).path(rootPath + "/onlinecv/save");

		Builder builder = target.request();

		Response resp = builder.accept(MediaType.APPLICATION_JSON).post(Entity.entity(cv,MediaType.APPLICATION_JSON));
		return resp;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

}
