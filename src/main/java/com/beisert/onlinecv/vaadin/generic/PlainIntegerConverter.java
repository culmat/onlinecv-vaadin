package com.beisert.onlinecv.vaadin.generic;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.StringToIntegerConverter;

public class PlainIntegerConverter extends StringToIntegerConverter{
	
	@Override
	protected NumberFormat getFormat(Locale locale) {
		NumberFormat format = super.getFormat(locale);
        format.setGroupingUsed(false);
        return format;
	}

}
