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
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.guodian.data.UserPriceStatus;
import com.dbstar.guodian.egine.GDConstract;
import com.dbstar.guodian.parse.Util;
import com.dbstar.model.EventData;

public class GDStepPowerFragment extends GDBaseFragment {
	private static final String TAG = "GDStepPowerFragment";
	private static final float StepRulerStep1Angle = 50;
	private static final float StepRulerStep2Angle = 126;
	// Power View
	private TextView mYearCostView, mYearAmountView;
	private TextView mMonthCostView, mMonthAmountView;
	// Step Power
	private ImageView mStepPowerPointer;
	private TextView mStepPowerStepView;
	private TextView mStepPowerPriceView;
	private TextView mStepPowerRulerStep0, mStepPowerRulerStep1,
			mStepPowerRulerStep2;

	private ProgressBar mMypowerProgressBar;
	private TextView mMypowerCountView;

	private Button mPriceButton;
	private String mStrDegree;
	private int mPriceType;

	private ElectricityPrice mElecPrice = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mNum = getArguments().getInt("num");
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
		mYearAmountView = (TextView) mActivity
				.findViewById(R.id.mypower_poweramount);
		mYearCostView = (TextView) mActivity
				.findViewById(R.id.mypower_powercost);

		mMonthAmountView = (TextView) mActivity
				.findViewById(R.id.mypower_monthamount);
		mMonthCostView = (TextView) mActivity
				.findViewById(R.id.mypower_monthcost);

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

		mYearAmountView.setText("0");
		mYearCostView.setText("0");
		mMonthCostView.setText("0");
		mMonthAmountView.setText("0");
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

		mService.requestPowerData(GDConstract.DATATYPE_POWERPANELDATA, null);
	}

	// handle event at this point
	public void notifyEvent(FragmentObserver observer, int type, Object event) {
		if (observer != this)
			return;

		if (type == EventData.EVENT_GUODIAN_DATA) {
			EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
			handlePowerData(guodianEvent.Type, guodianEvent.Data);
		}
	}

	private void handlePowerData(int type, Object data) {
		if (type == GDConstract.DATATYPE_POWERPANELDATA) {
			updatePowerPanel((PowerPanelData) data);
		}
	}

	private void updatePowerPanel(PowerPanelData data) {
		Log.d(TAG, " ===== updatePowerPanel ===== ");

		if (data == null)
			return;

		float powerNumValue = 0, powerFeeValue = 0, yearPowerValue = 0;
		String powerNum = "", powerFee = "";

		if (data.MonthPower != null) {
			powerNum = data.MonthPower.Count;
			powerFee = data.MonthPower.Fee;
			powerNumValue = Float.valueOf(powerNum);
			powerFeeValue = Float.valueOf(powerFee);
		}

		if (data.YearPower != null) {
			mYearAmountView.setText(data.YearPower.Count);
			mYearCostView.setText(data.YearPower.Fee);

			yearPowerValue = Float.valueOf(data.YearPower.Count);
		}

		// show target
		if (data.Target != null && data.Target.mPower != null) {
			String myTarget = data.Target.mPower.Count;
			mMypowerCountView.setText(myTarget);
			float targetValue = Float.valueOf(myTarget);
			if (targetValue != 0) {
				int progress = (int) (yearPowerValue / targetValue);
				mMypowerProgressBar.setProgress(progress);
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
		} else {
			return;
		}

		if (mElecPrice == null) {
			mElecPrice = mService.getElecPrice();
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
					float angle = StepRulerStep1Angle
							+ (StepRulerStep2Angle - StepRulerStep1Angle)
							* (powerValue / (endValue - startValue));
					mStepPowerPointer.setRotation(angle);
				}
			} else if (currentStep.Step.equals(ElectricityPrice.STEP_3)) {
				float angle = StepRulerStep2Angle + 20;
				mStepPowerPointer.setRotation(angle);
			}
		}
	}

	void showPriceDialog() {
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
