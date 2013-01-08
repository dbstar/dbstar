package com.dbstar.app;

import java.util.List;

import com.dbstar.R;
import com.dbstar.guodian.GDConstract;
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.guodian.data.UserPriceStatus;
import com.dbstar.service.GDDataProviderService;
import com.dbstar.widget.GDArcView;
import com.dbstar.widget.GDCircleTextView;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GDPowerController {
	private static final String TAG = "GDPowerController";

	private static final int MSG_GETPOWER = 0xef1;
	private static final int SCHEDULE_INTERVAL = 60000;
	
	// message from engine
	public static final int PriceTypeSingle = 0;
	public static final int PriceTypeStep = 1;
	public static final int PriceTypeStepPlusTiming = 2;
	public static final int PriceTypeTiming = 3;

	private static final float StepRulerStep1Angle = 50;
	private static final float StepRulerStep2Angle = 126;
	
	private static final float TimingRulerStep1Angle = 40;
	private static final float TimingRulerStep2Angle = 140;

	Activity mActivity;
	GDDataProviderService mService;
	
	LoginData mLoginData;
	boolean mIsLogined = false;

	// Power View
	TextView mPowerUsedDegreeView, mPowerUsedCostView;

	ViewGroup mStepPowerPanel, mTimingPowerPanel;

	int mPriceType = -1;

	// Step Power
	ImageView mStepPowerPointer;
	TextView mStepPowerStepView;
	TextView mStepPowerPriceView;
	TextView mStepPowerRulerStep0, mStepPowerRulerStep1, mStepPowerRulerStep2;

	// Timing power
	ImageView mTimingPowerPointer;
	GDCircleTextView mTimingPowerStepView;
	TextView mTimingPowerPriceView;
	TextView mTimingPowerRulerStep0, mTimingPowerRulerStep1, mTimingPowerRulerStep2;
	TextView mTimePowerPeriodView, mTimePowerPeriodTimeView;
	GDArcView mTimingPowerPeriodPointer;

	String mPowerUsageStr, mPowerCostStr;
	String Yuan, Degree;
	
	private Handler mHandler = null;

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
				.findViewById(R.id.steppower_panel);
		mTimingPowerPanel = (ViewGroup) activity
				.findViewById(R.id.timingpower_panel);

		// step power
		mStepPowerPointer = (ImageView) activity
				.findViewById(R.id.steppower_pointer);
		mStepPowerStepView = (TextView) activity
				.findViewById(R.id.steppower_step);
		mStepPowerPriceView = (TextView) activity
				.findViewById(R.id.steppower_powerprice);
		
		mStepPowerRulerStep0 = (TextView) activity
				.findViewById(R.id.steppower_ruler_step0);
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
		
		mTimingPowerRulerStep0 = (TextView) activity
				.findViewById(R.id.timingpower_ruler_step0);
		
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
		
		
		mPowerUsedDegreeView.setText(mPowerUsageStr + " 0 " + Degree);
		mPowerUsedCostView.setText(mPowerCostStr + " 0 " + Yuan);
		mStepPowerPointer.setRotation(0);
		mStepPowerStepView.setText("");
		mStepPowerPriceView.setText("");
		mStepPowerRulerStep0.setText("");
		mStepPowerRulerStep1.setText("");
		mStepPowerRulerStep2.setText("");
		
		mTimingPowerPointer.setRotation(0);
		mTimingPowerStepView.setText("");
		mTimingPowerPriceView.setText("");
		mTimingPowerRulerStep0.setText("");
		mTimingPowerRulerStep1.setText("");
		mTimingPowerRulerStep2.setText("");
		mTimePowerPeriodView.setText("");
		mTimePowerPeriodTimeView.setText("");
		mTimingPowerPeriodPointer.setRotation(0);
		
		
		mHandler = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_GETPOWER: {
					getPowerData();
					break;
				}
				}
			}
		};
	}

	public void start(GDDataProviderService service) {
		mService = service;
	}
	
	public void stop() {
		mHandler.removeMessages(MSG_GETPOWER);
	}
	
	public void getPowerData() {
		if (mService != null) {
			mService.requestPowerData(GDConstract.DATATYPE_POWERPANELDATA);
		}
		
		mHandler.sendEmptyMessageDelayed(MSG_GETPOWER, SCHEDULE_INTERVAL);
	}

	public void handleLogin(LoginData data) {
		mLoginData = data;
		if (mLoginData != null) {
			mIsLogined = true;
			updatePowerPanel(mLoginData.PanelData);
		}
		
		getPowerData();
	}
	
	public void updatePowerPanel(PowerPanelData data) {
		
		Log.d(TAG, " ===== updatePowerPanel ===== ");
		
		if (data == null)
			return;
		
		if (data.MonthPower == null)
			return;
		
		String powerNum = data.MonthPower.Count;
		String powerFee = data.MonthPower.Fee;
		float powerNumValue = Float.valueOf(powerNum);
		float powerFeeValue = Float.valueOf(powerFee);
		
		mPowerUsedDegreeView.setText(mPowerUsageStr + " " + powerNum + " " + Degree);
		mPowerUsedCostView.setText(mPowerCostStr + " "  + powerFee + " " + Yuan);

		UserPriceStatus status = data.PriceStatus;
		
		if (status == null)
			return;
		
		if (status.PriceType == null) {
			return;
		}
		
		String priceType = status.PriceType;
		
		Log.d(TAG, " ===== PriceType ===== " + priceType );
		
		if (priceType.equals(ElectricityPrice.PRICETYPE_STEP)) {
			mPriceType = PriceTypeStep;
			mStepPowerPanel.setVisibility(View.VISIBLE);
			mTimingPowerPanel.setVisibility(View.GONE);
		} else if (priceType.equals(ElectricityPrice.PRICETYPE_STEPPLUSTIMING)) {
			mPriceType = PriceTypeStepPlusTiming;

			mStepPowerPanel.setVisibility(View.GONE);
			mTimingPowerPanel.setVisibility(View.VISIBLE);
		} else {
			return;
		}

		ElectricityPrice priceData = mLoginData.ElecPrice;

		if (priceData == null)
			return;

		if (mPriceType == PriceTypeStep) {

			mStepPowerStepView.setText(getStepStr(status.Step));
			mStepPowerPriceView.setText(status.Price);

			List<ElectricityPrice.StepPrice> stepPriceList = priceData.StepPriceList;
			if (stepPriceList == null)
				return;

			for (ElectricityPrice.StepPrice stepPrice : stepPriceList) {
				
				Log.d(TAG, "step " + stepPrice.Step);
				Log.d(TAG, "step start " + stepPrice.StepStartValue);
				Log.d(TAG, "step end " + stepPrice.StepEndValue);
				Log.d(TAG, "step price " + stepPrice.StepPrice);
				Log.d(TAG, "step period " + stepPrice.PeriodPriceList);
				
				if (stepPrice.Step.equals(ElectricityPrice.STEP_1)) {
					mStepPowerRulerStep0.setText(stepPrice.StepStartValue);
					mStepPowerRulerStep1.setText(stepPrice.StepEndValue);
				} else if (stepPrice.Step.equals(ElectricityPrice.STEP_2)) {
					mStepPowerRulerStep2.setText(stepPrice.StepEndValue);
				}
			}

			if (powerNumValue == 0) {
				mStepPowerPointer.setRotation(0);
				return;
			}

			ElectricityPrice.StepPrice currentStep = getStep(stepPriceList,
					powerNum);
			
			Log.d(TAG, "current step " + currentStep);

			if (currentStep == null) {
				return;
			}
			
			Log.d(TAG, "current step " + currentStep);

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
			mTimingPowerStepView.setText(getStepStr(status.Step));
			mTimingPowerPriceView.setText(status.Price);
			
			mTimePowerPeriodView.setText(status.CurrentPeriodType);
			mTimePowerPeriodTimeView.setText(status.Period);

			List<ElectricityPrice.StepPrice> stepPriceList = priceData.StepPriceList;
			if (stepPriceList == null)
				return;

			for (ElectricityPrice.StepPrice stepPrice : stepPriceList) {
				if (stepPrice.Step.equals(ElectricityPrice.STEP_1)) {
					mTimingPowerRulerStep0.setText(stepPrice.StepStartValue);
					mTimingPowerRulerStep1.setText(stepPrice.StepEndValue);
				} else if (stepPrice.Step.equals(ElectricityPrice.STEP_2)) {
					mTimingPowerRulerStep2.setText(stepPrice.StepEndValue);
				}
			}

			if (powerNumValue == 0) {
				mStepPowerPointer.setRotation(0);
				return;
			}

			ElectricityPrice.StepPrice currentStep = getStep(stepPriceList,
					powerNum);
			
			if (currentStep == null)
				return;

			if (currentStep.Step.equals(ElectricityPrice.STEP_1)) {
				String stepEnd = currentStep.StepEndValue;
				float endValue = Float.valueOf(stepEnd);
				if (endValue != 0) {
					float powerValue = Float.valueOf(powerNum);
					float angle = TimingRulerStep1Angle * (powerValue / endValue);
					mTimingPowerPointer.setRotation(angle);
				}
			} else if (currentStep.Step.equals(ElectricityPrice.STEP_2)) {
				float endValue = Float.valueOf(currentStep.StepEndValue);
				float startValue = Float.valueOf(currentStep.StepStartValue);
				if (endValue != startValue) {
					float powerValue = Float.valueOf(powerNum);
					float angle = TimingRulerStep1Angle + (TimingRulerStep2Angle - TimingRulerStep1Angle)
							* (powerValue / (endValue - startValue));
					mTimingPowerPointer.setRotation(angle);
				}
			} else if (currentStep.Step.equals(ElectricityPrice.STEP_3)) {
				float angle = TimingRulerStep2Angle + 20;
				mTimingPowerPointer.setRotation(angle);
			}
			
		}

	}

	ElectricityPrice.StepPrice getStep(
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

	ElectricityPrice.PeriodPrice getPeriod(
			List<ElectricityPrice.PeriodPrice> periodList, String periodType) {
		for(ElectricityPrice.PeriodPrice  period : periodList) {
			return null;
		}
		
		return null;
	}
	
	String getStepStr(String step) {
		Resources res = mActivity.getResources();
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
}
