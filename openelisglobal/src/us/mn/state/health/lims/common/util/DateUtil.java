/**
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is OpenELIS code.
 *
 * Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;

public class DateUtil {

	private static final int EPIC = 1970;
	private static final String AMBIGUOUS_DATE_REGEX;
	public static final String AMBIGUOUS_DATE_CHAR;
	public static final String AMBIGUOUS_DATE_SEGMENT;
	private static final Pattern FOUR_DIGITS = Pattern.compile("\\d{4}");
	private static final long DAY_IN_MILLSEC = 1000L * 60L * 60L * 24L;

	private static final long WEEK_MS = DAY_IN_MILLSEC * 7;

	public static final String FORMAT_DD_MM_YYYY = "dd/MM/yyyy";

	static {
		AMBIGUOUS_DATE_CHAR = ConfigurationProperties.getInstance()
				.getPropertyValue(Property.AmbiguousDateHolder);
		AMBIGUOUS_DATE_REGEX = "(?i)" + AMBIGUOUS_DATE_CHAR
				+ AMBIGUOUS_DATE_CHAR;
		AMBIGUOUS_DATE_SEGMENT = AMBIGUOUS_DATE_CHAR + AMBIGUOUS_DATE_CHAR;
	}

	public static String formatDateTimeAsText(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(getDateTimeFormat());
		return format.format(date);
	}

	public static String formatDateAsText(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(getDateFormat());
		return format.format(date);

	}

	public static java.sql.Date convertStringDateToSqlDate(String date) {
		String stringLocale = SystemConfiguration.getInstance()
				.getDefaultLocale().toString();

		return convertStringDateToSqlDate(date, stringLocale);
	}

	public static java.sql.Date convertStringDateToSqlDate(String date,
			String stringLocale) throws LIMSRuntimeException {
		SimpleDateFormat format = new SimpleDateFormat(getDateFormat());
		format.setLenient(false);
		java.sql.Date returnDate = null;

		if (!StringUtil.isNullorNill(date)) {
			try {
				returnDate = new java.sql.Date(format.parse(date).getTime());
			} catch (ParseException e) {
				LogEvent.logError("DateUtil", "convertStringDateToSqlDate()",
						e.toString());
				throw new LIMSRuntimeException("Error parsing date", e);
			}
		}
		return returnDate;

	}
	
	public static java.sql.Date convertStringDateToSqlDateAIM(String date) 
									throws LIMSRuntimeException {
		SimpleDateFormat format = new SimpleDateFormat(getDateFormatPg(), getDateFormatLocale());
		format.setLenient(false);
		java.sql.Date returnDate = null;

		if (!StringUtil.isNullorNill(date)) {
			try {
				returnDate = new java.sql.Date(format.parse(date).getTime());
			} catch (ParseException e) {
				LogEvent.logError("DateUtil", "convertStringDateToSqlDateAIM()",
						e.toString());
				throw new LIMSRuntimeException("Error parsing date", e);
			}
		}
		return returnDate;

	}

	public static java.sql.Date convertStringDateTimeToSqlDate(String date)
			throws LIMSRuntimeException {
		Locale locale = getDateFormatLocale();
		SimpleDateFormat format = new SimpleDateFormat(getDateFormat(), locale);
		java.sql.Date returnDate = null;
		if (!StringUtil.isNullorNill(date)) {
			try {
				returnDate = new java.sql.Date(format.parse(date).getTime());
			} catch (ParseException e) {
				LogEvent.logError("DateUtil",
						"convertStringDateTimeToSqlDate()", e.toString());
				throw new LIMSRuntimeException("Error parsing date", e);
			}
		}
		return returnDate;

	}

	public static Timestamp convertStringDateToTruncatedTimestamp(String date)
			throws LIMSRuntimeException {
		SimpleDateFormat format = new SimpleDateFormat(getDateFormat());
		Timestamp returnTimestamp = null;

		if (!StringUtil.isNullorNill(date)) {
			try {
				returnTimestamp = new Timestamp(format.parse(date).getTime());
			} catch (ParseException e) {
				LogEvent.logError("DateUtil",
						"convertStringDateToTruncatedTimestamp()", e.toString());
				throw new LIMSRuntimeException("Error parsing date", e);
			}
		}
		return returnTimestamp;

	}

	public static Timestamp convertStringDateToTimestamp(String date)
			throws LIMSRuntimeException {
		Locale locale = getDateFormatLocale();
		String pattern = getDateTimeFormat();
		SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
		Timestamp returnTimestamp = null;

		if (!StringUtil.isNullorNill(date)) {
			try {
				returnTimestamp = new Timestamp(format.parse(date).getTime());
			} catch (ParseException e) {
				LogEvent.logError("DateUtil", "convertStringDateToTimestamp()",
						e.toString());
				throw new LIMSRuntimeException("Error parsing date", e);
			}
		}
		return returnTimestamp;

	}

	public static Timestamp convertStringDateToTimestampWithPatternNoLocale(
			String date, String pattern) throws LIMSRuntimeException {
		SimpleDateFormat format = new SimpleDateFormat(pattern);

		Timestamp returnTimestamp = null;
		if (!StringUtil.isNullorNill(date)) {
			try {
				returnTimestamp = new Timestamp(format.parse(date).getTime());
			} catch (ParseException e) {
				LogEvent.logError("DateUtil",
						"convertStringDateToTimestampWithPattern()\nPattern: "
								+ pattern + "\nDate: " + date, e.toString());
				throw new LIMSRuntimeException("Error parsing date", e);
			}
		}
		return returnTimestamp;

	}

	public static Timestamp convertStringDateToTimestampWithPattern(
			String date, String pattern) throws LIMSRuntimeException {
		Locale locale = SystemConfiguration.getInstance().getDefaultLocale();
		SimpleDateFormat format = new SimpleDateFormat(pattern, locale);

		Timestamp returnTimestamp = null;
		if (!StringUtil.isNullorNill(date)) {
			try {
				returnTimestamp = new Timestamp(format.parse(date).getTime());
			} catch (ParseException e) {
				LogEvent.logError("DateUtil",
						"convertStringDateToTimestampWithPattern()\nlocale: "
								+ locale + "\nPattern: " + pattern + "\nDate: "
								+ date, e.toString());
				throw new LIMSRuntimeException("Error parsing date", e);
			}
		}
		return returnTimestamp;

	}

	// TIMESTAMP
	public static Timestamp convertStringTimeToTimestamp(Timestamp date,
			String time) throws LIMSRuntimeException {
		if (!StringUtil.isNullorNill(time) && date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(0, 2)));
			cal.set(Calendar.MINUTE, Integer.valueOf(time.substring(3, 5)));
			date = new Timestamp(cal.getTimeInMillis());
		}
		return date;

	}

	public static String convertSqlDateToStringDate(java.sql.Date date)
			throws LIMSRuntimeException {
		SimpleDateFormat format = new SimpleDateFormat(getDateFormat());
		String returnDate = null;
		if (date != null) {
			try {
				returnDate = format.format(date);
			} catch (Exception e) {
				LogEvent.logError("DateUtil", "convertSqlDateToStringDate()",
						e.toString());
				throw new LIMSRuntimeException("Error converting date", e);
			}
		}
		return returnDate;
	}

	public static String convertTimestampToStringDate(Timestamp date)
			throws LIMSRuntimeException {
		return convertTimestampToStringDate(date, false);
	}

	public static String convertTimestampToStringDatePg(Timestamp date)
			throws LIMSRuntimeException {
		return convertTimestampToStringDatePg(date, false);
	}

	public static String convertTimestampToTwoYearStringDate(Timestamp date)
			throws LIMSRuntimeException {
		return convertTimestampToStringDate(date, true);
	}

	private static String convertTimestampToStringDate(Timestamp date,
			boolean twoYearDate) throws LIMSRuntimeException {
		if (date == null) {
			return "";
		}

		String pattern = getDateFormat();
		if (twoYearDate) {
			pattern = pattern.replace("yyyy", "yy");
		}

		SimpleDateFormat format = new SimpleDateFormat(pattern);
		String returnDate;

		try {
			returnDate = format.format(date);
		} catch (Exception e) {

			LogEvent.logError("DateUtil", "convertTimestampToStringDate()",
					e.toString());
			throw new LIMSRuntimeException("Error converting date", e);
		}

		return returnDate;
	}

	private static String convertTimestampToStringDatePg(Timestamp date,
			boolean twoYearDate) throws LIMSRuntimeException {
		if (date == null) {
			return "";
		}

		String pattern = getDateFormatPg();
		if (twoYearDate) {
			pattern = pattern.replace("yyyy", "yy");
		}

		SimpleDateFormat format = new SimpleDateFormat(pattern);
		String returnDate;

		try {
			returnDate = format.format(date);
		} catch (Exception e) {

			LogEvent.logError("DateUtil", "convertTimestampToStringDate()",
					e.toString());
			throw new LIMSRuntimeException("Error converting date", e);
		}

		return returnDate;
	}

	public static String convertTimestampToStringTime(Timestamp date)
			throws LIMSRuntimeException {
		return convertTimestampToStringTime(date, null);
	}

	// TIMESTAMP
	public static String convertTimestampToStringTime(Timestamp date,
			String stringLocale) throws LIMSRuntimeException {

		String returnTime = null;
		String hours;
		String minutes;
		if (date != null) {
			try {
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				if (cal.get(Calendar.HOUR_OF_DAY) <= 9) {
					hours = "0" + String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
				} else {
					hours = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
				}

				if (cal.get(Calendar.MINUTE) <= 9) {
					minutes = "0" + String.valueOf(cal.get(Calendar.MINUTE));
				} else {
					minutes = String.valueOf(cal.get(Calendar.MINUTE));
				}

				returnTime = hours + ":" + minutes;
			} catch (Exception e) {
				LogEvent.logError("DateUtil", "convertTimestampToStringTime()",
						e.toString());
				throw new LIMSRuntimeException("Error converting date", e);
			}
		}

		return returnTime;
	}

	// Decodes a time value in "hh:mm:ss" format and returns it as milliseconds
	// since midnight.
	public static synchronized int decodeTime(String s) throws Exception {
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
		// System.out.println("Passed in this time " +s);
		TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
		f.setTimeZone(utcTimeZone);
		f.setLenient(false);
		ParsePosition p = new ParsePosition(0);
		Date d = f.parse(s, p);
		if (d == null || !StringUtil.isRestOfStringBlank(s, p.getIndex())) {
			throw new Exception("Invalid time value (hh:mm:ss): \"" + s + "\".");
		}
		return (int) d.getTime();
	}

	public static Timestamp formatStringToTimestamp(String ts) {

		StringBuffer tssb = new StringBuffer();
		tssb.append(ts);
		if (ts.length() < 23) {
			for (int i = 23; ts.length() < i; i--) {
				tssb.append("0");
			}
		}

		ts = tssb.toString();

		SimpleDateFormat format = new SimpleDateFormat(getDateTimeFormat());

		Timestamp tsToReturn = null;

		if (!GenericValidator.isBlankOrNull(ts)) {
			try {
				java.util.Date date = format.parse(ts);
				tsToReturn = new Timestamp(date.getTime());
			} catch (Exception e) {
				// bugzilla 2154
				LogEvent.logError("DateUtil", "formatStringToTimestamp()",
						e.toString());
				throw new LIMSRuntimeException("Error converting date", e);
			}
		}
		return tsToReturn;
	}

	public static String getTwoDigitYear() {

		int year = new GregorianCalendar().get(Calendar.YEAR) - 2000;

		return String.format("%02d", year);
	}

	public static Timestamp convertAmbiguousStringDateToTimestamp(
			String dateForDisplay) {

		dateForDisplay = adjustAmbiguousDate(dateForDisplay);

		return convertStringDateToTruncatedTimestamp(dateForDisplay);
	}

	public static boolean yearSpecified(String dateString) {
		String[] dateParts = dateString.split("/");

		return dateParts.length == 3
				&& FOUR_DIGITS.matcher(dateParts[2]).find();
	}

	public static String adjustAmbiguousDate(String date) {
		String replaceValue = ConfigurationProperties.getInstance()
				.getPropertyValue(Property.AmbiguousDateValue);

		return date.replaceAll(AMBIGUOUS_DATE_REGEX, replaceValue);
	}

	public static java.sql.Date getNowAsSqlDate() {
		return new java.sql.Date(new Date().getTime());
	}

	public static String getCurrentAgeForDate(Timestamp birthDate,
			Timestamp endDate) {
		if (birthDate != null) {
			long age = endDate.getTime() - birthDate.getTime();

			Date ageDate = new Date(age);

			Calendar calendar = new GregorianCalendar();
			calendar.setTime(ageDate);

			return String.valueOf(calendar.get(Calendar.YEAR) - EPIC);
		}

		return null;
	}

	public static int getDaysInPastForDate(Date date) {
		long age = new Date().getTime() - date.getTime();
		return (int) Math.floor(age / DAY_IN_MILLSEC);

	}

	public static String getCurrentDateAsText() {
		return formatDateAsText(new Date());
	}

	public static String getCurrentDateAsText(String pattern) {
		if (GenericValidator.isBlankOrNull(pattern)) {
			return null;
		}

		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(new Date());

	}

	public static int getAgeInWeeks(Date startDate, Date endDate) {
		long duration = endDate.getTime() - startDate.getTime();
		return (int) Math.floor(duration / WEEK_MS);
	}

	public static int getAgeInDays(Date startDate, Date endDate) {
		long duration = endDate.getTime() - startDate.getTime();
		return (int) Math.floor(duration / DAY_IN_MILLSEC);
	}

	public static int getAgeInMonths(Date startDate, Date endDate) {
		Calendar start = new GregorianCalendar();
		start.setTime(startDate);
		int startMOY = start.get(Calendar.MONTH);
		int startYear = start.get(Calendar.YEAR);
		Calendar end = new GregorianCalendar();
		end.setTime(endDate);
		int endMOY = end.get(Calendar.MONTH);
		int endYear = end.get(Calendar.YEAR);
		// months between Jan. of start year and Jan. of end year
		int dMons = (endYear - startYear) * 12;
		// correct to actual months.
		dMons += endMOY - startMOY;
		// if the start day of month is after end day of month we have one too
		// months.
		if (start.get(Calendar.DAY_OF_MONTH) > end.get(Calendar.DAY_OF_MONTH)) {
			--dMons;
		}
		return dMons;
	}

	public static int getAgeInYears(Date startDate, Date endDate) {
		Calendar start = new GregorianCalendar();
		start.setTime(startDate);
		Calendar end = new GregorianCalendar();
		end.setTime(endDate);
		int year = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
		if (start.get(Calendar.MONTH) > end.get(Calendar.MONTH)
				|| (start.get(Calendar.MONTH) == end.get(Calendar.MONTH) && start
						.get(Calendar.DAY_OF_MONTH) > end
						.get(Calendar.DAY_OF_MONTH))) {
			--year;
		}
		return year;
	}

	public static Timestamp getTimestampAtMidnightForDaysAgo(int days) {
		Calendar now = new GregorianCalendar();
		now.add(Calendar.DAY_OF_YEAR, -days);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		return new Timestamp(now.getTimeInMillis());
	}

	public static Timestamp getTimestampForBeginingOfYear() {
		Calendar now = new GregorianCalendar();
		now.set(Calendar.MONTH, 0);
		now.set(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		return new Timestamp(now.getTimeInMillis());
	}

	public static Timestamp getTimestampForBeginningOfMonth() {
		Calendar now = new GregorianCalendar();
		now.set(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		return new Timestamp(now.getTimeInMillis());
	}

	public static int getMonthForTimestamp(Timestamp date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		return calendar.get(Calendar.MONTH);
	}

	public static int getCurrentMonth() {
		return new GregorianCalendar().get(Calendar.MONTH);
	}

	public static Timestamp getTimestampForBeginningOfMonthAgo(int months) {
		Calendar now = new GregorianCalendar();
		now.add(Calendar.MONTH, -months);
		now.set(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		return new Timestamp(now.getTimeInMillis());
	}

	public static int getCurrentYear() {
		return new GregorianCalendar().get(Calendar.YEAR);
	}

	public static int getCurrentHour() {
		return new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
	}

	public static int getCurrentMinute() {
		return new GregorianCalendar().get(Calendar.MINUTE);
	}

	public static Timestamp getNowAsTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	public static String convertTimestampToStringDateAndConfiguredHourTime(
			Timestamp timestamp) {
		if (ConfigurationProperties.getInstance().isPropertyValueEqual(
				Property.CLOCK_24, "true")) {
			return convertTimestampToStringDateAndTime(timestamp);
		} else {
			return convertTimestampToStringDateAnd12HourTime(timestamp);
		}
	}

	public static String convertTimestampToStringDateAndTime(Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		return new SimpleDateFormat(getDateTimeFormat()).format(timestamp);
	}

	public static String convertTimestampToStringDateAnd12HourTime(
			Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		return new SimpleDateFormat(getDateTime12HourFormat())
				.format(timestamp);
	}

	public static String convertTimestampToStringConfiguredHourTime(
			Timestamp timestamp) {
		if (ConfigurationProperties.getInstance().isPropertyValueEqual(
				Property.CLOCK_24, "true")) {
			return convertTimestampToStringHourTime(timestamp);
		} else {
			return convertTimestampToString12HourTime(timestamp);
		}
	}

	public static String convertTimestampToString12HourTime(Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		return new SimpleDateFormat("KK:mm a").format(timestamp);
	}

	public static String convertTimestampToStringHourTime(Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		return new SimpleDateFormat("HH:mm").format(timestamp);
	}

	public static java.sql.Date convertTimestampToSqlDate(Timestamp timestamp) {
		return new java.sql.Date(timestamp.getTime());
	}

	public static Timestamp convertSqlDateToTimestamp(java.sql.Date date) {
		return new Timestamp(date.getTime());
	}

	public static Object nowTimeAsText() {
		return convertTimestampToStringTime(getNowAsTimestamp());
	}

	public static Timestamp convertStringDateStringTimeToTimestamp(String date,
			String time) {
		if (!GenericValidator.isBlankOrNull(date)
				&& !GenericValidator.isBlankOrNull(time))
			date = date + " " + time;
		else if (!GenericValidator.isBlankOrNull(date)
				&& GenericValidator.isBlankOrNull(time))
			date = date + " 00:00";
		else
			return null;
		return convertStringDateToTimestamp(date);

	}

	/**
	 * The purpose of this is to not overwrite an old value with a less
	 * specified new value If the new time is empty but the dates are the same
	 * then return the timestamp of the old date/time If the dates differ use
	 * the new date/time
	 * 
	 * @param oldDate
	 * @param oldTime
	 * @param newDate
	 * @param newTime
	 * @return
	 */
	public static Timestamp convertStringDatePreserveStringTimeToTimestamp(
			String oldDate, String oldTime, String newDate, String newTime) {
		if (!GenericValidator.isBlankOrNull(newTime)) {
			return convertStringDateStringTimeToTimestamp(newDate, newTime);
		}

		if (newDate != null && newDate.equals(oldDate)) {
			return convertStringDateStringTimeToTimestamp(oldDate, oldTime);
		}

		return convertStringDateStringTimeToTimestamp(newDate, newTime);
	}

	public static java.sql.Date addDaysToSQLDate(java.sql.Date date, int days) {
		return new java.sql.Date(date.getTime() + (days * DAY_IN_MILLSEC));
	}

	public static String getDateUserPrompt() {
		Locale locale = getDateFormatLocale();
		String yearRepresentation = StringUtil
				.getMessageForKey("date.format.display.year");
		String dayRepresentation = StringUtil
				.getMessageForKey("date.format.display.day");
		return StringUtil.getMessageForKeyAndLocale("sample.date.format",
				dayRepresentation, yearRepresentation, locale);

	}

	public static String getDateFormat() {
		Locale locale = getDateFormatLocale();
		return StringUtil.getMessageForKeyAndLocale("date.format.formatKey",
				locale);
	}

	public static String getDateFormatPg() {
		Locale locale = getDateFormatLocale();
		return StringUtil.getMessageForKeyAndLocale(
				"date.format.validate.pgsql", locale);
	}

	public static String getDateTimeFormat() {
		Locale locale = getDateFormatLocale();
		return StringUtil.getMessageForKeyAndLocale(
				"timestamp.format.formatKey", locale);
	}

	public static String getDateTime12HourFormat() {
		Locale locale = getDateFormatLocale();
		return StringUtil.getMessageForKeyAndLocale(
				"timestamp.format.formatKey.12", locale);
	}

	public static Locale getDateFormatLocale() {
		return SystemConfiguration.getInstance().getLocaleByLocalString(
				ConfigurationProperties.getInstance().getPropertyValue(
						Property.DEFAULT_DATE_LOCALE));
	}
	
	/**
	 * nhuql.gv Check string is valid date
	 * 
	 * @param dateToValidate
	 * @param dateFromat
	 * @return
	 */
	public static boolean isDateValid(String dateToValidate, String dateFromat) {
		if (dateToValidate == null) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		sdf.setLenient(false);
		try {
			Date date = sdf.parse(dateToValidate);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
}
