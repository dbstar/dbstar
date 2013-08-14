package com.dbstar.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.dbstar.util.LogUtil;

import android.content.Context;
import android.content.res.AssetManager;

public class GDCalendarGB {

	private static final String TAG = "ChineseCalendarGB";
	private int gregorianYear;
	private int gregorianMonth;
	private int gregorianDate;
	private boolean isGregorianLeap;
	private int dayOfYear;
	private int dayOfWeek;
	private int chineseYear;
	private int chineseMonth;
	private int chineseDate;
	private int sectionalTerm;
	private int principleTerm;
	
	private static final String PROPERTY_CHINESEMONTHNAMES = "chineseMonthNames";
	private static final String PROPERTY_PRINCIPLETERMNAMES = "principleTermNames";
	private static final String PROPERTY_SECTIONALTERMNAMES = "sectionalTermNames";
	private static final String PROPERTY_NSTR1 = "nStr1";
	private static final String PROPERTY_NSTR2 = "nStr2";
	private static final String PROPERTY_HANZI_RUN = "Run";
	private static final String PROPERTY_HANZI_YUE = "Yue";
	private static final String PROPERTY_HANZI_CHUSHI = "ChuShi";
	private static final String PROPERTY_HANZI_ERSHI = "ErShi";
	private static final String PROPERTY_HANZI_SANSHI = "SanShi";
	
	private char[] daysInGregorianMonth = { 31, 28, 31, 30, 31, 30, 31,
			31, 30, 31, 30, 31 };

	private String[] chineseMonthNames;
	private String[] principleTermNames;
	private String[] sectionalTermNames;
	private String[] nStr1;
	private String[] nStr2;
	private String chuShiStr, erShiStr, sanShiStr, runStr, yueStr;

	public GDCalendarGB(Context context) {	
		setGregorian(1901, 1, 1);
		
		initialize(context);
	}
	
	private void initialize(Context context) {
		AssetManager am = context.getAssets();
		
		try {
			String UTF8 = "utf8";
			int BUFFER_SIZE = 8192;

			InputStream is = am.open("celander_data.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					is, UTF8), BUFFER_SIZE);
			parseDataFile(br);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseDataFile(BufferedReader br) {
		try {
			String line;
			String[] property = new String[2];
			while ((line = br.readLine()) != null) {
				int start = line.indexOf(":");
				property[0] = line.substring(0, start);
				property[1] = line.substring(start + 1);

//				Log.d(TAG, property[0] + "=" + property[1]);

				if (property[0].equals(PROPERTY_CHINESEMONTHNAMES)) {
					chineseMonthNames = property[1].trim().split(",");
				} else if (property[0].equals(PROPERTY_PRINCIPLETERMNAMES)) {
					principleTermNames = property[1].trim().split(",");
				} else if (property[0].equals(PROPERTY_SECTIONALTERMNAMES)) {
					sectionalTermNames = property[1].trim().split(",");
				} else if (property[0].equals(PROPERTY_NSTR1)) {
					nStr1 = property[1].trim().split(",");
				} else if (property[0].equals(PROPERTY_NSTR2)) {
					nStr2 = property[1].trim().split(",");
				} else if (property[0].equals(PROPERTY_HANZI_RUN)) {
					runStr = property[1].trim();
				} else if (property[0].equals(PROPERTY_HANZI_YUE)) {
					yueStr = property[1].trim();
				} else if (property[0].equals(PROPERTY_HANZI_CHUSHI)) {
					chuShiStr = property[1].trim();
				} else if (property[0].equals(PROPERTY_HANZI_ERSHI)) {
					erShiStr = property[1].trim();
				} else if (property[0].equals(PROPERTY_HANZI_SANSHI)) {
					sanShiStr = property[1].trim();
				} else {
					;
				}
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void setGregorian(int y, int m, int d) {
		gregorianYear = y;
		gregorianMonth = m;
		gregorianDate = d;
		isGregorianLeap = isGregorianLeapYear(y);
		dayOfYear = dayOfYear(y, m, d);
		dayOfWeek = dayOfWeek(y, m, d);
		chineseYear = 0;
		chineseMonth = 0;
		chineseDate = 0;
		sectionalTerm = 0;
		principleTerm = 0;
	}

	public static boolean isGregorianLeapYear(int year) {
		boolean isLeap = false;
		if (year % 4 == 0)
			isLeap = true;
		if (year % 100 == 0)
			isLeap = false;
		if (year % 400 == 0)
			isLeap = true;
		return isLeap;
	}

	public int daysInGregorianMonth(int y, int m) {
		int d = daysInGregorianMonth[m - 1];
		if (m == 2 && isGregorianLeapYear(y))
			d++;
		return d;
	}

	public int dayOfYear(int y, int m, int d) {
		int c = 0;
		for (int i = 1; i < m; i++) {
			c = c + daysInGregorianMonth(y, i);
		}
		c = c + d;
		return c;
	}

	public int dayOfWeek(int y, int m, int d) {
		int w = 1;
		y = (y - 1) % 400 + 1;
		int ly = (y - 1) / 4;
		ly = ly - (y - 1) / 100;
		ly = ly + (y - 1) / 400;
		int ry = y - 1 - ly;
		w = w + ry;
		w = w + 2 * ly;
		w = w + dayOfYear(y, m, d);
		w = (w - 1) % 7 + 1;
		return w;
	}

	private static char[] chineseMonths = {
			0x00, 0x04, 0xad, 0x08, 0x5a, 0x01, 0xd5, 0x54, 0xb4, 0x09, 0x64,
			0x05, 0x59, 0x45, 0x95, 0x0a, 0xa6, 0x04, 0x55, 0x24, 0xad, 0x08,
			0x5a, 0x62, 0xda, 0x04, 0xb4, 0x05, 0xb4, 0x55, 0x52, 0x0d, 0x94,
			0x0a, 0x4a, 0x2a, 0x56, 0x02, 0x6d, 0x71, 0x6d, 0x01, 0xda, 0x02,
			0xd2, 0x52, 0xa9, 0x05, 0x49, 0x0d, 0x2a, 0x45, 0x2b, 0x09, 0x56,
			0x01, 0xb5, 0x20, 0x6d, 0x01, 0x59, 0x69, 0xd4, 0x0a, 0xa8, 0x05,
			0xa9, 0x56, 0xa5, 0x04, 0x2b, 0x09, 0x9e, 0x38, 0xb6, 0x08, 0xec,
			0x74, 0x6c, 0x05, 0xd4, 0x0a, 0xe4, 0x6a, 0x52, 0x05, 0x95, 0x0a,
			0x5a, 0x42, 0x5b, 0x04, 0xb6, 0x04, 0xb4, 0x22, 0x6a, 0x05, 0x52,
			0x75, 0xc9, 0x0a, 0x52, 0x05, 0x35, 0x55, 0x4d, 0x0a, 0x5a, 0x02,
			0x5d, 0x31, 0xb5, 0x02, 0x6a, 0x8a, 0x68, 0x05, 0xa9, 0x0a, 0x8a,
			0x6a, 0x2a, 0x05, 0x2d, 0x09, 0xaa, 0x48, 0x5a, 0x01, 0xb5, 0x09,
			0xb0, 0x39, 0x64, 0x05, 0x25, 0x75, 0x95, 0x0a, 0x96, 0x04, 0x4d,
			0x54, 0xad, 0x04, 0xda, 0x04, 0xd4, 0x44, 0xb4, 0x05, 0x54, 0x85,
			0x52, 0x0d, 0x92, 0x0a, 0x56, 0x6a, 0x56, 0x02, 0x6d, 0x02, 0x6a,
			0x41, 0xda, 0x02, 0xb2, 0xa1, 0xa9, 0x05, 0x49, 0x0d, 0x0a, 0x6d,
			0x2a, 0x09, 0x56, 0x01, 0xad, 0x50, 0x6d, 0x01, 0xd9, 0x02, 0xd1,
			0x3a, 0xa8, 0x05, 0x29, 0x85, 0xa5, 0x0c, 0x2a, 0x09, 0x96, 0x54,
			0xb6, 0x08, 0x6c, 0x09, 0x64, 0x45, 0xd4, 0x0a, 0xa4, 0x05, 0x51,
			0x25, 0x95, 0x0a, 0x2a, 0x72, 0x5b, 0x04, 0xb6, 0x04, 0xac, 0x52,
			0x6a, 0x05, 0xd2, 0x0a, 0xa2, 0x4a, 0x4a, 0x05, 0x55, 0x94, 0x2d,
			0x0a, 0x5a, 0x02, 0x75, 0x61, 0xb5, 0x02, 0x6a, 0x03, 0x61, 0x45,
			0xa9, 0x0a, 0x4a, 0x05, 0x25, 0x25, 0x2d, 0x09, 0x9a, 0x68, 0xda,
			0x08, 0xb4, 0x09, 0xa8, 0x59, 0x54, 0x03, 0xa5, 0x0a, 0x91, 0x3a,
			0x96, 0x04, 0xad, 0xb0, 0xad, 0x04, 0xda, 0x04, 0xf4, 0x62, 0xb4,
			0x05, 0x54, 0x0b, 0x44, 0x5d, 0x52, 0x0a, 0x95, 0x04, 0x55, 0x22,
			0x6d, 0x02, 0x5a, 0x71, 0xda, 0x02, 0xaa, 0x05, 0xb2, 0x55, 0x49,
			0x0b, 0x4a, 0x0a, 0x2d, 0x39, 0x36, 0x01, 0x6d, 0x80, 0x6d, 0x01,
			0xd9, 0x02, 0xe9, 0x6a, 0xa8, 0x05, 0x29, 0x0b, 0x9a, 0x4c, 0xaa,
			0x08, 0xb6, 0x08, 0xb4, 0x38, 0x6c, 0x09, 0x54, 0x75, 0xd4, 0x0a,
			0xa4, 0x05, 0x45, 0x55, 0x95, 0x0a, 0x9a, 0x04, 0x55, 0x44, 0xb5,
			0x04, 0x6a, 0x82, 0x6a, 0x05, 0xd2, 0x0a, 0x92, 0x6a, 0x4a, 0x05,
			0x55, 0x0a, 0x2a, 0x4a, 0x5a, 0x02, 0xb5, 0x02, 0xb2, 0x31, 0x69,
			0x03, 0x31, 0x73, 0xa9, 0x0a, 0x4a, 0x05, 0x2d, 0x55, 0x2d, 0x09,
			0x5a, 0x01, 0xd5, 0x48, 0xb4, 0x09, 0x68, 0x89, 0x54, 0x0b, 0xa4,
			0x0a, 0xa5, 0x6a, 0x95, 0x04, 0xad, 0x08, 0x6a, 0x44, 0xda, 0x04,
			0x74, 0x05, 0xb0, 0x25, 0x54, 0x03 };

	private static int baseYear = 1901;
	private static int baseMonth = 1;
	private static int baseDate = 1;
	private static int baseIndex = 0;
	private static int baseChineseYear = 4598 - 1;
	private static int baseChineseMonth = 11;
	private static int baseChineseDate = 11;

	public int computeChineseFields() {
		if (gregorianYear < 1901 || gregorianYear > 2100)
			return 1;
		int startYear = baseYear;
		int startMonth = baseMonth;
		int startDate = baseDate;
		chineseYear = baseChineseYear;
		chineseMonth = baseChineseMonth;
		chineseDate = baseChineseDate;

		if (gregorianYear >= 2000) {
			startYear = baseYear + 99;
			startMonth = 1;
			startDate = 1;
			chineseYear = baseChineseYear + 99;
			chineseMonth = 11;
			chineseDate = 25;
		}
		int daysDiff = 0;
		for (int i = startYear; i < gregorianYear; i++) {
			daysDiff += 365;
			if (isGregorianLeapYear(i))
				daysDiff += 1; // leap year
		}
		for (int i = startMonth; i < gregorianMonth; i++) {
			daysDiff += daysInGregorianMonth(gregorianYear, i);
		}
		daysDiff += gregorianDate - startDate;

		chineseDate += daysDiff;
		int lastDate = daysInChineseMonth(chineseYear, chineseMonth);
		int nextMonth = nextChineseMonth(chineseYear, chineseMonth);
		while (chineseDate > lastDate) {
			if (Math.abs(nextMonth) < Math.abs(chineseMonth))
				chineseYear++;
			chineseMonth = nextMonth;
			chineseDate -= lastDate;
			lastDate = daysInChineseMonth(chineseYear, chineseMonth);
			nextMonth = nextChineseMonth(chineseYear, chineseMonth);
		}

		LogUtil.d(TAG, "month = " + chineseMonth);
		return 0;
	}

	private static int[] bigLeapMonthYears = {
			6, 14, 19, 25, 33, 36, 38, 41, 44, 52, 55, 79, 117, 136, 147, 150,
			155, 158, 185, 193 };

	public static int daysInChineseMonth(int y, int m) {
		int index = y - baseChineseYear + baseIndex;
		int v = 0;
		int l = 0;
		int d = 30;
		if (1 <= m && m <= 8) {
			v = chineseMonths[2 * index];
			l = m - 1;
			if (((v >> l) & 0x01) == 1)
				d = 29;
		} else if (9 <= m && m <= 12) {
			v = chineseMonths[2 * index + 1];
			l = m - 9;
			if (((v >> l) & 0x01) == 1)
				d = 29;
		} else {
			v = chineseMonths[2 * index + 1];
			v = (v >> 4) & 0x0F;
			if (v != Math.abs(m)) {
				d = 0;
			} else {
				d = 29;
				for (int i = 0; i < bigLeapMonthYears.length; i++) {
					if (bigLeapMonthYears[i] == index) {
						d = 30;
						break;
					}
				}
			}
		}
		return d;
	}

	public static int nextChineseMonth(int y, int m) {
		int n = Math.abs(m) + 1;
		if (m > 0) {
			int index = y - baseChineseYear + baseIndex;
			int v = chineseMonths[2 * index + 1];
			v = (v >> 4) & 0x0F;
			if (v == m)
				n = -m;
		}
		if (n == 13)
			n = 1;
		return n;
	}

	private static char[][] sectionalTermMap = {
			{ 7, 6, 6, 6, 6, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 5, 5,
					5, 5, 5, 4, 5, 5 },
			{ 5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 4, 4, 3,
					3, 4, 4, 3, 3, 3 },
			{ 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5,
					5, 5, 4, 5, 5, 5, 5 },
			{ 5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4,
					4, 5, 4, 4, 4, 4, 5 },
			{ 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5,
					5, 5, 4, 5, 5, 5, 5 },
			{ 6, 6, 7, 7, 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5,
					5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 5 },
			{ 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6,
					7, 7, 6, 6, 6, 7, 7 },
			{ 8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7,
					7, 7, 6, 7, 7, 7, 6, 6, 7, 7, 7 },
			{ 8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7,
					7, 7, 6, 7, 7, 7, 7 },
			{ 9, 9, 9, 9, 8, 9, 9, 9, 8, 8, 9, 9, 8, 8, 8, 9, 8, 8, 8, 8, 7, 8,
					8, 8, 7, 7, 8, 8, 8 },
			{ 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7,
					7, 7, 6, 6, 7, 7, 7 },
			{ 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6,
					7, 7, 6, 6, 6, 7, 7 } };
	private static char[][] sectionalTermYear = {
			{ 13, 49, 85, 117, 149, 185, 201, 250, 250 },
			{ 13, 45, 81, 117, 149, 185, 201, 250, 250 },
			{ 13, 48, 84, 112, 148, 184, 200, 201, 250 },
			{ 13, 45, 76, 108, 140, 172, 200, 201, 250 },
			{ 13, 44, 72, 104, 132, 168, 200, 201, 250 },
			{ 5, 33, 68, 96, 124, 152, 188, 200, 201 },
			{ 29, 57, 85, 120, 148, 176, 200, 201, 250 },
			{ 13, 48, 76, 104, 132, 168, 196, 200, 201 },
			{ 25, 60, 88, 120, 148, 184, 200, 201, 250 },
			{ 16, 44, 76, 108, 144, 172, 200, 201, 250 },
			{ 28, 60, 92, 124, 160, 192, 200, 201, 250 },
			{ 17, 53, 85, 124, 156, 188, 200, 201, 250 } };
	private static char[][] principleTermMap = {
			{ 21, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20,
					20, 20, 20, 20, 20, 19, 20, 20, 20, 19, 19, 20 },
			{ 20, 19, 19, 20, 20, 19, 19, 19, 19, 19, 19, 19, 19, 18, 19, 19,
					19, 18, 18, 19, 19, 18, 18, 18, 18, 18, 18, 18 },
			{ 21, 21, 21, 22, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21,
					20, 20, 20, 21, 20, 20, 20, 20, 19, 20, 20, 20, 20 },
			{ 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 20, 20, 20, 20,
					19, 20, 20, 20, 19, 19, 20, 20, 19, 19, 19, 20, 20 },
			{ 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21,
					20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 21 },
			{ 22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22,
					21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 21 },
			{ 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23,
					22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 23 },
			{ 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23,
					22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23 },
			{ 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23,
					22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23 },
			{ 24, 24, 24, 24, 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24,
					23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 23 },
			{ 23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23,
					22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 22 },
			{ 22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 21, 22, 22, 22,
					21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21, 22 } };
	private static char[][] principleTermYear = {
			{ 13, 45, 81, 113, 149, 185, 201 },
			{ 21, 57, 93, 125, 161, 193, 201 },
			{ 21, 56, 88, 120, 152, 188, 200, 201 },
			{ 21, 49, 81, 116, 144, 176, 200, 201 },
			{ 17, 49, 77, 112, 140, 168, 200, 201 },
			{ 28, 60, 88, 116, 148, 180, 200, 201 },
			{ 25, 53, 84, 112, 144, 172, 200, 201 },
			{ 29, 57, 89, 120, 148, 180, 200, 201 },
			{ 17, 45, 73, 108, 140, 168, 200, 201 },
			{ 28, 60, 92, 124, 160, 192, 200, 201 },
			{ 16, 44, 80, 112, 148, 180, 200, 201 },
			{ 17, 53, 88, 120, 156, 188, 200, 201 } };

	public int computeSolarTerms() {
		if (gregorianYear < 1901 || gregorianYear > 2100)
			return 1;
		sectionalTerm = sectionalTerm(gregorianYear, gregorianMonth);
		principleTerm = principleTerm(gregorianYear, gregorianMonth);
		return 0;
	}

	public static int sectionalTerm(int y, int m) {
		if (y < 1901 || y > 2100)
			return 0;
		int index = 0;
		int ry = y - baseYear + 1;
		while (ry >= sectionalTermYear[m - 1][index])
			index++;
		int term = sectionalTermMap[m - 1][4 * index + ry % 4];
		if ((ry == 121) && (m == 4))
			term = 5;
		if ((ry == 132) && (m == 4))
			term = 5;
		if ((ry == 194) && (m == 6))
			term = 6;
		return term;
	}

	public static int principleTerm(int y, int m) {
		if (y < 1901 || y > 2100)
			return 0;
		int index = 0;
		int ry = y - baseYear + 1;
		while (ry >= principleTermYear[m - 1][index])
			index++;
		int term = principleTermMap[m - 1][4 * index + ry % 4];
		if ((ry == 171) && (m == 3))
			term = 21;
		if ((ry == 181) && (m == 5))
			term = 21;
		return term;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Gregorian Year: " + gregorianYear + "\n");
		buf.append("Gregorian Month: " + gregorianMonth + "\n");
		buf.append("Gregorian Date: " + gregorianDate + "\n");
		buf.append("Is Leap Year: " + isGregorianLeap + "\n");
		buf.append("Day of Year: " + dayOfYear + "\n");
		buf.append("Day of Week: " + dayOfWeek + "\n");
		buf.append("Chinese Year: " + chineseYear + "\n");
		buf.append("Heavenly Stem: " + ((chineseYear - 1) % 10) + "\n");
		buf.append("Earthly Branch: " + ((chineseYear - 1) % 12) + "\n");
		buf.append("Chinese Month: " + chineseMonth + "\n");
		buf.append("Chinese Date: " + chineseDate + "\n");
		buf.append("Sectional Term: " + sectionalTerm + "\n");
		buf.append("Principle Term: " + principleTerm + "\n");
		return buf.toString();
	}

	public String cDay(int day) {
		String str;
		switch (day) {
		case 10:
			str = chuShiStr;
			break;
		case 20:
			str = erShiStr;
			break;
		case 30:
			str = sanShiStr;
			break;
		default:
			str = nStr2[(int) java.lang.Math.floor(day / 10)]
					+ nStr1[(day % 10)];
			break;
		}
		return str;
	}

	public String getLunarDate() {
		String str = "";
		if (chineseMonth > 0) {
			str = chineseMonthNames[chineseMonth - 1] + yueStr;
		} else if (chineseMonth < 0) {
			str = runStr + chineseMonthNames[-chineseMonth - 1] + yueStr;
		} else {
			;
		}
		
		str += cDay(chineseDate);
		
//		if (gregorianDate == sectionalTerm) {
//			str += " " + sectionalTermNames[gregorianMonth - 1];
//		} else if (gregorianDate == principleTerm) {
//			str +=  " " + principleTermNames[gregorianMonth - 1];
//		} else {
//			;
//		} 

		return str;
	}

	public String getDateString() {
		String str = "";
		String gm = String.valueOf(gregorianMonth);
		if (gm.length() == 1)
			gm = ' ' + gm;
		String cm = String.valueOf(Math.abs(chineseMonth));
		if (cm.length() == 1)
			cm = ' ' + cm;
		String gd = String.valueOf(gregorianDate);
		if (gd.length() == 1)
			gd = ' ' + gd;
		String cd = String.valueOf(chineseDate);
		if (cd.length() == 1)
			cd = ' ' + cd;
		if (gregorianDate == sectionalTerm) {
			str = " " + sectionalTermNames[gregorianMonth - 1];
		} else if (gregorianDate == principleTerm) {
			str = " " + principleTermNames[gregorianMonth - 1];
		} else if (chineseDate == 1 && chineseMonth > 0) {
			str = " " + chineseMonthNames[chineseMonth - 1] + yueStr;
		} else if (chineseDate == 1 && chineseMonth < 0) {
			str = "*" + chineseMonthNames[-chineseMonth - 1] + yueStr;
		} else {
			str = gd + '/' + cd;
		}
		return str;
	}

	public int rollUpOneDay() {
		dayOfWeek = dayOfWeek % 7 + 1;
		dayOfYear++;
		gregorianDate++;
		int days = daysInGregorianMonth(gregorianYear, gregorianMonth);
		if (gregorianDate > days) {
			gregorianDate = 1;
			gregorianMonth++;
			if (gregorianMonth > 12) {
				gregorianMonth = 1;
				gregorianYear++;
				dayOfYear = 1;
				isGregorianLeap = isGregorianLeapYear(gregorianYear);
			}
			sectionalTerm = sectionalTerm(gregorianYear, gregorianMonth);
			principleTerm = principleTerm(gregorianYear, gregorianMonth);
		}
		chineseDate++;
		days = daysInChineseMonth(chineseYear, chineseMonth);
		if (chineseDate > days) {
			chineseDate = 1;
			chineseMonth = nextChineseMonth(chineseYear, chineseMonth);
			if (chineseMonth == 1)
				chineseYear++;
		}
		return 0;
	}
}
