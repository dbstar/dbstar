package com.dbstar.multiple.media.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    
    public static final String FORMART1 = "MM\n月\ndd\n日\n ";
    public static final String FORMART2 = "yyyy/MM/dd  E";
    public static final String FORMART3 = "yyyy-MM-dd";
    
    public static String getStringFromDate(Date date,String format){
        String strDate = null;
        
        try{
            SimpleDateFormat dateformat=new SimpleDateFormat(format);   
            strDate= dateformat.format(date);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
        
    }
    public static Date getDate(String dateStr,String format){
        Date date = null;
        SimpleDateFormat dateformat=new SimpleDateFormat(format); 
        try {
            date = dateformat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
        
    }
}
