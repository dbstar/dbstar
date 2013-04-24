package com.dbstar.guodian.parse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.sax.StartElementListener;

import com.dbstar.R;
import com.dbstar.guodian.data.ElectricityPrice;

public class Util {
	public static ElectricityPrice.StepPrice getStep(
			List<ElectricityPrice.StepPrice> stepPriceList, String monthPower) {
	    float powerValue = getFloatFromString(monthPower);
		for (ElectricityPrice.StepPrice step : stepPriceList) {
			String start = step.StepStartValue;
			String end = step.StepEndValue;
			float startValue =getFloatFromString(start);
			float endValue = getFloatFromString(end);
			if (powerValue > startValue && powerValue <= endValue) {
				return step;
			}
		}

		return null;
	}

	public static ElectricityPrice.PeriodPrice getPeriod(
			List<ElectricityPrice.PeriodPrice> periodList, String periodType) {
		for (ElectricityPrice.PeriodPrice period : periodList) {
			return null;
		}

		return null;
	}

	public static String getStepStr(Context context, String step) {
		Resources res = context.getResources();
		if (step.equals(ElectricityPrice.STEP_1)) {
			return res.getString(R.string.step_1);
		} else if (step.equals(ElectricityPrice.STEP_2)) {
			return res.getString(R.string.step_2);
		} else if (step.equals(ElectricityPrice.STEP_3)) {
			return res.getString(R.string.step_3);
		} else {
			return "";
		}
	}

	public static String getPeriodStr(Context context, String period) {
		Resources res = context.getResources();
		if (period.equals(ElectricityPrice.PERIOD_1)) {
			return res.getString(R.string.period_1);
		} else if (period.equals(ElectricityPrice.PERIOD_2)) {
			return res.getString(R.string.period_2);
		} else if (period.equals(ElectricityPrice.PERIOD_3)) {
			return res.getString(R.string.period_3);
		} else {
			return "";
		}
	}
	
	public static String getPeriodTimeString(String period) {
		String[] time = period.split("-");
		StringBuffer sb = new StringBuffer();
		sb.append(time[0].substring(0, time[0].length() - 3));
		sb.append("-");
		sb.append(time[1].substring(0, time[1].length() - 3));
		return sb.toString();
	}

	public static float[] getSweep(String period) {
		float[] sweep = new float[2];
		String[] time = period.split("-");
		StringBuffer sb = new StringBuffer();
		float sh = getFloatFromString(time[0].substring(0, 2));
		float sm = getFloatFromString(time[0].substring(3, 5));
		float eh = getFloatFromString(time[1].substring(0, 2));
		float em = getFloatFromString(time[1].substring(3, 5));
		float rate = (float) 180 / (24 * 60);
		sweep[0] = 180 + (float) ((((sh * 60) + sm) * rate));
		sweep[1] = 180 + (float) ((((eh * 60) + em) * rate));
		sweep[1] = sweep[1] - sweep[0];
		return sweep;
	}
	
	public static float getFloatFromString(String value){
	    float f = 0;
	    if(value == null || value.isEmpty()){
	        return 0;
	    }else{
	        try {
                f= Float.valueOf(value.trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
	    }
	    return f;
	}
}
