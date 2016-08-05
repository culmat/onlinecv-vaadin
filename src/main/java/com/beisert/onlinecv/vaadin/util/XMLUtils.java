package com.beisert.onlinecv.vaadin.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import com.beisert.onlinecv.vaadin.xsd.OnlineCV;

/**
 * Can convert a java class that has JAXB annotations
 * to an xml string.
 * 
 * @author EVFQY
 *
 */
public class XMLUtils {
	
	public static String marshal(Object object) throws JAXBException {
		System.out.println(object.getClass());
		Class<? extends Object> linkedHashMapClass = LinkedHashMap.class;
		//BUGFIX: Add the LinkedHashMap class, because unknown objects from mongo will be of type LinkedHashMap
		JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass(), linkedHashMapClass);
		System.out.println(jaxbContext);
		Marshaller marshaller = jaxbContext.createMarshaller();
		Writer writer = new StringWriter();
		marshaller.marshal(object, writer);
		return writer.toString();
	}

	public static Object unmarshal(Class<?> clazz, String xml) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz.getClass());
		Unmarshaller marshaller = jaxbContext.createUnmarshaller();
		Reader reader = new StringReader(xml);
		Object result = marshaller.unmarshal(reader);
		return result;
	}
	
	public static String generateXsd(Class<?> clazz)throws JAXBException, IOException{
		System.out.println(clazz);
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		System.out.println(jaxbContext);
		
		final StringWriter writer = new StringWriter();
		
		SchemaOutputResolver outputResolver = new SchemaOutputResolver() {
			
			@Override
			   public Result createOutput(String namespaceUri, String suggestedFileName)
			         throws IOException {
			      
			      // create stream result
			      StreamResult result = new StreamResult(writer);
			      result.setSystemId(suggestedFileName);
			      // return result
			      return result;
			   }
		};
		jaxbContext.generateSchema(outputResolver );
		return writer.toString();
	}
	
	
}
