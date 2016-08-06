package com.beisert.onlinecv.vaadin;

import java.util.Locale;

import com.beisert.onlinecv.vaadin.xsd.I18NText;
import com.vaadin.data.util.converter.Converter;

public class I18NTableColumnConverter implements Converter<String, I18NText>{

	@Override
	public I18NText convertToModel(String value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		//editing data in table is not supported
		return null;
	}

	@Override
	public String convertToPresentation(I18NText value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value == null? null : value.getDefaultText();
	}

	@Override
	public Class<I18NText> getModelType() {
		return I18NText.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
	
	
	

}
