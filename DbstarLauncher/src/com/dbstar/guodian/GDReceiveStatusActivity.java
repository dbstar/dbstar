package com.dbstar.guodian;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dbstar.guodian.model.ClientObserver;
import com.dbstar.guodian.model.GDDataProviderService;
import com.dbstar.guodian.model.GDDiskInfo;
import com.dbstar.guodian.model.ReceiveEntry;
import com.dbstar.guodian.util.StringUtil;

public class GDReceiveStatusActivity extends GDBaseActivity {

	private static final String TAG = "GDReceiveStatusActivity";
	private static final int PageSize = 10;

	private static final int UpdatePeriodInMills = 5000;
	private static final int UpdatePeriodInSecs = 5;
	private int mPageNumber = 0;
	private int mPageCount = 0;

	private List<ReceiveEntry[]> mPageDatas = null;
	private DownloadProgressAdapter mAdapter = null;

	private static final int MSG_UPDATEPROGRESS = 0;

	private ClientObserver mObserver = null;

	TextView mPageNumberView;
	ListView mListView;
	TextView mDownloadSpeedView;
	TextView mDiskInfoView;

	private Handler mUIUpdateHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATEPROGRESS: {
				if (mService != null && mBound) {
					mService.getDownloadStatus(mObserver);
				}
				break;
			}
			default:
				break;
			}
		}
	};

	Timer mTimer = new Timer();
	TimerTask mTask = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mObserver = this;

		setContentView(R.layout.download_status_view);

		mPageDatas = new LinkedList<ReceiveEntry[]>();

		initializeView();

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
		
		mTask = new TimerTask() {
			public void run() {
				mUIUpdateHandler.sendEmptyMessage(MSG_UPDATEPROGRESS);
			}
		};
	}

	public void onStart() {
		super.onStart();

		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}

	public void onStop() {
		super.onStop();

		mTimer.cancel();
		mTimer.purge();
	}

	public void onServiceStart() {
		super.onServiceStart();

		mService.startGetTaskInfo();
		mTimer.schedule(mTask, 0, UpdatePeriodInMills);
	}

	public void onServiceStop() {
		super.onServiceStop();

		mService.stopGetTaskInfo();
		mTimer.cancel();
		mTimer.purge();
	}

	private void loadPrevPage() {
		if ((mPageNumber - 1) >= 0) {
			Log.d(TAG, "loadPrevPage");

			mPageNumber--;
			mPageNumberView.setText(formPageText(mPageNumber));

			ReceiveEntry[] entries = mPageDatas.get(mPageNumber);
			mAdapter.setDataSet(entries);
			mListView.clearChoices();
			mListView.setSelection(0);
			mAdapter.notifyDataSetChanged();
		}
	}

	private void loadNextPage() {
		Log.d(TAG, "loadNextPage");

		if (mPageCount == 0)
			return;

		if ((mPageNumber + 1) < mPageCount) {

			mPageNumber++;
			mPageNumberView.setText(formPageText(mPageNumber));

			ReceiveEntry[] entries = mPageDatas.get(mPageNumber);
			mAdapter.setDataSet(entries);
			mListView.clearChoices();
			mListView.setSelection(0);
			mAdapter.notifyDataSetChanged();
		}
	}

	void updateEntryData(ReceiveEntry oldEntry, ReceiveEntry newEntry) {
		oldEntry.RawProgress = newEntry.RawProgress;
		oldEntry.RawTotal = newEntry.RawTotal;
		oldEntry.ConverSize();
	}

	void updatePageData(ReceiveEntry[] pageEntries,
			ArrayList<ReceiveEntry> allEntries) {
		for (int i = 0; i < pageEntries.length; i++) {
			for (int j = 0; j < allEntries.size(); j++) {
				if (pageEntries[i].Id.equals(allEntries.get(j).Id)) {
					updateEntryData(pageEntries[i], allEntries.get(j));
					// remove not used items
					allEntries.remove(j);
				}
			}
		}
	}

	void addNewPageDatas(ArrayList<ReceiveEntry> entries) {
		while (entries.size() > 0) {
			int pageSize = 0;
			if (entries.size() >= PageSize) {
				pageSize = PageSize;
			} else {
				pageSize = entries.size();
			}

			if (pageSize > 0) {
				ReceiveEntry[] newEntries = new ReceiveEntry[pageSize];
				for (int j = 0; j < pageSize; j++) {
					newEntries[j] = entries.get(j);
					entries.remove(j);
				}
				mPageDatas.add(newEntries);
			}
		}
	}

	void updatePagesData(ArrayList<ReceiveEntry> entries) {
		for (int i = 0; i < mPageDatas.size(); i++) {
			ReceiveEntry[] oldEntries = mPageDatas.get(i);
			if (oldEntries.length == PageSize) {
				updatePageData(oldEntries, entries);
			} else {
				// last page is not full
				break;
			}
		}

		if (mPageDatas.get(mPageDatas.size() - 1).length < PageSize) {
			// the old last page is not full, update it
			int pageNumber = mPageDatas.size() - 1;
			ReceiveEntry[] lastEntries = mPageDatas.get(pageNumber);
			mPageDatas.remove(pageNumber);
			updatePageData(lastEntries, entries);

			int pageSize = lastEntries.length + entries.size();
			pageSize = pageSize < PageSize ? pageSize : PageSize;

			ReceiveEntry[] newEntries = new ReceiveEntry[pageSize];
			int i = 0;
			for (i = 0; i < lastEntries.length; i++) {
				newEntries[i] = lastEntries[i];
			}

			if (i < PageSize && newEntries.length == PageSize) {
				for (int j = i; j < newEntries.length; i++) {
					newEntries[j] = entries.get(j);
					entries.remove(j);
				}
			}
			
			addNewPageDatas(entries);

		}

	}
	
	long computeEntriesSize(ReceiveEntry[] entries) {
		long size = 0;
		for(int i=0; i<entries.length ; i++) {
			size += entries[i].RawProgress;
		}
		
		return size;
	}
	
	long computeAllPagesSize(List<ReceiveEntry[]> pages) {
		long size = 0;
		for (int i=0; i<pages.size(); i++) {
			size += computeEntriesSize(pages.get(i));
		}
		return size;
	}

	public void updateData(int type, Object key, Object data) {
		if (type != GDDataProviderService.REQUESTTYPE_GETDOWNLOADSTATUS)
			return;

		ReceiveEntry[] entries = (ReceiveEntry[]) data;

		if (entries != null && entries.length > 0) {

			long preSize = 0;
			long curSize = 0;
			
			preSize = computeAllPagesSize(mPageDatas);
			curSize = computeEntriesSize(entries);
			
			float speed = (float) ((curSize - preSize) / 1024)
					/ (float) UpdatePeriodInSecs;
			String strSpeed = StringUtil.formatFloatValue(speed)
					+ "KB/s";
			mDownloadSpeedView.setText(strSpeed);
			
			ArrayList<ReceiveEntry> entriesList = new ArrayList<ReceiveEntry>();
			for (int i = 0; i < entries.length; i++) {
				entriesList.add(entries[i]);
			}
			
			if (mPageDatas.size() > 0) {
				updatePagesData(entriesList);
			} else {
				addNewPageDatas(entriesList);
			}

			mPageCount = mPageDatas.size();

			// update current page
			mPageNumberView.setText(formPageText(mPageNumber));
			mAdapter.setDataSet(mPageDatas.get(mPageNumber));
			mAdapter.notifyDataSetChanged();

			GDDiskInfo.DiskInfo diskInfo = null;
			if (mBound) {
				diskInfo = GDDiskInfo.getDiskInfo(
						mService.getStorageDisk(), true);
				if (diskInfo != null) {
					String diskSpaceStr = diskInfo.DiskSpace + "/"
							+ diskInfo.DiskSize;
					Log.d(TAG, " disk space = " + diskSpaceStr);
					mDiskInfoView.setText(diskSpaceStr);
				}
			}

		}
	}

	public void initializeView() {
		super.initializeView();

		mDownloadSpeedView = (TextView) findViewById(R.id.download_speed);
		mDiskInfoView = (TextView) findViewById(R.id.disk_info);
		mPageNumberView = (TextView) findViewById(R.id.pageNumberView);
		mAdapter = new DownloadProgressAdapter(this);
		mListView = (ListView) findViewById(R.id.download_view);
		mListView.setAdapter(mAdapter);

		mListView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				boolean ret = false;
				int action = event.getAction();
				if (action == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {

					case KeyEvent.KEYCODE_DPAD_UP: {
						loadPrevPage();
						ret = true;
						break;
					}
					case KeyEvent.KEYCODE_DPAD_DOWN: {
						loadNextPage();
						ret = true;
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

	public class DownloadProgressAdapter extends BaseAdapter {

		ReceiveEntry[] mDataSet = null;

		public void setDataSet(ReceiveEntry[] dataSet) {
			mDataSet = dataSet;
		}

		private class ItemHolder {
			TextView Name;
			ProgressBar ProgressView;
			TextView Progress;
			TextView PercentView;
		}

		public DownloadProgressAdapter(Context context) {
		}

		public int getCount() {
			int size = 0;

			if (mDataSet != null) {
				size = mDataSet.length;
			}

			Log.d(TAG, " DownloadProgressAdapter count = " + size);
			return size;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ItemHolder holder = null;

			if (convertView == null) {
				holder = new ItemHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.download_status_item, null);

				holder.Name = (TextView) convertView
						.findViewById(R.id.text_name);

				holder.ProgressView = (ProgressBar) convertView
						.findViewById(R.id.progress_bar);

				holder.Progress = (TextView) convertView
						.findViewById(R.id.text_progress);

				holder.PercentView = (TextView) convertView
						.findViewById(R.id.text_percent);

				convertView.setTag(holder);
			} else {
				holder = (ItemHolder) convertView.getTag();
			}

			holder.Name.setText(mDataSet[position].Name);

			String strProgress = mDataSet[position].Progress + "/"
					+ mDataSet[position].Total;
			holder.Progress.setText(strProgress);

			holder.ProgressView.setProgress(mDataSet[position].nProgress);

			holder.PercentView.setText(mDataSet[position].Percent);

			return convertView;
		}

	}

}
