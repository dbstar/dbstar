package com.dbstar.guodian.app.mypower;

import java.util.List;

import com.dbstar.R;
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.ElectricityPrice.PeriodPrice;
import com.dbstar.guodian.data.ElectricityPrice.StepPrice;
import com.dbstar.guodian.parse.Util;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GDPriceDlgFragment extends DialogFragment {

	private static final String TAG = "GDPriceDlgFragment";

	private ElectricityPrice mPriceData;

	private ListAdapter mAdapter;
	private ListView mListView;

	private Activity mActivity;

	private String mStrHan, mStrStep1, mStrStep2, mStrStep3;

	public static GDPriceDlgFragment newInstance(ElectricityPrice priceData) {
		GDPriceDlgFragment f = new GDPriceDlgFragment(priceData);
		return f;
	}

	public GDPriceDlgFragment(ElectricityPrice priceData) {
		mPriceData = priceData;

		Log.d(TAG, " ==== price data = " + mPriceData);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.GDAlertDialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.mypower_priceview, null, false);
		mListView = (ListView) v.findViewById(R.id.price_list);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = getActivity();
		initializeView();
	}

	private void initializeView() {

		mStrHan = mActivity.getResources().getString(R.string.suffix_str_han);
		mStrStep1 = mActivity.getResources().getString(R.string.str_step_1);
		mStrStep2 = mActivity.getResources().getString(R.string.str_step_2);
		mStrStep3 = mActivity.getResources().getString(R.string.str_step_3);

		List<StepPrice> stepPrices = null;
		if (mPriceData != null) {
			stepPrices = mPriceData.StepPriceList;
		}

		if (stepPrices != null) {
			mAdapter = new ListAdapter();
			StepPrice[] stepPrice = new StepPrice[stepPrices.size()];
			for (int i = 0; i < stepPrice.length; i++) {
				stepPrice[i] = stepPrices.get(i);
			}

			Log.d(TAG, " ==== step size = " + stepPrice.length);

			mAdapter.setDataSet(stepPrice);
			Log.d(TAG, " === listview = " + mListView);
			mListView.setAdapter(mAdapter);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private class ListAdapter extends BaseAdapter {

		public class ViewHolder {
			TextView stepView;
			TextView priceIntervalView;
			TextView periodPrice1View, periodPrice2View, periodPrice3View;
			ViewGroup row2, row3;
		}

		private StepPrice[] mDataSet = null;

		public ListAdapter() {
		}

		public void setDataSet(StepPrice[] dataSet) {
			mDataSet = dataSet;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (mDataSet != null) {
				count = mDataSet.length;
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				LayoutInflater inflater = mActivity.getLayoutInflater();
				convertView = inflater.inflate(R.layout.price_list_stepitem,
						parent, false);

				holder = new ViewHolder();
				holder.stepView = (TextView) convertView
						.findViewById(R.id.step);
				holder.priceIntervalView = (TextView) convertView
						.findViewById(R.id.price_interval);

				holder.periodPrice1View = (TextView) convertView
						.findViewById(R.id.period_price_1);

				holder.periodPrice2View = (TextView) convertView
						.findViewById(R.id.period_price_2);

				holder.periodPrice3View = (TextView) convertView
						.findViewById(R.id.period_price_3);

				holder.row2 = (ViewGroup) convertView.findViewById(R.id.row_2);

				holder.row3 = (ViewGroup) convertView.findViewById(R.id.row_3);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.stepView.setText(getStepStr(mDataSet[position].Step));
			holder.priceIntervalView.setText(mDataSet[position].StepStartValue
					+ "~" + mDataSet[position].StepEndValue + mStrHan);

			if (mDataSet[position].PeriodPriceList != null) {
				List<PeriodPrice> periodList = mDataSet[position].PeriodPriceList;

				Log.d(TAG, "===periode size == " + periodList.size());

				PeriodPrice periodPrice = null;
				if (periodList.size() > 0) {
					periodPrice = periodList.get(0);
					holder.periodPrice1View.setText(Util.getPeriodStr(
							mActivity, periodPrice.PeriodType)
							+ "  "
							+ periodPrice.Price);
				}

				if (periodList.size() > 1) {
					periodPrice = periodList.get(1);
					holder.periodPrice2View.setText(Util.getPeriodStr(
							mActivity, periodPrice.PeriodType)
							+ "  "
							+ periodPrice.Price);

					holder.row2.setVisibility(View.VISIBLE);
				}

				if (periodList.size() > 2) {
					periodPrice = periodList.get(2);
					holder.periodPrice3View.setText(Util.getPeriodStr(
							mActivity, periodPrice.PeriodType)
							+ "  "
							+ periodPrice.Price);
					holder.row2.setVisibility(View.VISIBLE);
				}
			} else {
				holder.periodPrice1View.setText(mDataSet[position].StepPrice);
				holder.row2.setVisibility(View.GONE);
				holder.row3.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	private String getStepStr(String step) {
		if (ElectricityPrice.STEP_1.equals(step)) {
			return mStrStep1;
		} else if (ElectricityPrice.STEP_2.equals(step)) {
			return mStrStep2;
		} else if (ElectricityPrice.STEP_3.equals(step)) {
			return mStrStep3;
		} else {
			return "";
		}
	}
}
