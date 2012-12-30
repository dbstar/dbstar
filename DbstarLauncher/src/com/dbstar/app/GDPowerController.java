package com.dbstar.app;

import java.util.List;

import com.dbstar.R;
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.guodian.data.UserPriceStatus;
import com.dbstar.service.GDDataProviderService;
import com.dbstar.widget.GDArcView;
import com.dbstar.widget.GDCircleTextView;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GDPowerController {

	// message from engine
	public static final int PriceTypeSingle = 0;
	public static final int PriceTypeStep = 1;
	public static final int PriceTypeStepPlusTiming = 2;
	public static final int PriceTypeTiming = 3;

	private static final float StepRulerStep1Angle = 50;
	private static final float StepRulerStep2Angle = 126;

	Activity mActivity;
	GDDataProviderService mService;

	// Power View
	TextView mPowerUsedDegreeView, mPowerUsedCostView;

	ViewGroup mStepPowerPanel, mTimingPowerPanel;

	int mPriceType = -1;

	// Step Power
	ImageView mStepPowerPointer;
	TextView mStepPowerStepView;
	TextView mStepPowerPriceView;
	TextView mStepPowerRulerStep1, mStepPowerRulerStep2;

	// Timing power
	ImageView mTimingPowerPointer;
	GDCircleTextView mTimingPowerStepView;
	TextView mTimingPowerPriceView;
	TextView mTimingPowerRulerStep1, mTimingPowerRulerStep2;
	TextView mTimePowerPeriodView, mTimePowerPeriodTimeView;
	GDArcView mTimingPowerPeriodPointer;

	String mPowerUsageStr, mPowerCostStr;
	String Yuan, Degree;

	public GDPowerController(Activity activity) {
		mActivity = activity;

		Yuan = activity.getResources().getString(R.string.string_yuan);
		Degree = activity.getResources().getString(R.string.string_degree);
		mPowerUsageStr = activity.getResources().getString(
				R.string.mypower_powerusage);
		mPowerCostStr = activity.getResources().getString(
				R.string.mypower_powercost);
		// Power View
		mPowerUsedDegreeView = (TextView) activity
				.findViewById(R.id.mypower_degree);
		mPowerUsedCostView = (TextView) activity
				.findViewById(R.id.mypower_cost);

		mStepPowerPanel = (ViewGroup) activity
				.findViewById(R.id.timingpower_panel);
		mTimingPowerPanel = (ViewGroup) activity
				.findViewById(R.id.steppower_panel);

		// step power
		mStepPowerPointer = (ImageView) activity
				.findViewById(R.id.steppower_pointer);
		mStepPowerStepView = (TextView) activity
				.findViewById(R.id.steppower_step);
		mStepPowerPriceView = (TextView) activity
				.findViewById(R.id.steppower_powerprice);
		mStepPowerRulerStep1 = (TextView) activity
				.findViewById(R.id.steppower_ruler_step1);
		mStepPowerRulerStep2 = (TextView) activity
				.findViewById(R.id.steppower_ruler_step2);

		// timing power
		mTimingPowerPointer = (ImageView) activity
				.findViewById(R.id.timingpower_pointer);
		mTimingPowerStepView = (GDCircleTextView) activity
				.findViewById(R.id.timingppower_step);
		mTimingPowerPriceView = (TextView) activity
				.findViewById(R.id.timingpower_powerprice);
		mTimingPowerRulerStep1 = (TextView) activity
				.findViewById(R.id.timingpower_ruler_step1);
		mTimingPowerRulerStep2 = (TextView) activity
				.findViewById(R.id.timingpower_ruler_step2);
		mTimePowerPeriodView = (TextView) activity
				.findViewById(R.id.timingpower_period);
		mTimePowerPeriodTimeView = (TextView) activity
				.findViewById(R.id.timingpower_time);
		mTimingPowerPeriodPointer = (GDArcView) activity
				.findViewById(R.id.timingpower_periodpointer);
	}

	public void start(GDDataProviderService service) {
		mService = service;
	}

	public void updatePowerPanel(PowerPanelData data) {
		String powerNum = data.MonthPower.Count;
		String powerFee = data.MonthPower.Fee;
		mPowerUsedDegreeView.setText(mPowerUsageStr + powerNum + Degree);
		mPowerUsedCostView.setText(mPowerCostStr + powerFee + Yuan);

		UserPriceStatus status = data.PriceStatus;

		if (status.equals(ElectricityPrice.PRICETYPE_STEP)) {
			mPriceType = PriceTypeStep;
			mStepPowerPanel.setVisibility(View.VISIBLE);
			mTimingPowerPanel.setVisibility(View.GONE);
		} else if (status.equals(ElectricityPrice.PRICETYPE_STEPPLUSTIMING)) {
			mPriceType = PriceTypeStepPlusTiming;

			mStepPowerPanel.setVisibility(View.GONE);
			mTimingPowerPanel.setVisibility(View.VISIBLE);
		}

		ElectricityPrice priceData = mService.getPowerPriceData();

		if (priceData == null)
			return;

		if (mPriceType == PriceTypeStep) {

			mStepPowerStepView.setText(status.Step);
			mStepPowerPriceView.setText(status.Price);

			List<ElectricityPrice.StepPrice> stepPriceList = priceData.StepPriceList;
			if (stepPriceList == null)
				return;

			ElectricityPrice.StepPrice currentStep = getStep(stepPriceList,
					powerNum);
			for (ElectricityPrice.StepPrice stepPrice : stepPriceList) {
				if (stepPrice.Step.equals(ElectricityPrice.STEP_1)) {
					mStepPowerRulerStep1.setText(stepPrice.StepEndValue);
				} else if (stepPrice.Step.equals(ElectricityPrice.STEP_2)) {
					mStepPowerRulerStep2.setText(stepPrice.StepEndValue);
				}
			}

			if (currentStep.Step.equals(ElectricityPrice.STEP_1)) {
				String stepEnd = currentStep.StepEndValue;
				float endValue = Float.valueOf(stepEnd);
				if (endValue != 0) {
					float powerValue = Float.valueOf(powerNum);
					float angle = StepRulerStep1Angle * (powerValue / endValue);
					mStepPowerPointer.setRotation(angle);
				}
			} else if (currentStep.Step.equals(ElectricityPrice.STEP_2)) {
				float endValue = Float.valueOf(currentStep.StepEndValue);
				float startValue = Float.valueOf(currentStep.StepStartValue);
				if (endValue != startValue) {
					float powerValue = Float.valueOf(powerNum);
					float angle = StepRulerStep1Angle + (StepRulerStep2Angle - StepRulerStep1Angle)
							* (powerValue / (endValue - startValue));
					mStepPowerPointer.setRotation(angle);
				}
			} else if (currentStep.Step.equals(ElectricityPrice.STEP_3)) {
				float angle = StepRulerStep2Angle + 20;
				mStepPowerPointer.setRotation(angle);
			}

		} else if (mPriceType == PriceTypeStepPlusTiming) {
			mTimingPowerStepView.setText(status.Step);
			mTimingPowerPriceView.setText(status.Price);
		}

	}

	ElectricityPrice.StepPrice getStep(
			List<ElectricityPrice.StepPrice> stepPriceList, String monthPower) {
		for (ElectricityPrice.StepPrice step : stepPriceList) {
			String start = step.StepStartValue;
			String end = step.StepEndValue;
			int startValue = Integer.valueOf(start);
			int endValue = Integer.valueOf(end);
			int powerValue = Integer.valueOf(monthPower);

			if (powerValue > startValue && powerValue < endValue) {
				return step;
			}
		}

		return null;
	}

}
