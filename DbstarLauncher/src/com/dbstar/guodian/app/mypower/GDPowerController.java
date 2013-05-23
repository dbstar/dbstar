package com.dbstar.guodian.app.mypower;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbstar.R;
import com.dbstar.guodian.app.familyefficency.GDPowerConstitueActivity;
import com.dbstar.guodian.app.familyefficency.GDPowerConsumptionTrackActivity;
import com.dbstar.guodian.app.familyefficency.GDPowerConsumptionTrendActivity;
import com.dbstar.guodian.app.familyefficency.GDPowerTipsActivity;
import com.dbstar.guodian.app.smarthome.GDSmartHomeModeActivity;
import com.dbstar.guodian.app.smarthome.GDSmartHomeMyEleActivity;
import com.dbstar.guodian.app.smarthome.GDSmartHomeTimedTaskActivity;
import com.dbstar.guodian.data.EPCConstitute;
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.guodian.data.UserPriceStatus;
import com.dbstar.guodian.engine.GDConstract;
import com.dbstar.guodian.parse.Util;
import com.dbstar.model.GDCommon;
import com.dbstar.service.GDDataProviderService;
import com.dbstar.util.DateUtil;
import com.dbstar.widget.GDArcView;
import com.dbstar.widget.GDCircleTextView;

import android.app.Activity;
import android.content.Intent;
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
	private static final int SCHEDULE_INTERVAL = 3600000; // 1 hours

	private static final float StepRulerStep1Angle = 50;
	private static final float StepRulerStep2Angle = 126;

	private static final float TimingRulerStep1Angle = 40;
	private static final float TimingRulerStep2Angle = 140;

	private Activity mActivity;
	private GDDataProviderService mService;

	private LoginData mLoginData;
	private boolean mIsLogined = false;

	// Power View
	private TextView mPowerUsedDegreeView, mPowerUsedCostView;

	private ViewGroup mStepPowerPanel, mTimingPowerPanel;

	private int mPriceType = -1;

	// Step Power
	private ImageView mStepPowerPointer;
	private TextView mStepPowerStepView;
	private TextView mStepPowerPriceView;
	private TextView mStepPowerRulerStep0, mStepPowerRulerStep1,
			mStepPowerRulerStep2;

	// Timing power
	private ImageView mTimingPowerPointer;
	private GDCircleTextView mTimingPowerStepView;
	private TextView mTimingPowerPriceView;
	private TextView mTimingPowerRulerStep0, mTimingPowerRulerStep1,
			mTimingPowerRulerStep2;
	private TextView mTimePowerPeriodView, mTimePowerPeriodTimeView;
	private GDArcView mTimingPowerPeriodPointer;

	private String mMonthUsageStr, mMonthCostStr, mYearUsageStr, mYearCostStr;
	private String Yuan, Degree;
	private String mCycleType;
	private Handler mHandler = null;

	public GDPowerController(Activity activity) {
		mActivity = activity;

		Yuan = activity.getResources().getString(R.string.string_yuan);
		Degree = activity.getResources().getString(R.string.string_degree);
		mMonthUsageStr = activity.getResources().getString(
				R.string.mypower_monthpowerusage);
		mMonthCostStr = activity.getResources().getString(
				R.string.mypower_monthpowercost);

		mYearUsageStr = activity.getResources().getString(
				R.string.mypower_yearpowerusage);
		mYearCostStr = activity.getResources().getString(
				R.string.mypower_yearpowercost);

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

		mPowerUsedDegreeView.setText(mMonthUsageStr + " 0 " + Degree);
		mPowerUsedCostView.setText(mMonthCostStr + " 0 " + Yuan);
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

	public void resume() {
		if (mIsLogined) {
			getPowerData();
		}
	}

	public void pause() {
		mHandler.removeMessages(MSG_GETPOWER);
	}

	public void stop() {
		mHandler.removeMessages(MSG_GETPOWER);
	}

	public void getPowerData() {
		if (mService != null) {
			mService.requestPowerData(GDConstract.DATATYPE_POWERPANELDATA, null);
			requestEPCConstitute();
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
	
	private void requestEPCConstitute(){
        LoginData loginData = mLoginData;
        if(loginData == null)
            return ;
        String date_type = "month";
        Date date = new Date(System.currentTimeMillis());
        String start = DateUtil.getStringFromDate(date, DateUtil.DateFormat2);
        if(loginData.PanelData != null && loginData.PanelData.PriceStatus != null){
            if(ElectricityPrice.CYCLETYPE_YEAR.equals(loginData.PanelData.PriceStatus.CycleType)){
                date_type = "year";
                start = DateUtil.getStringFromDate(date, DateUtil.DateFormat3);
            }
        }
        String end = DateUtil.getStringFromDate(date, DateUtil.DateFormat1);
        
        String  userType =loginData.UserData.UserType;
        String ccguid = loginData.CtrlNo.CtrlNoGuid;
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonTag.TAGNumCCGuid, ccguid);
        params.put(JsonTag.TAGDateStart, start);
        params.put(JsonTag.TAGDateEnd, end);
        params.put(JsonTag.TAGDateType, date_type);
        params.put(JsonTag.TAGUser_Type, userType);
        mService.requestPowerData(GDConstract.DATATYPE_ELECTRICAL_POWER_CONSUMPTION_CONSTITUTE, params);
    }
	public void updateElectriDimension(EPCConstitute dimension){
	        if(dimension == null || dimension.totalPower == null)
	            return ;
	        if(dimension.totalPower.Count == null || dimension.totalPower.Fee == null)
	            return;
	        String powerNumStr = "", powerFeeStr = "";
	        if(ElectricityPrice.CYCLETYPE_YEAR.equals(mCycleType)){
	            powerNumStr = mYearUsageStr + dimension.totalPower.Count + " " + Degree;
                powerFeeStr = mYearCostStr + dimension.totalPower.Fee + " " + Yuan;
	        }else{
	            powerNumStr = mMonthUsageStr + dimension.totalPower.Count + " " + Degree;
                powerFeeStr = mMonthCostStr + dimension.totalPower.Fee + " " + Yuan;
	        }
	        mPowerUsedDegreeView.setText(powerNumStr);
	        mPowerUsedCostView.setText(powerFeeStr);
	}
	public void updatePowerPanel(PowerPanelData data) {

		Log.d(TAG, " ===== updatePowerPanel ===== ");

		if (data == null)
			return;

		if (data.MonthPower == null)
			return;

		String powerNum = null;// data.MonthPower.Count;
		String powerFee = null;// data.MonthPower.Fee;
		float powerNumValue = 0; // Float.valueOf(powerNum);
		float powerFeeValue = 0;// Float.valueOf(powerFee);

		UserPriceStatus status = data.PriceStatus;

		if (status == null)
			return;

		if (status.PriceType == null) {
			return;
		}

		//String powerNumStr = "", powerFeeStr = "";
		if (status.CycleType != null) {
		 if (status.CycleType.equals(ElectricityPrice.CYCLETYPE_YEAR)) {
		         mCycleType = ElectricityPrice.CYCLETYPE_YEAR;
			    if(data.YearPower != null){
    				powerNum = data.YearPower.Count;
    				powerFee = data.YearPower.Fee;
    				if(powerNum != null && powerFee != null){
//        				powerNumStr = mYearUsageStr + powerNum + " " + Degree;
//        				powerFeeStr = mYearCostStr + powerFee + " " + Yuan;
        
        				powerNumValue = Util.getFloatFromString(powerNum);
                        powerFeeValue = Util.getFloatFromString(powerFee);
    				}
			}
		}else{
		    mCycleType = ElectricityPrice.CYCLETYPE_MONTH;
		    if(data.MonthPower != null){
                powerNum = data.MonthPower.Count;
                powerFee = data.MonthPower.Fee;
                if(powerNum != null && powerFee != null){
//                    powerNumStr = mMonthUsageStr + powerNum + " " + Degree;
//                    powerFeeStr = mMonthCostStr + powerFee + " " + Yuan;
    
                    powerNumValue = Util.getFloatFromString(powerNum);
                    powerFeeValue = Util.getFloatFromString(powerFee);
                }
            }
		}
		
//		mPowerUsedDegreeView.setText(powerNumStr);
//		mPowerUsedCostView.setText(powerFeeStr);

		String priceType = status.PriceType;

		Log.d(TAG, " ===== PriceType ===== " + priceType);

		if (ElectricityPrice.PRICETYPE_STEP.equals(priceType)) {
			mPriceType = GDConstract.PriceTypeStep;
			mStepPowerPanel.setVisibility(View.VISIBLE);
			mTimingPowerPanel.setVisibility(View.GONE);
		} else if (ElectricityPrice.PRICETYPE_STEPPLUSTIMING.equals(priceType)) {
			mPriceType = GDConstract.PriceTypeStepPlusTiming;
			mStepPowerPanel.setVisibility(View.GONE);
			mTimingPowerPanel.setVisibility(View.VISIBLE);
		} else if(ElectricityPrice.PRICETYPE_SINGLE.equals(priceType)){
		    mPriceType = GDConstract.PriceTypeSingle;
		    mStepPowerPanel.setVisibility(View.VISIBLE);
            mTimingPowerPanel.setVisibility(View.GONE); 
		}else if(ElectricityPrice.PRICETYPE_TIMING.equals(priceType)){
		    mPriceType = GDConstract.PriceTypeTiming;
		    mStepPowerPanel.setVisibility(View.GONE);
            mTimingPowerPanel.setVisibility(View.VISIBLE);
		}else{
			return;
		}

		ElectricityPrice priceData = mLoginData.ElecPrice;

		if (priceData == null)
			return;

		if (mPriceType == GDConstract.PriceTypeStep ) {

			mStepPowerStepView.setText(Util.getStepStr(mActivity, status.Step));
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

			ElectricityPrice.StepPrice currentStep = Util.getStep(
					stepPriceList, powerNum);

			Log.d(TAG, "current step " + currentStep);

			if (currentStep == null) {
				return;
			}

			Log.d(TAG, "current step " + currentStep);

			if (currentStep.Step.equals(ElectricityPrice.STEP_1)) {
				String stepEnd = currentStep.StepEndValue;
				float endValue = Util.getFloatFromString(stepEnd);
				if (endValue != 0) {
					float angle = StepRulerStep1Angle * (powerNumValue / endValue);
					mStepPowerPointer.setRotation(angle);
				}
			} else if (currentStep.Step.equals(ElectricityPrice.STEP_2)) {
				float endValue = Util.getFloatFromString(currentStep.StepEndValue);
				float startValue = Util.getFloatFromString(currentStep.StepStartValue);
				if (endValue != startValue) {
					float angle = StepRulerStep1Angle
							+ (StepRulerStep2Angle - StepRulerStep1Angle)
							* (powerNumValue - startValue) / (endValue - startValue);
					mStepPowerPointer.setRotation(angle);
				}
			} else if (currentStep.Step.equals(ElectricityPrice.STEP_3)) {
				float startValue =  Util.getFloatFromString(currentStep.StepStartValue);
				if (powerNumValue > 0) {
					float angle = (StepRulerStep2Angle - 180) * startValue / powerNumValue  + 180;
					mStepPowerPointer.setRotation(angle);
				}
			}

		} else if(mPriceType == GDConstract.PriceTypeSingle){
		    mStepPowerStepView.setText(R.string.powerprice_type_single);
            mStepPowerPriceView.setText(priceData.SinglePrice);
		    
		}else if (mPriceType == GDConstract.PriceTypeStepPlusTiming 
		        || mPriceType == GDConstract.PriceTypeTiming ) {
		    
		    if (mPriceType == GDConstract.PriceTypeTiming) {
	            mTimingPowerStepView.setText(mActivity.getResources().getString(R.string.powerprice_type_timing));
	        }else if(mPriceType == GDConstract.PriceTypeStepPlusTiming ){
	            mTimingPowerStepView.setText(Util.getStepStr(mActivity, status.Step));
	        } 
			mTimingPowerPriceView.setText(status.Price);
			
			mTimePowerPeriodView.setText(Util.getPeriodStr(mActivity, status.CurrentPeriodType));
			mTimePowerPeriodTimeView.setText(Util.getPeriodTimeString(status.Period));
            float sweep [] = Util.getSweep(status.Period);
            mTimingPowerPeriodPointer.setSweepAngle(sweep[0], sweep[1]);

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

			ElectricityPrice.StepPrice currentStep = Util.getStep(
					stepPriceList, powerNum);

			if (currentStep == null)
				return;

			if (currentStep.Step.equals(ElectricityPrice.STEP_1)) {
				String stepEnd = currentStep.StepEndValue;
				float endValue = Util.getFloatFromString(stepEnd) ;
				if (endValue != 0) {
					float angle = TimingRulerStep1Angle
							* (powerNumValue / endValue);
					mTimingPowerPointer.setRotation(angle);
				}
			} else if (currentStep.Step.equals(ElectricityPrice.STEP_2)) {
				float endValue = Util.getFloatFromString(currentStep.StepEndValue);
				float startValue = Util.getFloatFromString(currentStep.StepStartValue);
				if (endValue != startValue) {
					float angle = TimingRulerStep1Angle
							+ (TimingRulerStep2Angle - TimingRulerStep1Angle)
							* (powerNumValue - startValue) / (endValue - startValue);
					mTimingPowerPointer.setRotation(angle);
				}
			} else if (currentStep.Step.equals(ElectricityPrice.STEP_3)) {
				float startValue = Util.getFloatFromString(currentStep.StepStartValue);
				if (powerNumValue > 0) {
					float angle = (TimingRulerStep2Angle - 180) * startValue / powerNumValue  + 180;
					mTimingPowerPointer.setRotation(angle);
				}
			}

		}
		}
	}

	public Intent startGuoidanActivity(String columnId, String menuPath) {
		Intent intent = null;
		if (columnId.equals(GDCommon.ColumnIDGuodianMyPower)) {
			intent = new Intent();
			intent.putExtra(GDConstract.KeyPriceType, mPriceType);
			intent.setClass(mActivity, GDMypowerActivity.class);
		} else if (columnId.equals(GDCommon.ColumnIDGuodianPowerBill)) {
			intent = new Intent();
			if (mLoginData != null && mLoginData.UserData != null
					&& mLoginData.UserData.UserInfo != null) {
				intent.putExtra(GDConstract.KeyUserName,
						mLoginData.UserData.UserInfo.Name);
				intent.putExtra(GDConstract.KeyDeviceNo,
						mLoginData.UserData.UserInfo.ElecCard);
				intent.putExtra(GDConstract.KeyUserAddress,
						mLoginData.UserData.UserInfo.Address);
			}
			intent.setClass(mActivity, GDBillActivity.class);
		} else if (columnId.equals(GDCommon.ColumnIDGuodianFeeRecord)) {
		    intent = new Intent();
		    intent.setClass(mActivity, GDPlaymentRecordsActivity.class);
		} else if (columnId.equals(GDCommon.ColumnIDGuodianPowerNews)) {
			intent = new Intent();
			intent.setClass(mActivity, GDNoticeActivity.class);
		} else if (columnId.equals(GDCommon.ColumnIDGuodianBusinessNet)) {
			intent = new Intent();
			if (mLoginData != null && mLoginData.UserData != null
					&& mLoginData.UserData.UserInfo != null) {
				intent.putExtra(GDConstract.KeyUserAreaId,
						mLoginData.UserData.UserInfo.AreaIdPath);
			}
			intent.setClass(mActivity, GDBusinessAreaActvity.class);
		} else if(columnId.equals(GDCommon.ColumnIDGuodianMyElectrical)) {
		    intent = new Intent();
            intent.setClass(mActivity, GDSmartHomeMyEleActivity.class);
		}else if(columnId.equals(GDCommon.ColumnIDGuodianModel)){
		    intent = new Intent();
            intent.setClass(mActivity, GDSmartHomeModeActivity.class);
		}else if(columnId.equals(GDCommon.ColumnIDGuodianTimedTask)){
		    intent = new Intent();
            intent.setClass(mActivity, GDSmartHomeTimedTaskActivity.class);
		}else if(columnId.equals(GDCommon.ColumnIDGuodianPowerConstitue)){
            intent = new Intent();
            intent.setClass(mActivity, GDPowerConstitueActivity.class);
        }
		else if(columnId.equals(GDCommon.ColumnIDGuodianPowerConsumptionTrack)){
            intent = new Intent();
            intent.setClass(mActivity, GDPowerConsumptionTrackActivity.class);
        }
		else if(columnId.equals(GDCommon.ColumnIDGuodianPowerConsumptionTrend)){
            intent = new Intent();
            intent.setClass(mActivity, GDPowerConsumptionTrendActivity.class);
        }
		else if(columnId.equals(GDCommon.ColumnIDGuodianPowerTips)){
            intent = new Intent();
            intent.setClass(mActivity, GDPowerTipsActivity.class);
        }

		return intent;
	}
}
