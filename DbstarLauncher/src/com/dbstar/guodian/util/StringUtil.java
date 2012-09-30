package com.dbstar.guodian.util;

import android.util.Log;

public class StringUtil {

	public static final int DemicalLength = 3;
	
	public static final int UNIT_B = 0;
	public static final int UNIT_K = 1;
	public static final int UNIT_M = 2;
	public static final int UNIT_G = 3;
	public static final int UNIT_T = 4;

	public static final int UNITSIZE_K = 1024;
	public static final int UNITSIZE_M = 1048576;
	public static final int UNITSIZE_G = 1073741824;
	// public static final long UNITSIZE_T = 1099511627776;

	public static class SizePair {
		public float Value;
		public int Unit;
	}

	public static String getUnitString(int unit) {
		String unitStr = "";
		switch (unit) {
		case UNIT_K:
			unitStr = "K";
			break;
		case UNIT_M:
			unitStr = "M";
			break;
		case UNIT_G:
			unitStr = "G";
			break;
		default:
			break;
		}

		return unitStr;
	}
	
	public static SizePair formatSize(long size) {
		SizePair pair = new SizePair();
		
		if (size < UNITSIZE_M) {
			pair.Unit = UNIT_K;
			pair.Value = (float)size / UNITSIZE_K;
		} else if (size < UNITSIZE_G) {
			pair.Unit = UNIT_M;
			pair.Value = (float)size / UNITSIZE_M;
		} else {
			pair.Unit = UNIT_G;
			pair.Value = (float)size / UNITSIZE_G;
		}
		
		return pair;
	}
	
	static public String formatFloatValue(float value) {

		if(value <= 0.0f) {
			return "0";
		}
		
		// TODO: handle the case as: 5.232E-4 ???

		//Float d = new Float(value);
		//String str = d.toString();
		String str = String.valueOf(value);

		Log.d("", " formatFloatValue" + str);
		int index = str.indexOf('.');
		
		if (index < 0) {
			return str;
		}
		
		String valueStr;

		String demicalStr = str.substring(index + 1);
		if (demicalStr.length() > DemicalLength ) {
			valueStr = new String(str.substring(0, index + DemicalLength));
		} else {
			valueStr = str;
		}

		return valueStr;
	}
	
}
