package com.dbstar.widget;

import java.lang.Character.UnicodeBlock;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class CommondTools {


	public static String unicodeToUtf8(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	public static String utf8ToUnicode(String inStr) {

		char[] myBuffer = inStr.toCharArray();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < inStr.length(); i++) {
			UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
			if (ub == UnicodeBlock.BASIC_LATIN) {
				sb.append(myBuffer[i]);
			} else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
				int j = (int) myBuffer[i] - 65248;
				sb.append((char) j);
			} else {
				int s = (int) myBuffer[i];
				String hexS = Integer.toHexString(s);
				String unicode = "\\u" + hexS;

				sb.append(unicode.toLowerCase());
			}
		}
		return sb.toString();
	}

	
	public static String getTimestamp(String user_time) {
		String timestamp = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		TimeZone l_timezone = TimeZone.getTimeZone("GMT-0 ");
		sdf.setTimeZone(l_timezone);
		Date d;
		try {

			d = sdf.parse(user_time);
			long l = d.getTime();

			timestamp = String.valueOf(l).substring(0, 10);

		} catch (ParseException e) {

			e.printStackTrace();
		}

		
		return timestamp;
	}

	public static String getStrTime(String timestamp) {
		String user_Time = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		TimeZone l_timezone = TimeZone.getTimeZone("GMT+0");
		sdf.setTimeZone(l_timezone);

		long lcc_time = Long.valueOf(timestamp);
		user_Time = sdf.format(new Date(lcc_time * 1000L));

		return user_Time;

	}

	public static String getTimestamp8(String user_time) {
		String timestamp = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		TimeZone l_timezone = TimeZone.getTimeZone("GMT+8 ");
		sdf.setTimeZone(l_timezone);
		Date d;

		try {

			d = sdf.parse(user_time);
			long l = d.getTime();
			timestamp = String.valueOf(l).substring(0, 10);

		} catch (ParseException e) {

			e.printStackTrace();
		}
		
		return timestamp;
	}

	public static String getStrTime8(String timestamp) {
		String user_Time = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		TimeZone l_timezone = TimeZone.getTimeZone("GMT+8");
		sdf.setTimeZone(l_timezone);

		long lcc_time = Long.valueOf(timestamp);
		user_Time = sdf.format(new Date(lcc_time * 1000L));
		

		return user_Time;

	}




	




	public static String denaryToHex(int denary) {
		String hex = null;

		hex = Integer.toHexString(denary);

		return hex;

	}

	public static int hexToDenary(String hex) {
		int denary = 0;

		denary = Integer.valueOf(String.valueOf(hex), 16);
		return denary;
	}

	public static String hexToBinary(String hex) {
		String binary = null;
		int denary;

		denary = hexToDenary(hex);
		binary = Integer.toBinaryString(denary);

		return binary;
	}

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
	

}
