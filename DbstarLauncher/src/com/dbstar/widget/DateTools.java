package com.dbstar.widget;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**********************************************************************************
Copyright (C), 2011-2012, �������ͨ���缼�����޹�˾. 
FileName:     com.lp.tools.DateTools.java
Author:��     ������ xiaomin.qin
Version :     V3.0
Date:         2011-11-10
Description:��

 ***********************************************************************************
 History:������update past records 
 <Author>��                         <Date>����  <Version>��  <Description> 
 ***********************************************************************************/ 

public class DateTools {
	



	/**
	 * �õ����꣬��ʽΪyyyy
	 * @return ��ȡ����yyyy
	 */
	public static String getCurrentYear()
	{	
		DateFormat day = new SimpleDateFormat("yyyy");
		String str_day = day.format(new Date());
		return str_day;
	}

	/**
	 * �õ��������ڣ���ʽΪyyyyMM
	 * @return ��ȡ��������yyyyMM
	 */
	public static String getCurrentMonth()
	{	
		DateFormat day = new SimpleDateFormat("yyyyMM");
		String str_day = day.format(new Date());
		return str_day;
	}

	/**
	 * �õ���������ڣ���ʽΪyyyyMMdd
	 * @return ��ȡ��������yyyyMMdd
	 */
	public static String getCurrentday()
	{	
		DateFormat day = new SimpleDateFormat("yyyyMMdd");
		String str_day = day.format(new Date());
		return str_day;
	}
	/**
	 * �õ�����˿��Ǽ��㣬��ʽΪHH
	 * @return ��ȡ��������HH
	 */
	public static String getCurrentHour()
	{	
		DateFormat day = new SimpleDateFormat("HH");
		String str_day = day.format(new Date());
		return str_day;
	}


	/**
	 * ��ȡ�����Ǽ���
	 */
	public static int getDayOfMonth()
	{
		DateFormat day = new SimpleDateFormat("dd");
		String str_day = day.format(new Date());
		return Integer.valueOf(str_day);
	}

	/***
	 * ��ȡ���������ڼ�
	 * @return
	 */

	public static int getDayOfWeek()
	{
		Calendar calendar = Calendar.getInstance();
		return (calendar.get(Calendar.DAY_OF_WEEK)-1);

	}





	/**
	 * �õ�n��ǰ��������ڣ���ʽΪyyyyMMdd
	 * @return ������һ�������
	 */
	public static String getNextDay(int n)
	{
		DateFormat day = new SimpleDateFormat("yyyyMMdd");
		String str_day = day.format(new Date());
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");

		try {
			c.setTime(dateFormat.parse(str_day));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ��һ�������
		c.add(Calendar.DAY_OF_MONTH, n);
		String nextTime=dateFormat.format(c.getTime());
		return nextTime;
	}


	/**
	 * �õ�nǰ������µ��·ݣ���ʽΪyyyyMM
	 * n ǰ��������ٸ����·�
	 * @return �����ϸ��µ��·�
	 */
	public static String getLastMonth(int n)
	{
		DateFormat day = new SimpleDateFormat("yyyyMM");
		String str_day = day.format(new Date());
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMM");

		try {
			c.setTime(dateFormat.parse(str_day));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		c.add(Calendar.MONTH, n);
		String nextTime=dateFormat.format(c.getTime());
		return nextTime;
	}
	/**
	 * �õ�nǰ������µ��·ݣ���ʽΪMM
	 * n ǰ��������ٸ����·�
	 * @return �����ϸ��µ��·�
	 */
	public static String getOnlyLastMonth(int n)
	{
		DateFormat day = new SimpleDateFormat("MM");
		String str_day = day.format(new Date());
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormat=new SimpleDateFormat("MM");

		try {
			c.setTime(dateFormat.parse(str_day));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		c.add(Calendar.MONTH, n);
		String nextTime=dateFormat.format(c.getTime());
		return nextTime;
	}




	/**
	 * ���ĳһ�µ�������
	 * @param String���ͣ�ĳһ�£����磺2010103
	 * @return int���ͣ�����
	 */
	public static int getMonthSize(String time) {
		int     size    = 0;
		int     year    = Integer.valueOf(time.substring(0, 4));
		int     month   = Integer.valueOf(time.substring(4));
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			size = 31;
			break;
		case 2:
			if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
				size = 29;
			} else {
				size = 28;
			}
			break;
		default:
			size = 30;
			break;
		}
		return size;
	}


	/**
	 * ���������ַ��Ƿ����·������ʽ
	 * @param �ַ�
	 * @return int���ͣ������ʽ������ǰ�·ݣ�0�������ʽ����ȷ��1�������ʽ��ȷ��2
	 */
	public int checkMonth(String time) {
		if (time.length() != 7) {
			return 0;
		}
		if ((!String.valueOf(time.charAt(4)).equals("-"))) {
			return 0;
		}
		DateFormat           year            = new SimpleDateFormat("yyyyMM");
		String               current         = year.format(new Date());
		Pattern              Pattern_gettime = Pattern.compile("-");
		String[]             item            = Pattern_gettime.split(time);
		String               str_year        = item[0] + item[1];
		if (Integer.valueOf(current) < Integer.valueOf(str_year)) {
			return 0;
		}
		String reg_minute = "((^((2\\d{3})"
				+ "|([2-9]\\d{3}))([-])(10|11|12|0?[123456789])$))";
		Pattern p_date = Pattern.compile(reg_minute);
		Matcher m_date = p_date.matcher(time);
		if (m_date.matches()) {
			return 1;
		} else {
			return 0;
		}
	}




	/**
	 * �õ�һ����·ݣ���ʽΪyyyyMM
	 * @return ��ȡһ����·�
	 */
	public static void GetOneYearMonth(ArrayList<String> month)
	{	
		DateFormat day = new SimpleDateFormat("yyyy-MM");
		String str_day = day.format(new Date());
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM");
		try {
			c.setTime(dateFormat.parse(str_day));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int sum = 0;
		String monthItem;
		month.add(str_day);
		for(int i = 0; i < 11; i++)
		{
			c.add(Calendar.MONTH, -1);
			monthItem=dateFormat.format(c.getTime());
			if(Integer.parseInt(monthItem.substring(5))==1)
			{
				month.add(monthItem);
				sum++;
				break;
			}		    
			month.add(monthItem);
			sum++;
		}
		if(sum!=12)
		{			
			int year = Integer.parseInt(str_day.substring(0, 4))-1;
			for(int i = 12; i > sum; i--)
			{
				month.add(String.valueOf(year)+"-"+String.valueOf(i));
			}
		}
		return;
	}


	/**
	 * �������뷽��
	 * @param v ������ֵ
	 * @param scale С����λ��
	 * @return
	 */
	public   static   String   round(double v,int scale){     
		String   temp= "#,##0.";     
		for   (int   i=0;i <scale   ;i++   )     
		{     
			temp+= "0";     
		}     
		if(scale == 0)
		{
			return  (new DecimalFormat(temp).format(v)).replace('.', ' ').trim();    
		}
		else
		{
			return  (new DecimalFormat(temp).format(v));    
		} 
	}  

	/**
	 * 
	 * @param  yyyy-MM-dd HH:mm:ss
	 * @return  yyyyMMdd
	 */
	public static String dateToSring(String strdate){

		SimpleDateFormat   from=new   SimpleDateFormat( "yyyy-MM-dd HH:mm:ss"); 
		SimpleDateFormat   to=new   SimpleDateFormat( "yyyyMMdd"); 
		Date temp = null;
		try {
			temp = from.parse(strdate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return to.format(temp);

	}

	/**
	 * 
	 * @param  yyyyMMdd
	 * @return  yyyy-MM-dd HH:mm:ss
	 */
	public static String sringToDate(String strdate){

		try {
			SimpleDateFormat   from=new   SimpleDateFormat( "yyyyMMdd"); 
			SimpleDateFormat   to=new   SimpleDateFormat( "yyyy-MM-dd HH:mm:ss"); 

			Date temp = null;

			temp = from.parse(strdate);


			return to.format(temp);
		} catch (Exception e) {
			// TODO: handle exception
			return "";//����쳣���ؿմ�

		}
	}

	/**
	 * 
	 * @param  yyyyMM
	 * @return  yyyy-MM-dd HH:mm:ss
	 */
	public static String sringToMonth(String strdate){

		try {
			SimpleDateFormat   from=new   SimpleDateFormat( "yyyyMM"); 
			SimpleDateFormat   to=new   SimpleDateFormat( "yyyy-MM-dd HH:mm:ss"); 

			Date temp = null;

			temp = from.parse(strdate);


			return to.format(temp);
		} catch (Exception e) {
			// TODO: handle exception
			return "";//����쳣���ؿմ�

		}
	}

	/**
	 * 
	 * @param  yyyy
	 * @return  yyyy-MM-dd HH:mm:ss
	 */
	public static String sringToYear(String strdate){

		try {
			SimpleDateFormat   from=new   SimpleDateFormat( "yyyy"); 
			SimpleDateFormat   to=new   SimpleDateFormat( "yyyy-MM-dd HH:mm:ss"); 

			Date temp = null;

			temp = from.parse(strdate);


			return to.format(temp);
		} catch (Exception e) {
			// TODO: handle exception
			return "";//����쳣���ؿմ�

		}
	}



	/**
	 * 
	 * @param  yyyyMMdd
	 * @return  yyyy-MM-dd || yyyy-MM || yyyy
	 */
	public static String yyyyMMddTo_date(String strdate){

		Date temp = null;
		SimpleDateFormat   from=null;
		SimpleDateFormat   to = null;
		try {
			switch (strdate.length()) {
			case 8:
				from = new   SimpleDateFormat( "yyyyMMdd"); 
				to = new   SimpleDateFormat( "yyyy-MM-dd"); 
				temp = from.parse(strdate);
				break;
			case 6:
				from = new   SimpleDateFormat( "yyyyMM"); 
				to = new   SimpleDateFormat( "yyyy-MM"); 
				temp = from.parse(strdate);
				break;
			case 4:
				from = new   SimpleDateFormat( "yyyy"); 
				to = new   SimpleDateFormat( "yyyy"); 
				temp = from.parse(strdate);
				break;

			default:
				break;
			}
			return to.format(temp);
		} catch (Exception e) {
			// TODO: handle exception
			return "";//����쳣���ؿմ�

		}
	}



	/**
	 * 
	 * @param  yyyyMMdd
	 * @return  yyyy-MM-dd
	 */
	public static String yyyyMMddToyyyy_MM_dd(String stdate,String sysdate){

		try {
			String strdate=null;
			if(stdate.length()==4){
				strdate = stdate+sysdate.substring(5,7)+ sysdate.substring(8,10);
			}else if(stdate.length()==6){
				strdate = stdate+sysdate.substring(8,10);
			}else{
				strdate=stdate;
			}

			SimpleDateFormat   from=new   SimpleDateFormat( "yyyyMMdd"); 
			SimpleDateFormat   to=new   SimpleDateFormat( "yyyy-MM-dd"); 

			Date temp = null;

			temp = from.parse(strdate);


			return to.format(temp);
		} catch (Exception e) {
			// TODO: handle exception
			return "";//����쳣���ؿմ�

		}
	}

	/**
	 * 
	 * @param  yyyy-MM-dd
	 * @return  yyyyMMdd
	 */
	public static String yyyy_MM_ddToyyyyMMdd(String strdate){

		try {

			SimpleDateFormat   from=new   SimpleDateFormat( "yyyy-MM-dd"); 
			SimpleDateFormat   to=new   SimpleDateFormat( "yyyyMMdd"); 
			Date temp = null;

			temp = from.parse(strdate);


			return to.format(temp);
		} catch (Exception e) {
			// TODO: handle exception
			return "";//����쳣���ؿմ�

		}
	}
}
