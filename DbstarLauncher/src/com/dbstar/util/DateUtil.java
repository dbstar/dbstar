package com.dbstar.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Loader;

public class DateUtil {

	public static final String DateFormat1 = "yyyy-MM-dd HH:mm:ss";
	public static final String DateFormat2 = "yyyy-MM-01 00:00:00";
	public static final String DateFormat3 = "yyyy-01-01 00:00:00";
	public static final String DateFormat4 = "yyyy-MM-dd";
	public static final String DateFormat5 = "yyyy-MM-dd  00:00:00";
	public static final String DateFormat6 = "yyyyMMddHHmmss";
	public static Date getDateFromStr(String dateStr, String format) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	public static String constructDateStr(String year, String month,
			String day, String format) {
		String datetime = null;

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.valueOf(year));
		c.set(Calendar.MONTH, Integer.valueOf(month));
		c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));

		Date date = c.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		datetime = sdf.format(date);
		System.out.println("Date Time : " + datetime);
		
		return datetime;
	}
	
	public static String getStringFromDate(Date date,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }	 
        return null;
	}
	
	public static String getStringFromDateString(String dateStr,String format){
	    String str = null;
	    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
	    try {
            Date date = dateFormat.parse(dateStr);
            str = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
	    
	}
	public static String getMothFromString(String dataStr){
	    String moth = null;
	            SimpleDateFormat format = new SimpleDateFormat(DateFormat1);
	            Calendar calendar = Calendar.getInstance();
	            try {
                    calendar.setTime(format.parse(dataStr));
                    moth = String.valueOf(calendar.get(Calendar.MONTH) +1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
	    return moth;
	}
	
}
