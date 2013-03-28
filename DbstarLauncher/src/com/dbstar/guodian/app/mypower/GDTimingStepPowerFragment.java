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
import com.dbstar.widget.GDArcView;
import com.dbstar.widget.GDCircleTextView;

public class GDTimingStepPowerFragment extends GDBaseFragment {
	private static final String TAG = "GDTimingStepPowerFragment";
	private static final float TimingRulerStep1Angle = 40;
	private static final float TimingRulerStep2Angle = 140;

	private TextView mYearCostView, mYearAmountView;
	private TextView mMonthCostView, mMonthAmountView;
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

	private int mPriceType;
	private String mStrDegree;

	private ElectricityPrice mElecPrice = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mNum = getArguments().getInt("num");
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
		mYearAmountView = (TextView) mActivity
				.findViewById(R.id.mypower_poweramount);
		mYearCostView = (TextView) mActivity
				.findViewById(R.id.mypower_powercost);

		mMonthAmountView = (TextView) mActivity
				.findViewById(R.id.mypower_monthamount);
		mMonthCostView = (TextView) mActivity
				.findViewById(R.id.mypower_monthcost);

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

		mYearAmountView.setText("0");
		mYearCostView.setText("0");
		mMonthCostView.setText("0");
		mMonthAmountView.setText("0");
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
			mMypowerCountView.setText(myTarget + " " + mStrDegree);
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

			mTimingPowerStepView.setText(Util
					.getStepStr(mActivity, status.Step));
			mTimingPowerPriceView.setText(status.Price);

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
				float endValue = Float.valueOf(stepEnd);
				if (endValue != 0) {
					float powerValue = Float.valueOf(powerNum);
					float angle = TimingRulerStep1Angle
							* (powerValue / endValue);
					mTimingPowerPointer.setRotation(angle);
				}
			} else if (currentStep.Step.equals(ElectricityPrice.STEP_2)) {
				float endValue = Float.valueOf(currentStep.StepEndValue);
				float startValue = Float.valueOf(currentStep.StepStartValue);
				if (endValue != startValue) {
					float powerValue = Float.valueOf(powerNum);
					float angle = TimingRulerStep1Angle
							+ (TimingRulerStep2Angle - TimingRulerStep1Angle)
							* (powerValue - startValue) / (endValue - startValue);
					mTimingPowerPointer.setRotation(angle);
				}
			} else if (currentStep.Step.equals(ElectricityPrice.STEP_3)) {
				float startValue = Float.valueOf(currentStep.StepStartValue);
				float powerValue = Float.valueOf(powerNum);
				if (powerValue > 0) {
					float angle = (TimingRulerStep2Angle - 180) * startValue / powerValue  + 180;
					mTimingPowerPointer.setRotation(angle);
				}
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
