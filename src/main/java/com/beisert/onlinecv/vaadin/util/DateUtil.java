package com.beisert.onlinecv.vaadin.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility for dates.
 * 
 * @author DBE
 * 
 */
public abstract class DateUtil {

	public static final int MINUTES_PER_HOUR = 60;

	public static final int MINUTES_PER_DAY = MINUTES_PER_HOUR * 24;
	
	public static final int SECONDS_PER_DAY = MINUTES_PER_DAY * 60;
	
	public static final int MILLISECONDS_PER_DAY = SECONDS_PER_DAY * 1000;

	private static final String DATE_FORMAT = "dd.MM.yyyy";
	
	public static int getCurrentYear() {
		Date date = new Date();
		return getYear(date);
	}
	
	public static int getYearOfPreviosMonth() {
		Date date = addMonths(new Date(), -1);
		return getYear(date);
	}

	public static int getYear(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}
	
	public static int getCurrentMonth() {
		Date date = new Date();
		return getMonth(date);
	}
	
	public static int getMonthOfPreviosMonth() {
		Date date = addMonths(new Date(), -1);
		return getMonth(date);
	}

	public static int getMonth(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) + 1;
	}
	
	public static int getDayOfMonth(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static Date getToday() {
		return getDateBegin(new Date());
	}
	
	private static Date getDateBegin(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getYesterday() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(getToday());
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		return calendar.getTime();
	}
	
	public static Date getTomorrow() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(getToday());
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		return calendar.getTime();
	}

	public static boolean isInPast(Date date) {
		return (new Date().compareTo(date) > 0);
	}

	public static java.sql.Timestamp now() {
		return new Timestamp(new Date().getTime());
	}

	public static java.sql.Date convert(Date date) {
		if (date == null)
			return null;
		return new java.sql.Date(date.getTime());
	}

	public static Date addHoursToNow(int hours) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		return calendar.getTime();
	}

	public static Date addDaysToNow(int days) {
		return addDays(new Date(), days);
	}
	
	public static Date subtractDaysFromNow(int days) {
		return addDays(new Date(), -days);
	}
	
	public static Date subtractDaysFromToday(int days) {
		return addDays(getToday(), -days);
	}

	public static Date addDays(Date date, int days) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar.getTime();
	}
	
	public static Date subtractMonths(Date date, int months) {
		return addMonths(date, -months);
	}
	
	public static Date addMonths(Date date, int months) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}
	
	public static java.sql.Date addMonths(java.sql.Date date, int months) {
		return convert(addMonths((java.util.Date) date, months));
	}
	
	public static Date subtractYears(Date date, int years) {
		return addYears(date, -years);
	}
	
	public static Date addYears(Date date, int years) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, years);
		return calendar.getTime();
	}
	
	public static java.sql.Date addYears(java.sql.Date date, int years) {
		return convert(addYears((java.util.Date) date, years));
	}
	
	public static Date substract(Date date, int field, int value) {
		if(date == null) {
			return null;
		}
		Date calculatedDate = null;
		switch (field) {
		case Calendar.YEAR:
			calculatedDate = getFirstDateInYear(subtractYears(date, value));
			break;
		case Calendar.MONTH:
			calculatedDate = getFirstDateInMonth(subtractMonths(date, value));
			break;
		case Calendar.DAY_OF_MONTH: 
			calculatedDate = substractDays(date, value);
			break;
		default:
			break;
		}
		return getDateBegin(calculatedDate);
	}

	public static Date addMilliseconds(Date date, int milliseconds) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MILLISECOND, milliseconds);
		return calendar.getTime();
	}
	
	
	public static Date addMinutesToNow(int minutes) {
		return addMinutes(now(), minutes);
	}
	
	public static Date addMinutes(Date date, int minutes) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

	public static String formatDate(Date date) {
		return formatDate(date, DATE_FORMAT);
	}

	public static String formatDate(Date date, String format) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * Parse the date with the format {@value #DATE_FORMAT}=
	 * {@value #DATE_FORMAT}.
	 * 
	 * @param dateString
	 *            Date as string
	 * @return parsed date.
	 */
	public static Date parseDate(String dateString) {
		return parseDate(dateString, DATE_FORMAT);
	}

	/**
	 * Parse the date with the format {@value #DATE_FORMAT}=
	 * {@value #DATE_FORMAT}.
	 * 
	 * @param dateString
	 *            Date as string
	 * @return parsed date.
	 */
	public static java.sql.Date parseSqlDate(String dateString) {
		return convert(parseDate(dateString));
	}

	public static Date parseDate(String string, String format) {
		if (StringUtils.isEmpty(string)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(string);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Date parseXmlDate(String string) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			return sdf.parse(string);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static java.sql.Date parseSqlDate(String string, String format) {
		return convert(parseDate(string, format));
	}

	public static java.sql.Date parseSqlXmlDate(String string) {
		return convert(parseXmlDate(string));
	}

	/**
	 * Substract the number of days from the given days. The returned date is in
	 * the past.
	 * 
	 * @param now
	 * @param days
	 * @return The returned date is in the past
	 */
	public static Date substractDays(Date date, int days) {
		return addDays(date, -days);
	}

	public static int getDiffInHours(Date dateFrom, Date dateTo) {
		return (int) ((dateTo.getTime() - dateFrom.getTime()) / (1000 * 60 * 60));
	}
	
	public static int getDiffInDays(Date dateFrom, Date dateTo) {
		return (int)((dateTo.getTime() - dateFrom.getTime()) / (1000 * 60 * 60 * 24))+1;
	}
	
	public static Date getFirstDateInMonth(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}
	
	public static Date getLastDateInMonth(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		int maximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, maximum);
		return calendar.getTime();
	}
	
	public static Date createDate(int year, int month, int day) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar.getTime();
	}
	
	public static java.sql.Date createSqlDate(int year, int month, int day) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return convert(calendar.getTime());
	}
	
	public static Date getFirstDateInPreviousYear() {
		return getFirstDateInYear(getCurrentYear()-1);
	}
	
	public static Date getFirstDateInCurrentYear() {
		return getFirstDateInYear(new Date());
	}
	
	public static Date getFirstDateInYear(int year) {
		return getFirstDateInYear(createDate(year, 1,1));
	}
	
	public static Date getFirstDateInYear(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}
	
	public static Date getLastDateInCurrentYear() {
		return getLastDateInYear(new Date());
	}
	
	public static Date getLastDateInPreviousYear() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 12);
		return calendar.getTime();
	}
	
	public static Date getLastDateInYear(int year) {
		return getLastDateInYear(createDate(year, 12, 31));
	}
	
	public static Date getLastDateInYear(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 12);
		return calendar.getTime();
	}
	
	public static long getCalendarDays(Date date) {
		return date.getTime() / MILLISECONDS_PER_DAY;
	} 
	
	public static Date getForeverDate() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.YEAR, 2099);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 12);
		return calendar.getTime();
	}
	
}
