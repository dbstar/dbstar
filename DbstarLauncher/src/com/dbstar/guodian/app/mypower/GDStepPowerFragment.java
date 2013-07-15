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

public class GDStepPowerFragment extends GDBaseFragment {
	private static final String TAG = "GDStepPowerFragment";
	private static final float StepRulerStep1Angle = 50;
	private static final float StepRulerStep2Angle = 126;
	// Power View
	private TextView mCostCycleTypeView , mAmountCycleTypeView,mCostView, mAmountView;
	//private TextView mMonthCostView, mMonthAmountView;
	// Step Power
	private ImageView mStepPowerPointer;
	private TextView mStepPowerStepView;
	private TextView mStepPowerPriceView;
	private TextView mStepPowerRulerStep0, mStepPowerRulerStep1,
			mStepPowerRulerStep2;

	private ProgressBar mMypowerProgressBar;
	private TextView mMypowerCountView;
	private TextView mAllDevicePowerAmountView;
	private Button mPriceButton;
	private String mStrDegree;
	private int mPriceType;
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
		return inflater.inflate(R.layout.mypower_steppower, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initializeView();
	}

	void initializeView() {
		mStrDegree = mActivity.getResources().getString(R.string.str_degree);
		// Power View
		mCostCycleTypeView = (TextView) mActivity
		        .findViewById(R.id.mypower_cost_cycletype);
		mAmountCycleTypeView = (TextView) mActivity
		        .findViewById(R.id.mypower_amount_cycletype);
		mAmountView = (TextView) mActivity
				.findViewById(R.id.mypower_poweramount);
		mCostView = (TextView) mActivity
				.findViewById(R.id.mypower_powercost);

//		mMonthAmountView = (TextView) mActivity
//				.findViewById(R.id.mypower_monthamount);
//		mMonthCostView = (TextView) mActivity
//				.findViewById(R.id.mypower_monthcost);

		// step power
		mStepPowerPointer = (ImageView) mActivity
				.findViewById(R.id.power_pointer);
		mStepPowerStepView = (TextView) mActivity
				.findViewById(R.id.steppower_step);
		mStepPowerPriceView = (TextView) mActivity
				.findViewById(R.id.mypower_powerprice);

		mStepPowerRulerStep0 = (TextView) mActivity
				.findViewById(R.id.steppower_ruler_step0);
		mStepPowerRulerStep1 = (TextView) mActivity
				.findViewById(R.id.steppower_ruler_step1);
		mStepPowerRulerStep2 = (TextView) mActivity
				.findViewById(R.id.steppower_ruler_step2);

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

		mStepPowerPointer.setRotation(0);
		mStepPowerStepView.setText("");
		mStepPowerPriceView.setText("");
		mStepPowerRulerStep0.setText("");
		mStepPowerRulerStep1.setText("");
		mStepPowerRulerStep2.setText("");

		mPriceButton = (Button) mActivity
				.findViewById(R.id.mypower_query_button);
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
		   mActivity.handleErrorResponse(R.string.no_login);
		   return;
		}
		if(mLoginData.CtrlNo == null || mLoginData.CtrlNo.CtrlNoGuid == null){
		    mActivity.handleErrorResponse(R.string.no_login);
	           return; 
		}
		
		String ccguid = mLoginData.CtrlNo.CtrlNoGuid;
		
		if(mLoginData.UserData == null 
		        ||mLoginData.UserData.UserInfo == null 
		        || mLoginData.UserData.UserInfo.UserType == null){
		    mActivity.handleErrorResponse(R.string.no_login);
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
		    activity.handleErrorResponse(R.string.loading_error);
        }
	}

	private void handlePowerData(int type, Object data) {
		if (type == GDRequestType.DATATYPE_POWERPANELDATA) {
			updatePowerPanel((PowerPanelData) data);
			mPriceButton.setFocusableInTouchMode(true);
			mPriceButton.setFocusable(true);
			mPriceButton.requestFocus();
		}
	}

	/*public void handleLoginSuccessed() {
		Log.d(TAG, "handleLoginSuccessed");
		
		mEngine.request(GDConstract.DATATYPE_POWERPANELDATA, null);
	}*/

	private void updatePowerPanel(PowerPanelData data) {
		Log.d(TAG, " ===== updatePowerPanel ===== ");

		if (data == null)
			return;

		float powerNumValue = 0, powerFeeValue = 0;
		String powerNum = "", powerFee = "";
		
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
          
          powerNumValue = Util.getFloatFromString(powerNum);;
          powerFeeValue = Util.getFloatFromString(powerFee);;
	  }
		// show target
		if (data.Target != null && data.Target.mPower != null) {
		    String myTarget = data.Target.mPower.Count;
			mMypowerCountView.setText(myTarget + " " + mStrDegree);
			float targetValue = Util.getFloatFromString(myTarget);
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
		if (priceType.equals(ElectricityPrice.PRICETYPE_STEP)) {
            mPriceType = GDConstract.PriceTypeStep;
        }else if(priceType.equals(ElectricityPrice.PRICETYPE_SINGLE)){
            mPriceType = GDConstract.PriceTypeSingle;
        } else {
            return;
        }

		if (mElecPrice == null) {
			mElecPrice = mService.getElecPrice();
		}
		EPCConstitute ed = mService.getEDimension();
        if(ed != null && ed.totalPower != null){
            mAllDevicePowerAmountView.setText(ed.totalPower.Count);
        }
		
		ElectricityPrice priceData = mElecPrice;

		if (priceData == null)
			return;

		if (mPriceType == GDConstract.PriceTypeStep) {
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
				float startValue = Util.getFloatFromString(currentStep.StepStartValue);
				if (powerNumValue > 0) {
					float angle = (StepRulerStep2Angle - 180) * startValue / powerNumValue  + 180;
					mStepPowerPointer.setRotation(angle);
				}
			}
		}else if(mPriceType == GDConstract.PriceTypeSingle){
		    mStepPowerStepView.setText(R.string.powerprice_type_single);
		    mStepPowerPriceView.setText(priceData.SinglePrice);
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
