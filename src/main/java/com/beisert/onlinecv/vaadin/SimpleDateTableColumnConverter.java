package com.beisert.onlinecv.vaadin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.beisert.onlinecv.vaadin.util.DateUtil;
import com.beisert.onlinecv.vaadin.xsd.I18NText;
import com.beisert.onlinecv.vaadin.xsd.SimpleDate;
import com.vaadin.data.util.converter.Converter;

/**
 * A converter that converts {@link SimpleDate} to readable string. This is used in the table columns.
 * @author dbe
 *
 */
public class SimpleDateTableColumnConverter implements Converter<String, SimpleDate>{
	
	private static final long serialVersionUID = 1L;

	@Override
	public SimpleDate convertToModel(String value, Class<? extends SimpleDate> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		// editing data not needed
		return null;
	}

	@Override
	public String convertToPresentation(SimpleDate value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		DateFormat format = new SimpleDateFormat("yyyy-MM");
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
