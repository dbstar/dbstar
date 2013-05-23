package com.dbstar.guodian.app.mypower;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.guodian.data.BillDetail;
import com.dbstar.guodian.data.BillDetailData;
import com.dbstar.guodian.data.BillDetailListData;
import com.dbstar.guodian.data.BillItem;
import com.dbstar.guodian.engine.GDConstract;
import com.dbstar.model.EventData;
import com.dbstar.util.DateUtil;

public class GDBillActivity extends GDBaseActivity {
	private static final String TAG = "GDBillActivity";

	private String StrYear, StrMonth;

	private TextView mUserNameView, mDeviceNoView, mAddressView;
	private TextView mItemsCountView;
	private ListView mBillListView;
	private ListAdapter mBillAdaper;
	private Spinner mYearSpinner, mMonthSpinner;
	private ArrayAdapter<String> mYearAdapter, mMonthAdapter;
	private ArrayList<String> mYearList, mMonthList;

	private Button mQueryButton;
	private String mUserName, mDeviceNo, mAddress;

	private String mServiceDate = null;
	private String mCurrentMonth, mCurrentYear;
	private BillDetail mCurrentMonthBill;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mypower_billview);

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		mUserName = intent.getStringExtra(GDConstract.KeyUserName);
		mDeviceNo = intent.getStringExtra(GDConstract.KeyDeviceNo);
		mAddress = intent.getStringExtra(GDConstract.KeyUserAddress);

		initializeView();

		if (mMenuPath != null) {
			String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
			showMenuPath(menuArray);
		}
	}

	public void initializeView() {
		super.initializeView();

		StrYear = getResources().getString(R.string.ch_year);
		StrMonth = getResources().getString(R.string.ch_month);

		mUserNameView = (TextView) findViewById(R.id.user_name);
		mDeviceNoView = (TextView) findViewById(R.id.device_no);
		mAddressView = (TextView) findViewById(R.id.user_address);
		mItemsCountView = (TextView) findViewById(R.id.items_number);
		mYearSpinner = (Spinner) findViewById(R.id.year_spinner);
		mMonthSpinner = (Spinner) findViewById(R.id.month_spinner);

		mBillListView = (ListView) findViewById(R.id.bill_list);
		mBillAdaper = new ListAdapter();
		mBillListView.setAdapter(mBillAdaper);

		mQueryButton = (Button) findViewById(R.id.mypower_query_button);

		mQueryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				queryBillData();
			}
		});

		mUserNameView.setText(mUserName == null ? "" : mUserName);
		mDeviceNoView.setText(mDeviceNo == null ? "" : mDeviceNo);
		mAddressView.setText(mAddress == null ? "" : mAddress);
	}

	private void initializeData(String dateStr) {

//		Date date = DateUtil.getDateFromStr(dateStr, DateUtil.DateFormat1);
//
//		Calendar c = Calendar.getInstance();
//		c.setTime(date);
//		int year = c.get(Calendar.YEAR);
//		int month = c.get(Calendar.MONTH);
		
		if (dateStr == null || dateStr.isEmpty()) {
			return;
		}

		String[] temp = dateStr.split(" ");
		String date = temp[0];
		String[] dateTime = date.split("-");

		mCurrentYear = dateTime[0];
		mCurrentMonth = dateTime[1];
		
		int year = Integer.valueOf(mCurrentYear);
		int month = Integer.valueOf(mCurrentMonth);

		mYearList = new ArrayList<String>();
		for (int i = year; i > year - 10; i--) {
			mYearList.add(String.valueOf(i));
		}

		mMonthList = new ArrayList<String>();
		String emptyDate = getResources().getString(R.string.str_month);
		mMonthList.add(emptyDate);
		for (int i = 1; i < 13; i++) {
			mMonthList.add(String.valueOf(i));
		}

		mYearAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,
				mYearList);
		mYearSpinner.setAdapter(mYearAdapter);
		mYearSpinner.setSelection(0);

		mMonthAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,
				mMonthList);

		mMonthSpinner.setAdapter(mMonthAdapter);
		mMonthSpinner.setSelection(month);
	}

	protected void onServiceStart() {
		super.onServiceStart();
		Log.d(TAG, "onServiceStart");

		// mService.requestPowerData(GDConstract.DATATYPE_BILLMONTHLIST, "1");

		// first: request the latest bill
		// date is empty.
		mService.requestPowerData(GDConstract.DATATYPE_BILLDETAILOFMONTH, "");
	}

	void queryBillData() {
		int yearIndex = mYearSpinner.getSelectedItemPosition();
		int monthIndex = mMonthSpinner.getSelectedItemPosition();
		initalListData(null);
		Log.d(TAG, "queryBillData yearIndex =" + yearIndex + " monthIndex="
				+ monthIndex);

		if (monthIndex > 0) {
			String year = mYearList.get(yearIndex);
			String month = mMonthList.get(monthIndex);
			// TODO: how to construct the datetime?
			if (monthIndex < 10) {
				month = "0" + month;
			}
			String date = year + "-" + month + "-" + "01 00:00:00";

			Log.d(TAG, " === date ==" + date);

			mService.requestPowerData(GDConstract.DATATYPE_BILLDETAILOFMONTH,
					date);
		} else {
			mService.requestPowerData(GDConstract.DATATYPE_BILLDETAILOFRECENT,
					"12");
			
		}
	}

	public void notifyEvent(int type, Object event) {
		super.notifyEvent(type, event);

		if (type == EventData.EVENT_GUODIAN_DATA) {
			EventData.GuodianEvent guodianEvent = (EventData.GuodianEvent) event;
			handlePowerData(guodianEvent.Type, guodianEvent.Data);
		}
	}

	private void handlePowerData(int type, Object data) {
		if (data == null) {
			Log.d(TAG, "ERROR: data is null");
			return;
		}
		
		if (type == GDConstract.DATATYPE_BILLDETAILOFMONTH) {
			BillDetailData detailData = (BillDetailData) data;

			if (mServiceDate == null) {
				mServiceDate = detailData.ServiceSysDate;

				initializeData(mServiceDate);
				mCurrentMonthBill = detailData.Detail;
			}

			String month = mMonthList.get(mMonthSpinner
					.getSelectedItemPosition());
			String year = mYearList.get(mYearSpinner.getSelectedItemPosition());

			BillDataItem item = constructBillData(detailData.Detail, year,
					month);

			if (item != null) {
				BillDataItem[] items = new BillDataItem[1];
				items[0] = item;
				initalListData(items);
			}else{
			    Toast.makeText(this, R.string.load_data_is_null, Toast.LENGTH_SHORT).show();
			}
		} else if (type == GDConstract.DATATYPE_BILLDETAILOFRECENT) {
			BillDetailListData listData = (BillDetailListData) data;
			if (mServiceDate == null) {
				mServiceDate = listData.ServiceSysDate;
				initializeData(mServiceDate);
			}
			
			ArrayList<BillDetail> detailList = listData.DetailList;
			if (detailList != null && detailList.size() > 0) {
				int size = detailList.size();
				int yearIndx = mYearSpinner.getSelectedItemPosition();
				String currentyear = mYearList.get(yearIndx);
				ArrayList<BillDataItem> tempArray = new ArrayList<GDBillActivity.BillDataItem>();
				for (int i = 0; i < size; i++) {
					BillDetail billDetail = detailList.get(i);
					String startDate = billDetail.StartDate;
					String year = "0", month = "0";
					if (startDate != null && !startDate.isEmpty()) {
						String[] times = startDate.split(" ");
						String date = times[0];
						String[] dates = date.split("-");
						year = dates[0];
						if (dates.length > 1) {
							month = dates[1];
						}
					}
					if(currentyear.equals(year)){
					    tempArray.add(constructBillData(billDetail, year, month));
					}
				}
				initalListData(tempArray.toArray(new BillDataItem[tempArray.size()]));
			}else{
			    Toast.makeText(this, R.string.load_data_is_null, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void initalListData(BillDataItem [] itmes){
	    mBillAdaper.setDataSet(itmes);
        mBillAdaper.notifyDataSetChanged();
        String strGong = getResources().getString(R.string.text_gong);
        String strTiao = getResources().getString(R.string.text_tiao);
        int size = 0;
        if(itmes != null)
            size = itmes.length;
        mItemsCountView.setText(strGong + size + strTiao);
	}
	private BillDataItem constructBillData(BillDetail detail, String year,
			String month) {
		BillDataItem item = null;

		if (detail == null || detail.BillList == null
				|| detail.BillList.size() == 0) {
		    
			return item;
		}

		ArrayList<BillItem> billItems = detail.BillList;
		int size = billItems.size();
		float count = 0, fee = 0;
		for (int i = 0; i < size; i++) {
			BillItem billItem = billItems.get(i);

			count += Float.valueOf(billItem.Count);
			fee += Float.valueOf(billItem.Fee);
		}

		item = new BillDataItem();
		item.Count = String.valueOf(count);
		item.Fee = String.valueOf(fee);
		item.Month = month;
		item.Year = year;
		return item;
	}

	private class BillDataItem {
		public String Year;
		public String Month;
		public String Count;
		public String Fee;
	}

	private class ListAdapter extends BaseAdapter {

		public class ViewHolder {
			TextView period;
			TextView amount;
			TextView cost;
		}

		private BillDataItem[] mDataSet = null;

		public ListAdapter() {
		}

		public void setDataSet(BillDataItem[] dataSet) {
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
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.mypower_billlist_item,
						parent, false);

				holder = new ViewHolder();
				holder.period = (TextView) convertView
						.findViewById(R.id.period);
				holder.amount = (TextView) convertView
						.findViewById(R.id.amount);

				holder.cost = (TextView) convertView.findViewById(R.id.cost);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.period.setText(mDataSet[position].Year + StrYear
					+ mDataSet[position].Month + StrMonth);
			holder.amount.setText(mDataSet[position].Count);
			holder.cost.setText(mDataSet[position].Fee);

			return convertView;
		}
	}

}
