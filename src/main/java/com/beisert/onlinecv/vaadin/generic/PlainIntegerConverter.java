package com.beisert.onlinecv.vaadin.generic;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.StringToIntegerConverter;

/**
 * Converts all integers in the UI that are formatted without a grouping e.g 1.976
 * but they will bill displayed as 1976.
 * @author dbe
 *
 */
public class PlainIntegerConverter extends StringToIntegerConverter{
	
	@Override
	protected NumberFormat getFormat(Locale locale) {
		NumberFormat format = super.getFormat(locale);
        format.setGroupingUsed(false);
        return format;
	}

}
