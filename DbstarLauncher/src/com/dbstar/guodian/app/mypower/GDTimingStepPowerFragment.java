package com.dbstar.guodian.app.mypower;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.app.base.FragmentObserver;
import com.dbstar.guodian.app.base.GDBaseFragment;
import com.dbstar.guodian.app.base.GDSmartActivity;
import com.dbstar.guodian.data.EPCConstitute;
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.guodian.data.UserPriceStatus;
import com.dbstar.guodian.engine.GDConstract;
import com.dbstar.guodian.engine1.GDRequestType;
import com.dbstar.guodian.engine1.RequestParams;
import com.dbstar.guodian.parse.Util;
import com.dbstar.model.EventData;
import com.dbstar.widget.GDArcView;
import com.dbstar.widget.GDCircleTextView;

public class GDTimingStepPowerFragment extends GDBaseFragment {
	private static final String TAG = "GDTimingStepPowerFragment";
	private static final float TimingRulerStep1Angle = 40;
	private static final float TimingRulerStep2Angle = 140;

	private TextView mCostCycleTypeView , mAmountCycleTypeView, mCostView, mAmountView;
	//private TextView mMonthCostView, mMonthAmountView;
	private ImageView mTimingPowerPointer;
	private GDCircleTextView mTimingPowerStepView;
	private TextView mTimingPowerPriceView;
	private TextView mTimingPowerRulerStep0, mTimingPowerRulerStep1,
			mTimingPowerRulerStep2;
	private TextView mTimePowerPeriodView, mTimePowerPeriodTimeView;
	private GDArcView mTimingPowerPeriodPointer;
	private ProgressBar mMypowerProgressBar;
	private TextView mMypowerCountView;
	private Button mPriceButton;
	private TextView mAllDevicePowerAmountView;
	private int mPriceType;
	private String mStrDegree;
	private GDMypowerActivity mActivity;
	private ElectricityPrice mElecPrice = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mNum = getArguments().getInt("num");
		mActivity = (GDMypowerActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.mypower_timingsteppower, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initializeView();
	}

	void initializeView() {
		mStrDegree = mActivity.getResources().getString(R.string.str_degree);
		mAmountView = (TextView) mActivity
				.findViewById(R.id.mypower_poweramount);
		mCostView = (TextView) mActivity
				.findViewById(R.id.mypower_powercost);
		mCostCycleTypeView = (TextView) mActivity
                .findViewById(R.id.mypower_cost_cycletype);
        mAmountCycleTypeView = (TextView) mActivity
                .findViewById(R.id.mypower_amount_cycletype);
//		mMonthAmountView = (TextView) mActivity
//				.findViewById(R.id.mypower_monthamount);
//		mMonthCostView = (TextView) mActivity
//				.findViewById(R.id.mypower_monthcost);
//		
		mTimingPowerPointer = (ImageView) mActivity
				.findViewById(R.id.timingpower_pointer);
		mTimingPowerStepView = (GDCircleTextView) mActivity
				.findViewById(R.id.timingppower_step);
		mTimingPowerPriceView = (TextView) mActivity
				.findViewById(R.id.timingpower_powerprice);

		mTimingPowerRulerStep0 = (TextView) mActivity
				.findViewById(R.id.timingpower_ruler_step0);

		mTimingPowerRulerStep1 = (TextView) mActivity
				.findViewById(R.id.timingpower_ruler_step1);
		mTimingPowerRulerStep2 = (TextView) mActivity
				.findViewById(R.id.timingpower_ruler_step2);
		mTimePowerPeriodView = (TextView) mActivity
				.findViewById(R.id.timingpower_period);
		mTimePowerPeriodTimeView = (TextView) mActivity
				.findViewById(R.id.timingpower_time);
		mTimingPowerPeriodPointer = (GDArcView) mActivity
				.findViewById(R.id.timingpower_periodpointer);

		mMypowerProgressBar = (ProgressBar) mActivity
				.findViewById(R.id.progress_bar);
		mMypowerCountView = (TextView) mActivity
				.findViewById(R.id.mypower_count);
		mAllDevicePowerAmountView = (TextView) mActivity
		        .findViewById(R.id.mypower_allamount);
		
		mAmountView.setText("0");
		mCostView.setText("0");
//		mMonthCostView.setText("0");
//		mMonthAmountView.setText("0");
		mMypowerCountView.setText("0" + mStrDegree);
		mMypowerProgressBar.setProgress(0);

		mTimingPowerPointer.setRotation(0);
		mTimingPowerStepView.setText("");
		mTimingPowerPriceView.setText("");
		mTimingPowerRulerStep0.setText("");
		mTimingPowerRulerStep1.setText("");
		mTimingPowerRulerStep2.setText("");
		mTimePowerPeriodView.setText("");
		mTimePowerPeriodTimeView.setText("");
		mTimingPowerPeriodPointer.setRotation(0);

		mPriceButton = (Button) mActivity
				.findViewById(R.id.mypower_query_button);
		mPriceButton.setFocusableInTouchMode(true);
		mPriceButton.setFocusable(true);
		mPriceButton.requestFocus();
		mPriceButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showPriceDialog();
			}
		});
	}

	public void serviceStart() {
		if (mService == null)
			return;
		
		LoginData mLoginData = mService.getLoginData();
        if(mLoginData == null){
           mActivity.showErrorMsg(R.string.no_login);
           return;
        }
        if(mLoginData.CtrlNo == null || mLoginData.CtrlNo.CtrlNoGuid == null){
            mActivity.showErrorMsg(R.string.no_login);
               return; 
        }
        
        String ccguid = mLoginData.CtrlNo.CtrlNoGuid;
        
        if(mLoginData.UserData == null 
                ||mLoginData.UserData.UserInfo == null 
                || mLoginData.UserData.UserInfo.UserType == null){
            mActivity.showErrorMsg(R.string.no_login);
            return; 
        }
        
        String userType = mLoginData.UserData.UserInfo.UserType;
        RequestParams params = new RequestParams(GDRequestType.DATATYPE_POWERPANELDATA);
        params.put(RequestParams.KEY_SYSTEM_FLAG,"elc");
        params.put(RequestParams.KEY_METHODID, "m008f001");
        params.put(JsonTag.TAGNumCCGuid, ccguid);
        params.put(JsonTag.TAGUser_Type, userType);
		mEngine.request(params);
	}

	// handle event at this point
	public void notifyEvent(FragmentObserver observer, int type, Object event) {
		if (observer != this)
			return;

		if (type == EventData.EVENT_GUODIAN_DATA) {
			EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
			handlePowerData(guodianEvent.Type, guodianEvent.Data);
		} else if(EventData.EVENT_GUODIAN_DATA_ERROR == type){
		    GDSmartActivity activity = (GDSmartActivity) getActivity();
            activity.showErrorMsg(R.string.loading_error);
        }
	}

	/*public void handleLoginSuccessed() {
		Log.d(TAG, "handleLoginSuccessed");
		
		mEngine.request(GDConstract.DATATYPE_POWERPANELDATA, null);
	}*/
	
	private void handlePowerData(int type, Object data) {
		if (type == GDRequestType.DATATYPE_POWERPANELDATA) {
			updatePowerPanel((PowerPanelData) data);
			mPriceButton.setFocusableInTouchMode(true);
            mPriceButton.setFocusable(true);
            mPriceButton.requestFocus();
		}
	}

	private void updatePowerPanel(PowerPanelData data) {
		Log.d(TAG, " ===== updatePowerPanel ===== ");
		if (data == null)
			return;

		float powerNumValue = 0, powerFeeValue = 0;
		String powerNum = "", powerFee = "";
		
		 //if cycletype is null ,default is year
	    if(ElectricityPrice.CYCLETYPE_YEAR.equals(data.PriceStatus.CycleType)){
            mCostCycleTypeView.setText(R.string.mypower_text_yearpowercost);
            mAmountCycleTypeView.setText(R.string.mypower_text_yearpoweramount);
            if(data.YearPower != null){
                powerNum = data.YearPower.Count;
                powerFee = data.YearPower.Fee;
            }
            
        }else{
            mCostCycleTypeView.setText(R.string.mypower_text_monthpowercost);
            mAmountCycleTypeView.setText(R.string.mypower_text_monthpoweramount);
            if(data.MonthPower != null){
                powerNum = data.MonthPower.Count;
                powerFee = data.MonthPower.Fee;
            }
        }
	    
	    if(powerNum != null && powerFee != null){
    	    mAmountView.setText(powerNum);
    	    mCostView.setText(powerFee);
        
    	    powerNumValue =Util.getFloatFromString(powerNum);
            powerFeeValue = Util.getFloatFromString(powerFee);
	    }
		// show target
		if (data.Target != null && data.Target.mPower != null) {
			String myTarget = data.Target.mPower.Count;
			mMypowerCountView.setText(myTarget + " " + mStrDegree);
			float targetValue = Util.getFloatFromString(myTarget);;
			if (targetValue != 0) {
				float progress = (float) (powerNumValue / targetValue);
				mMypowerProgressBar.setProgress((int)(progress * 100));
			}
		}
		
		
		UserPriceStatus status = data.PriceStatus;

		if (status == null)
			return;

		if (status.PriceType == null) {
			return;
		}

		String priceType = status.PriceType;

		Log.d(TAG, " ===== PriceType ===== " + priceType);

		if (ElectricityPrice.PRICETYPE_TIMING.equals(priceType)) {
			mPriceType = GDConstract.PriceTypeTiming;
			mTimingPowerStepView.setText(getResources().getString(R.string.powerprice_type_timing));
		}else if(ElectricityPrice.PRICETYPE_STEPPLUSTIMING.equals(priceType)){
		    mPriceType = GDConstract.PriceTypeStepPlusTiming;
		    mTimingPowerStepView.setText(Util
                    .getStepStr(mActivity, status.Step));
		} else {
			return;
		}

		if (mElecPrice == null && mService.getLoginData() != null) {
			mElecPrice = mService.getLoginData().ElecPrice;
		}
		
	    if(mService.getLoginData() != null){
            mAllDevicePowerAmountView.setText(mService.getLoginData().ControlledPowerCount);
        }
		ElectricityPrice priceData = mElecPrice;

		if (priceData == null)
			return;
		    
			mTimingPowerPriceView.setText(status.Price);
			
			mTimePowerPeriodView.setText(Util.getPeriodStr(mActivity, status.CurrentPeriodType));
			mTimePowerPeriodTimeView.setText(Util.getPeriodTimeString(status.Period));
			
			float sweep [] = Util.getSweep(status.Period);
			
			mTimingPowerPeriodPointer.setSweepAngle(sweep[0], sweep[1]);
			
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
					mTimingPowerRulerStep0.setText(stepPrice.StepStartValue);
					mTimingPowerRulerStep1.setText(stepPrice.StepEndValue);
				} else if (stepPrice.Step.equals(ElectricityPrice.STEP_2)) {
					mTimingPowerRulerStep2.setText(stepPrice.StepEndValue);
				}
			}

			if (powerNumValue == 0) {
				mTimingPowerPointer.setRotation(0);
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
	
	void showPriceDialog() {
	    if(mElecPrice == null)
            mElecPrice = mService.getElecPrice();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("price_dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		GDPriceDlgFragment newFragment = GDPriceDlgFragment
				.newInstance(mElecPrice);
		newFragment.show(ft, "price_dialog");
	}
}
