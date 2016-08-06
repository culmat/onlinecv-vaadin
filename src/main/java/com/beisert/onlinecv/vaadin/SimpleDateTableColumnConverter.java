package com.beisert.onlinecv.vaadin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.beisert.onlinecv.vaadin.util.DateUtil;
import com.beisert.onlinecv.vaadin.xsd.I18NText;
import com.beisert.onlinecv.vaadin.xsd.SimpleDate;
import com.vaadin.data.util.converter.Converter;

public class SimpleDateTableColumnConverter implements Converter<String, SimpleDate>{

	@Override
	public SimpleDate convertToModel(String value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		//editing data in table is not supported
		return null;
	}
	

	@Override
	public String convertToPresentation(SimpleDate value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if(value == null) return "";
		Date d = DateUtil.createDate(value.getYear(), value.getMonth(), value.getDay());
		return format.format(d);
	}

	@Override
	public Class<SimpleDate> getModelType() {
		return SimpleDate.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
	
	
	

}
