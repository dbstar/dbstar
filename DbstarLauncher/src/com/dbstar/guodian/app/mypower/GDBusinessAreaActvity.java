package com.dbstar.guodian.app.mypower;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.guodian.data.BusinessArea;
import com.dbstar.guodian.egine.GDConstract;
import com.dbstar.model.EventData;

public class GDBusinessAreaActvity extends GDBaseActivity {
	private static final String TAG = "GDBusinessAreaActvity";

	private String mAreaId = null;
	private static final int PageSize = 8;
	private ArrayList<BusinessArea[]> mPagesData;
	private int mPageCount, mPageNumber;

	private ListView mListView;
	private ListAdapter mBusinessAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mypower_businessareaview);

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		mAreaId = intent.getStringExtra(GDConstract.KeyUserAreaId);
		initializeView();

		if (mMenuPath != null) {
			String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
			showMenuPath(menuArray);
		}
	}

	public void initializeView() {
		super.initializeView();

		mListView = (ListView) findViewById(R.id.listview);
		mBusinessAdapter = new ListAdapter();
		mListView.setAdapter(mBusinessAdapter);

		mListView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				boolean ret = false;
				int action = event.getAction();
				if (action == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {

					case KeyEvent.KEYCODE_DPAD_UP: {
						int selectedIndex = mListView.getSelectedItemPosition();
						if (selectedIndex == 0 && mPageNumber > 0) {
							loadPrevPage();
							ret = true;
						}
						break;
					}
					case KeyEvent.KEYCODE_DPAD_DOWN: {
						int selectedIndex = mListView.getSelectedItemPosition();
						if (selectedIndex == (PageSize - 1)
								&& mPageNumber < mPageCount - 1) {
							loadNextPage();
							ret = true;
						}
						break;
					}
					default:
						break;
					}

				}
				return ret;
			}
		});
	}

	protected void onServiceStart() {
		super.onServiceStart();
		Log.d(TAG, "onServiceStart");

		mService.requestPowerData(GDConstract.DATATYPE_BUSINESSAREA, mAreaId);
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

		if (type == GDConstract.DATATYPE_BUSINESSAREA) {
			ArrayList<BusinessArea> business = (ArrayList<BusinessArea>) data;
			constructPages(business);
			displayPage(mPageNumber);
		}
	}

	private void displayPage(int pageNumber) {
		BusinessArea[] page = mPagesData.get(pageNumber);
		mBusinessAdapter.setDataSet(page);
		mBusinessAdapter.notifyDataSetChanged();
	}

	private void constructPages(ArrayList<BusinessArea> items) {
		int size = items.size();
		if (size == 0) {
			mPageNumber = size;
			mPageCount = size;
			return;
		}

		mPageCount = size / PageSize;
		if (size % PageSize > 0) {
			mPageCount++;
		}

		mPagesData = new ArrayList<BusinessArea[]>();

		int index = 0;
		for (int i = 0; i < mPageCount; i++) {
			int pageSize = Math.min(PageSize, size - index);
			BusinessArea[] page = new BusinessArea[pageSize];
			for (int j = 0; j < pageSize; j++) {
				page[j] = items.get(index);
				index++;
			}

			mPagesData.add(page);
		}
	}

	private void loadPrevPage() {
		Log.d(TAG, "loadPrevPage count=" + mPageCount + " number= "
				+ mPageNumber);

		Log.d(TAG, "loadPrevPage");
		mPageNumber--;

		BusinessArea[] items = mPagesData.get(mPageNumber);
		mBusinessAdapter.setDataSet(items);
		mListView.clearChoices();
		mListView.setSelection(items.length);
		mBusinessAdapter.notifyDataSetChanged();
	}

	private void loadNextPage() {
		Log.d(TAG, "loadNextPage count=" + mPageCount + " number= "
				+ mPageNumber);

		mPageNumber++;

		BusinessArea[] items = mPagesData.get(mPageNumber);
		mBusinessAdapter.setDataSet(items);
		mListView.clearChoices();
		mListView.setSelection(0);
		mBusinessAdapter.notifyDataSetChanged();
	}

	private class ListAdapter extends BaseAdapter {

		public class ViewHolder {
			TextView index;
			TextView name;
			TextView phone;
			TextView address;
			TextView time;
		}

		private BusinessArea[] mDataSet = null;

		public ListAdapter() {
		}

		public void setDataSet(BusinessArea[] dataSet) {
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
				convertView = inflater.inflate(R.layout.mypower_business_listitem,
						parent, false);

				holder = new ViewHolder();
				holder.index = (TextView) convertView.findViewById(R.id.index);
				holder.name = (TextView) convertView.findViewById(R.id.name);

				holder.phone = (TextView) convertView.findViewById(R.id.phone);
				holder.address = (TextView) convertView
						.findViewById(R.id.address);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			int index = mPageNumber * PageSize + position;
			holder.index.setText(String.valueOf(index));
			holder.name.setText(mDataSet[position].Name);
			holder.phone.setText(mDataSet[position].Telephone);
			holder.address.setText(mDataSet[position].Address);
			holder.time.setText(mDataSet[position].WorkTime);

			return convertView;
		}
	}
}
