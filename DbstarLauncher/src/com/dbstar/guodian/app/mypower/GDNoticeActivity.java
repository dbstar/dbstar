package com.dbstar.guodian.app.mypower;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.guodian.egine.GDConstract;
import com.dbstar.model.EventData;
import com.dbstar.guodian.data.Notice;

public class GDNoticeActivity extends GDBaseActivity {

	private static final String TAG = "GDNoticeActivity";

	private static final int MODE_LIST = 0;
	private static final int MODE_DETAIL = 1;

	private static final int PageSize = 8;
	private ArrayList<Notice[]> mPagesData;
	private int mPageCount, mPageNumber;

	private ListView mListView;
	private ListAdapter mNoticesAdapter;
	private int mViewMode;
	private ViewGroup mDetailContainer;
	private TextView mTitle, mContent;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mypower_noticesview);

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);

		initializeView();

		if (mMenuPath != null) {
			String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
			showMenuPath(menuArray);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {
			if (mViewMode == MODE_DETAIL) {
				showDetail(false);
				return true;
			}
			break;
		}
		}

		return super.onKeyDown(keyCode, event);
	}

	public void initializeView() {
		super.initializeView();

		mViewMode = MODE_LIST;

		mListView = (ListView) findViewById(R.id.listview);
		mNoticesAdapter = new ListAdapter();
		mListView.setAdapter(mNoticesAdapter);

		mDetailContainer = (ViewGroup) findViewById(R.id.detail);
		mTitle = (TextView) findViewById(R.id.title);
		mContent = (TextView) findViewById(R.id.content);
		mContent.setMovementMethod(new ScrollingMovementMethod());

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				showDetail(true);
			}

		});

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
						if (selectedIndex == PageSize
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

	private void loadPrevPage() {
		Log.d(TAG, "loadPrevPage count=" + mPageCount + " number= "
				+ mPageNumber);

		Log.d(TAG, "loadPrevPage");
		mPageNumber--;

		Notice[] notices = mPagesData.get(mPageNumber);
		mNoticesAdapter.setDataSet(notices);
		mListView.clearChoices();
		mListView.setSelection(notices.length);
		mNoticesAdapter.notifyDataSetChanged();
	}

	private void loadNextPage() {
		Log.d(TAG, "loadNextPage count=" + mPageCount + " number= "
				+ mPageNumber);

		mPageNumber++;

		Notice[] notices = mPagesData.get(mPageNumber);
		mNoticesAdapter.setDataSet(notices);
		mListView.clearChoices();
		mListView.setSelection(0);
		mNoticesAdapter.notifyDataSetChanged();
	}

	protected void onServiceStart() {
		super.onServiceStart();
		Log.d(TAG, "onServiceStart");

		mService.requestPowerData(GDConstract.DATATYPE_NOTICES, null);
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

		if (type == GDConstract.DATATYPE_NOTICES) {
			ArrayList<Notice> notices = (ArrayList<Notice>) data;
			constructPages(notices);

			displayPage(mPageNumber);
		}
	}

	private void constructPages(ArrayList<Notice> notices) {
		int size = notices.size();
		if (size == 0) {
			mPageNumber = size;
			mPageCount = size;
			return;
		}

		mPageCount = size / PageSize;
		if (size % PageSize > 0) {
			mPageCount++;
		}

		mPagesData = new ArrayList<Notice[]>();

		int index = 0;
		for (int i = 0; i < mPageCount; i++) {
			int pageSize = Math.min(PageSize, size - index);

			Notice[] page = new Notice[pageSize];
			for (int j = 0; j < pageSize; j++) {
				page[j] = notices.get(index);
				index++;
			}

			mPagesData.add(page);
		}
	}

	private void displayPage(int pageNumber) {
		Notice[] page = mPagesData.get(pageNumber);
		mNoticesAdapter.setDataSet(page);
		mNoticesAdapter.notifyDataSetChanged();
	}

	private void showDetail(boolean show) {
		if (show) {

			int index = mListView.getSelectedItemPosition();
			Notice[] page = mPagesData.get(mPageNumber);
			Notice notice = page[index];

			mTitle.setText(notice.Title);
			mContent.setText(notice.Content);

			mListView.setVisibility(View.GONE);
			mDetailContainer.setVisibility(View.VISIBLE);
			mViewMode = MODE_DETAIL;

			mDetailContainer.requestLayout();
		} else {
			mListView.setVisibility(View.VISIBLE);
			mDetailContainer.setVisibility(View.GONE);
			mViewMode = MODE_LIST;
		}
	}

	private class ListAdapter extends BaseAdapter {

		public class ViewHolder {
			TextView index;
			TextView title;
			TextView date;
		}

		private Notice[] mDataSet = null;

		public ListAdapter() {
		}

		public void setDataSet(Notice[] dataSet) {
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
				convertView = inflater.inflate(R.layout.noticelist_item,
						parent, false);

				holder = new ViewHolder();
				holder.index = (TextView) convertView.findViewById(R.id.index);
				holder.title = (TextView) convertView.findViewById(R.id.title);

				holder.date = (TextView) convertView.findViewById(R.id.date);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			int index = mPageNumber * PageSize + position;
			holder.index.setText(String.valueOf(index));
			holder.title.setText(mDataSet[position].Title);
			holder.date.setText(mDataSet[position].Date);

			return convertView;
		}
	}
}
