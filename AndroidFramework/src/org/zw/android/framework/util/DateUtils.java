package org.zw.android.framework.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * DateUtils
 * 
 * @author zhouwei
 *
 */
public final class DateUtils {

	public static final String YEAR_FORMAT			= "yyyy" ;
	public static final String DATE_FORMAT 			= "yyyy-MM-dd";
	public static final String DATE_MONTH 			= "MM-dd";
	public static final String TIME_FORMAT 			= "HH:mm";
	public static final String DATE_TIME_FORMAT 	= "yyyy-MM-dd HH:mm:ss";
	public static final String PART_TIME_FORMAT 	= "yyyy-MM-dd HH:mm";
	public static final String DATE_PARRERN 		= "\\d{1,4}\\-\\d{1,2}\\-\\d{1,2}";
	public static final String DATE_TIME_PATTERN 	= "\\d{1,4}\\-\\d{1,2}\\-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}";
	
	private DateUtils() {
		
	}
	
	public static Date toDate(String dateString) {

		if (StringUtils.matches(dateString, DATE_PARRERN)) {
			return toDate(dateString, DATE_FORMAT);
		}
		if (StringUtils.matches(dateString, DATE_TIME_PATTERN)) {
			return toDate(dateString, DATE_TIME_FORMAT);
		}
		return null;
	}
	
	public static Date toDate(long milliseconds) {
		
		if(milliseconds <= 0) return null ;
		
		try{
			return new Date(milliseconds) ;
		} catch(Exception e){
			e.printStackTrace() ;
		}
		return null;
	}
	
	public static Long getDaysBetween(Date endDate ,Date startDate) {
		
		if(endDate == null || startDate == null){
			return -1L ;
		}
		
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return (toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);
	}
	
	public static Date toDate(String dateString, String pattern) {
		
		if (StringUtils.isEmpty(dateString)) {
			return null;
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		ParsePosition position = new ParsePosition(0);
		Date strtodate = formatter.parse(dateString, position);
		return strtodate;
	}

	public static String toDateString(Date date) {
		return toDateString(date, DATE_FORMAT);
	}

	public static String toDateString(Date date, String pattern) {
		
		if (date == null) {
			return "";
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}

	public static String pad(int string) {
		if (string >= 10) {
			return String.valueOf(string);
		} else {
			return "0" + String.valueOf(string);
		}
	}

	public static Date getCurrtentTimes() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
		Date date = calendar.getTime();
		return date;
	}

	public static int[] getDateTime(Date date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
		int year = 0, month = 0, day = 0, hour = 0, min = 0;
		calendar.setTime(date);
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		min = calendar.get(Calendar.MINUTE);
		int[] times = { year, month, day, hour, min };
		return times;
	}

	public static long compare(Date d1, Date d2) {

		if (d1 == null) {
			return -1 ;
		}
		if (d2 == null) {
			return -1;
		}
		
		return d1.getTime() - d2.getTime();
	}

	public static Date countDate(Date date,int type,int num) {

		if (num < 1) {
			return date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(type, num);
		return calendar.getTime();
	}
	
	public static int getYear(Date date){
		
		if(date == null){
			return getCurrentYear() ;
		}
		
		SimpleDateFormat year = new SimpleDateFormat(YEAR_FORMAT);
		
		try{
			return Integer.valueOf(year.format(date)) ;
		}catch(Exception e){
			return 2013 ;
		}
	}
	
	public static int getMonth(Date date){
		Calendar cl = Calendar.getInstance() ;
		cl.setTime(date) ;
		return cl.get(Calendar.MONTH) + 1;
	}
	
	public static int getDay(Date date){
		Calendar cl = Calendar.getInstance() ;
		cl.setTime(date) ;
		return cl.get(Calendar.DAY_OF_MONTH) ;
	}
	
	public static int getCurrentYear(){
		return Calendar.getInstance().get(Calendar.YEAR) ;
	}
	
	public static Date addSub(Date date, int num) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, num);
		return calendar.getTime();
	}
}
