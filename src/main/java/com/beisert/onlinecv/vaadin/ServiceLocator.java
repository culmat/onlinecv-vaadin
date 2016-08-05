package com.beisert.onlinecv.vaadin;

/**
 * Finds the needed services
 * @author dbe
 *
 */
public class ServiceLocator {

	private final static ServiceLocator instance = new ServiceLocator();
	
	public ServiceLocator getInstance(){
		return instance;
	}
	
	
}
