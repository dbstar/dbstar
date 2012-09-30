package com.dbstar.guodian;

import java.util.ArrayList;
import java.util.List;

import com.dbstar.guodian.widget.GDAdapterView.OnItemSelectedListener;
import com.dbstar.guodian.widget.GDAdapterView;
import com.dbstar.guodian.widget.GDGridView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GDOrderPushActivity extends GDBaseActivity {

	private static final String TAG = "GDOrderPushActivity";

	private static final int TIMELINE_ITEMS_COUNT = 7;

	GDGridView mTimelineView = null;
	TimelineAdapter mTimelineAdapter = null;
	GDGridView mListView = null;
	ReceiveItemsAdapter mReceiveItemAdapter;
	View mTimelineItemFousedView = null;

	Drawable mTimelineItemIconNormal, mTimelineItemIconFocused,
			mTimelineItemTextFocusedBackground, mReceiveItemLightBackground,
			mReceiveItemDarkBackground, mReceiveItemFocusedBackground,
			mReceiveItemChecked, mReceiveItemUnchecked;

	List<ReceiveTask[]> mTaskPages = null;
	int mTasksPageNumber;
	int mTasksPageCount;
	ReceiveTask mCurrentTask;
	int mTaskIndex = -1, mOldTaskIndex = -1;

	ReceiveItem[] mReceiveItemCurrentPage;
	int mReceiveItemIndex = -1, mOldReceiveItemIndex = -1;

	class ReceiveItem {
		public String Type;
		public String Title;
		public boolean isReceive;

		public ReceiveItem() {
			isReceive = false;
		}
	}

	class ReceiveTask {
		public String Date;

		int ItemsPageNumber;
		int ItemsPageCount;
		public List<ReceiveItem[]> ItemPages;

		public ReceiveTask() {
			ItemPages = null;
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.orderpush_view);

		initializeView();
	}

	protected void initializeView() {
		super.initializeView();

		mTimelineItemIconNormal = getResources().getDrawable(
				R.drawable.timeline_button_normal);
		mTimelineItemIconFocused = getResources().getDrawable(
				R.drawable.timeline_button_focused);
		mTimelineItemTextFocusedBackground = getResources().getDrawable(
				R.drawable.timeline_date_focused);
		mReceiveItemLightBackground = getResources().getDrawable(
				R.drawable.receive_item_light_bg);
		mReceiveItemDarkBackground = getResources().getDrawable(
				R.drawable.receive_item_dark_bg);
		mReceiveItemFocusedBackground = getResources().getDrawable(
				R.drawable.receive_item_focused_bg);
		mReceiveItemChecked = getResources().getDrawable(
				R.drawable.checker_selected);
		mReceiveItemUnchecked = getResources().getDrawable(
				R.drawable.checker_unselected);

		mTimelineView = (GDGridView) findViewById(R.id.timeline);
		mTimelineAdapter = new TimelineAdapter(this);
		mTimelineView.setAdapter(mTimelineAdapter);

		mListView = (GDGridView) findViewById(R.id.receive_list);
		mReceiveItemAdapter = new ReceiveItemsAdapter(this);
		mListView.setAdapter(mReceiveItemAdapter);

		mTimelineView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//TODO: navigate between pages
				return false;
			}
		});

		mTimelineView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(GDAdapterView<?> parent, View view,
					int position, long id) {

				Log.d(TAG, "old mTaskIndex = " + mTaskIndex + " new pos = "
						+ position);

				mOldTaskIndex = mTaskIndex;
				mTaskIndex = position;

				ReceiveTask[] tasks = mTaskPages.get(mTasksPageNumber);
				mCurrentTask = tasks[position];
				mReceiveItemCurrentPage = mCurrentTask.ItemPages
						.get(mCurrentTask.ItemsPageNumber);
				mReceiveItemAdapter.setDataSet(mReceiveItemCurrentPage);
				mReceiveItemAdapter.notifyDataSetChanged();

//				mListView.setSelection(0);
				mListView.invalidate();
			}

			@Override
			public void onNothingSelected(GDAdapterView<?> parent) {

			}

		});

//		mListView.setOnKeyListener(new View.OnKeyListener() {
//
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				//TODO: navigate between pages
//				return false;
//			}
//		});

		mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(GDAdapterView<?> parent, View view,
					int position, long id) {

				if (mReceiveItemIndex >= 0) {
					View oldSel = mListView.getChildAt(mReceiveItemIndex);
					Drawable d = position % 2 == 0 ? mReceiveItemLightBackground
							: mReceiveItemDarkBackground;
					oldSel.setBackgroundDrawable(d);
				}

				mReceiveItemIndex = position;
				view.setBackgroundDrawable(mReceiveItemFocusedBackground);

				mOldReceiveItemIndex = mReceiveItemIndex;
				mReceiveItemIndex = position;
			}

			@Override
			public void onNothingSelected(GDAdapterView<?> parent) {

			}

		});

		mTimelineView.setFocusable(true);
//		mTimelineView.setFocusableInTouchMode(true);
		mTimelineView.requestFocus();

		mListView.setFocusable(true);
//		mListView.setFocusableInTouchMode(true);
		mListView.setOnKeyListener(mReceiveItemsKeyListener);
	}

	public void onServiceStart() {
		super.onServiceStart();

		initTestData();

		ReceiveTask[] tasks = mTaskPages.get(mTasksPageNumber);
		// Log.d(TAG, " tasks size " + tasks.length);
		mTimelineAdapter.setDataSet(tasks);
		mTimelineAdapter.notifyDataSetChanged();

		mTimelineView.setSelection(0);
	}

	View.OnKeyListener mReceiveItemsKeyListener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			int action = event.getAction();
			if (action == KeyEvent.ACTION_DOWN) {
				Log.d(TAG, " ---- key code =  " + keyCode);
				switch (keyCode) {
				case 82: // just for test on emulator
				case KeyEvent.KEYCODE_ENTER:
				case KeyEvent.KEYCODE_DPAD_CENTER: {
					ReceiveItem item = mReceiveItemCurrentPage[mReceiveItemIndex];
					if (item.isReceive) {
						item.isReceive = false;
					} else {
						item.isReceive = true;
					}

					mReceiveItemAdapter.notifyDataSetChanged();
					return true;
				}
				}
			}
			return false;
		}
	};

	private class TimelineAdapter extends BaseAdapter {

		private ReceiveTask[] mDataSet = null;

		public class ViewHolder {
			TextView timeView;
			ImageView iconView;
		}

		public TimelineAdapter(Context context) {
		}

		public void setDataSet(ReceiveTask[] dataSet) {
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

			ViewHolder holder = new ViewHolder();
			if (position == mTimelineView.getSelectedItemPosition()) {
				if (mTimelineItemFousedView == null) {
					mTimelineItemFousedView = getLayoutInflater().inflate(
							R.layout.timeline_item_focused, parent, false);

					holder.timeView = (TextView) mTimelineItemFousedView
							.findViewById(R.id.time_view);
					holder.iconView = (ImageView) mTimelineItemFousedView
							.findViewById(R.id.icon);
					mTimelineItemFousedView.setTag(holder);
				}
				convertView = mTimelineItemFousedView;
			} else {
				if (convertView == mTimelineItemFousedView) {
					convertView = null;
				}
			}

			if (null == convertView) {
				convertView = getLayoutInflater().inflate(
						R.layout.timeline_item, parent, false);
				holder.timeView = (TextView) convertView
						.findViewById(R.id.time_view);
				holder.iconView = (ImageView) convertView
						.findViewById(R.id.icon);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.timeView.setText(mDataSet[position].Date);

			return convertView;
		}
	}

	private class ReceiveItemsAdapter extends BaseAdapter {

		private ReceiveItem[] mDataSet = null;

		public class ViewHolder {
			TextView typeView;
			TextView titleView;
			ImageView checkerView;
		}

		public ReceiveItemsAdapter(Context context) {
		}

		public void setDataSet(ReceiveItem[] dataSet) {
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

			ViewHolder holder = new ViewHolder();

			if (null == convertView) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.receive_item, parent,
						false);

				holder.typeView = (TextView) convertView
						.findViewById(R.id.type_view);
				holder.titleView = (TextView) convertView
						.findViewById(R.id.title_view);
				holder.checkerView = (ImageView) convertView
						.findViewById(R.id.checker_view);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.typeView.setText(mDataSet[position].Type);
			holder.titleView.setText(mDataSet[position].Title);
			Drawable d = mDataSet[position].isReceive ? mReceiveItemChecked
					: mReceiveItemUnchecked;
			holder.checkerView.setImageDrawable(d);
//			 Log.d(TAG, "get view position = " + position
//			 + " mReceiveItemIndex " + mReceiveItemIndex + " checked = " + mDataSet[position].isReceive);
			 
			if (position == mListView.getSelectedItemPosition()) {
				convertView.setBackgroundDrawable(mReceiveItemFocusedBackground);
				holder.typeView.setTextColor(Color.WHITE);
				holder.titleView.setTextColor(Color.WHITE);
			} else {
				holder.typeView.setTextColor(Color.BLACK);
				holder.titleView.setTextColor(Color.BLACK);
				if (position % 2 == 0) {
					convertView
							.setBackgroundDrawable(mReceiveItemLightBackground);
				} else {
					convertView
							.setBackgroundDrawable(mReceiveItemDarkBackground);
				}
			}
			return convertView;
		}
	}

	void initTestData() {
		ReceiveItem item = null;
		ReceiveItem[] items = new ReceiveItem[8];

		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "变形金刚";
		items[0] = item;

		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "机械师";
		items[1] = item;
		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "冰河世纪4";
		items[2] = item;
		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "听风者";
		items[3] = item;
		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "画皮2";
		items[4] = item;
		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "蝙蝠侠前传3";
		items[5] = item;
		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "黑衣人3";
		items[6] = item;
		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "泰坦尼克号";
		items[7] = item;

		ReceiveItem[] items2 = new ReceiveItem[8];

		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "普罗米修斯";
		items2[0] = item;

		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "普罗米修斯";
		items2[1] = item;
		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "普罗米修斯";
		items2[2] = item;
		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "普罗米修斯";
		items2[3] = item;
		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "普罗米修斯";
		items2[4] = item;
		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "普罗米修斯";
		items2[5] = item;
		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "普罗米修斯";
		items2[6] = item;
		item = new ReceiveItem();
		item.Type = "电影";
		item.Title = "普罗米修斯";
		items2[7] = item;

		ReceiveTask task = null;
		ReceiveTask[] tasks = null;

		tasks = new ReceiveTask[5];

		task = new ReceiveTask();
		task.Date = "5月1号";
		task.ItemsPageCount = 1;
		task.ItemsPageNumber = 0;
		task.ItemPages = new ArrayList<ReceiveItem[]>();
		task.ItemPages.add(items);
		tasks[0] = task;

		task = new ReceiveTask();
		task.Date = "6月1号";
		task.ItemsPageCount = 1;
		task.ItemsPageNumber = 0;
		task.ItemPages = new ArrayList<ReceiveItem[]>();
		task.ItemPages.add(items2);
		tasks[1] = task;

		task = new ReceiveTask();
		task.Date = "7月1号";
		task.ItemsPageCount = 1;
		task.ItemsPageNumber = 0;
		task.ItemPages = new ArrayList<ReceiveItem[]>();
		task.ItemPages.add(items);
		tasks[2] = task;

		task = new ReceiveTask();
		task.Date = "8月1号";
		task.ItemsPageCount = 1;
		task.ItemsPageNumber = 0;
		task.ItemPages = new ArrayList<ReceiveItem[]>();
		task.ItemPages.add(items2);
		tasks[3] = task;

		task = new ReceiveTask();
		task.Date = "9月1号";
		task.ItemsPageCount = 1;
		task.ItemsPageNumber = 0;
		task.ItemPages = new ArrayList<ReceiveItem[]>();
		task.ItemPages.add(items);
		tasks[4] = task;

		mTaskPages = new ArrayList<ReceiveTask[]>();
		mTaskPages.add(tasks);

		mTasksPageNumber = 0;
		mTasksPageCount = 1;
		// mCurrentTask = tasks;
	}
}
