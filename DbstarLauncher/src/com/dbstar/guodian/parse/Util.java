package com.dbstar.guodian.parse;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;

import com.dbstar.R;
import com.dbstar.guodian.data.ElectricityPrice;

public class Util {
	public static ElectricityPrice.StepPrice getStep(
			List<ElectricityPrice.StepPrice> stepPriceList, String monthPower) {
		for (ElectricityPrice.StepPrice step : stepPriceList) {
			String start = step.StepStartValue;
			String end = step.StepEndValue;
			float startValue = Float.valueOf(start);
			float endValue = Float.valueOf(end);
			float powerValue = Float.valueOf(monthPower);

			if (powerValue > startValue && powerValue < endValue) {
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
}
