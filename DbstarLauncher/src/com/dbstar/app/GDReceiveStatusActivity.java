package com.dbstar.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.model.EventData;
import com.dbstar.model.GDDiskInfo;
import com.dbstar.model.ReceiveData;
import com.dbstar.model.ReceiveEntry;
import com.dbstar.service.ClientObserver;
import com.dbstar.service.GDDataProviderService;
import com.dbstar.util.ImageUtil;
import com.dbstar.util.LogUtil;
import com.dbstar.util.StringUtil;

public class GDReceiveStatusActivity extends GDBaseActivity {

	private static final String TAG = "GDReceiveStatusActivity";
	private static final int PageSize = 9;

	private static final int UpdatePeriodInMills = 5000;
	private static final int UpdatePeriodInSecs = 5;
	private int mPageNumber = 0;
	private int mPageCount = 0;

	private List<ReceiveEntry[]> mPageDatas = null;
	private DownloadProgressAdapter mAdapter = null;

	private static final int MSG_UPDATEPROGRESS = 0;

	private static final int SignalStateNone = -1;
	private static final int SignalStateOff = 0;
	private static final int SignalStateOn = 1;

	private int mSignalState = SignalStateNone;

	private ClientObserver mObserver = null;

	ListView mListView;
	TextView mDownloadSpeedView;
	TextView mDiskInfoView;
	TextView mPageItemsView;
	TextView mPageNumberView;
	TextView mSignalStatusView;

	String mStatusWaitting, mStatusDownloading, mStatusFinished, mStatusFailed;
	String mTextDi, mTextYe, mTextGong, mTextTiao;

	Drawable mReceiveItemLightBackground, mReceiveItemDarkBackground;

	Timer mTimer = null;
	TimerTask mTask = null;
	
	private Bitmap mBitmap;

	private Handler mUIUpdateHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATEPROGRESS: {
				if (mBound) {
					mService.getDownloadStatus(mObserver);
					//mService.getTSSignalStatus(mObserver);
				}
				break;
			}
			default:
				break;
			}
		}
	};

	private void callTask() {
		mUIUpdateHandler.sendEmptyMessage(MSG_UPDATEPROGRESS);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mObserver = this;

		setContentView(R.layout.download_status_view);

		mPageDatas = new LinkedList<ReceiveEntry[]>();

		initializeView();

		// Intent intent = getIntent();
		// mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		// showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));

		mTimer = new Timer();
	}

	public void onStart() {
		super.onStart();

		// showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
		if (mBound) {
			startUpdateTask();
		}
	}

	public void onStop() {
		super.onStop();

		stopUpdateTask();

		mService.stopGetTaskInfo();
	}

	public void onDestroy() {
		super.onDestroy();

		mTimer.cancel();
		
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
	}

	public void onServiceStart() {
		super.onServiceStart();

		mService.startGetTaskInfo();

		startUpdateTask();

		updateDiskStatus();
	}

	public void onServiceStop() {
		super.onServiceStop();

		stopUpdateTask();
	}

	void startUpdateTask() {
		if (mTask != null) {
			mTask.cancel();
		}

		mTask = new TimerTask() {
			public void run() {
				callTask();
			}
		};

		mTimer.schedule(mTask, 1000, UpdatePeriodInMills);
	}

	void stopUpdateTask() {
		if (mTask == null)
			return;

		mTask.cancel();
		mTask = null;
	}

	private void loadPrevPage() {
		LogUtil.d(TAG, "loadPrevPage count=" + mPageCount + " number= "
				+ mPageNumber);
		
		if ((mPageNumber - 1) >= 0) {
			LogUtil.d(TAG, "loadPrevPage");

			mPageNumber--;
			// mPageNumberView.setText(formPageText(mPageNumber));
			updatePageInfoView();

			ReceiveEntry[] entries = mPageDatas.get(mPageNumber);
			mAdapter.setDataSet(entries);
			mListView.clearChoices();
			mListView.setSelection(0);
			mAdapter.notifyDataSetChanged();
		}
	}

	private void loadNextPage() {
		LogUtil.d(TAG, "loadNextPage count=" + mPageCount + " number= "
				+ mPageNumber);

		if (mPageCount == 0)
			return;

		if ((mPageNumber + 1) < mPageCount) {

			mPageNumber++;
			// mPageNumberView.setText(formPageText(mPageNumber));
			updatePageInfoView();

			ReceiveEntry[] entries = mPageDatas.get(mPageNumber);
			mAdapter.setDataSet(entries);
			mListView.clearChoices();
			mListView.setSelection(0);
			mAdapter.notifyDataSetChanged();
		}
	}

	// void updateEntryData(ReceiveEntry oldEntry, ReceiveEntry newEntry) {
	// oldEntry.RawProgress = newEntry.RawProgress;
	// oldEntry.RawTotal = newEntry.RawTotal;
	// oldEntry.ConvertSize();
	// }

	void updatePageData(ReceiveEntry[] pageEntries,
			ArrayList<ReceiveEntry> allEntries) {

		LogUtil.d(TAG, "update page size=" + pageEntries.length + " total = "
				+ allEntries.size());

		for (int i = 0; i < pageEntries.length; i++) {
			int j = 0;
			int size = allEntries.size();
			while (j < size) {
				ReceiveEntry entry = allEntries.get(j);
				if (pageEntries[i].Id.equals(entry.Id)) {
					pageEntries[i] = entry;
					allEntries.remove(j);
					break;
				} else {
					j++;
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
					newEntries[j] = entries.get(0);
					entries.remove(0);
				}
				LogUtil.d(TAG, "add page size " + newEntries.length);
				mPageDatas.add(newEntries);
			}
		}
	}

	void updatePagesData(ArrayList<ReceiveEntry> entries) {
		LogUtil.d(TAG, "=== updatePagesData == start");
		long startTime = System.currentTimeMillis();

		int size = mPageDatas.size();
		for (int i = 0; i < size; i++) {
			ReceiveEntry[] oldEntries = mPageDatas.get(i);
			if (oldEntries.length == PageSize) {
				updatePageData(oldEntries, entries);
			} else {
				// last page is not full
				break;
			}
		}

		int lastPageNumber = mPageDatas.size() - 1;
		ReceiveEntry[] lastEntries = mPageDatas.get(lastPageNumber);
		
		if (lastEntries.length < PageSize) {
			// the old last page is not full, update it
			mPageDatas.remove(lastPageNumber);
			updatePageData(lastEntries, entries);

			ReceiveEntry[] newEntries = null;
			if (entries.size() > 0) {
				int pageSize = lastEntries.length + entries.size();
				pageSize = pageSize < PageSize ? pageSize : PageSize;

				newEntries = new ReceiveEntry[pageSize];
				int i = 0;
				for (i = 0; i < lastEntries.length; i++) {
					newEntries[i] = lastEntries[i];
				}

				for (; i < newEntries.length; i++) {
					newEntries[i] = entries.get(0);
					entries.remove(0);
				}
			} else {
				newEntries = lastEntries;
			}

			mPageDatas.add(newEntries);
		}
		
		if (entries.size() > 0) {
			addNewPageDatas(entries);
		}
		
		long endTime = System.currentTimeMillis();

		LogUtil.d(TAG, "=== updatePagesData == end " + (endTime - startTime));
	}

	long computeEntriesSize(ReceiveEntry[] entries) {
		long size = 0;
		for (int i = 0; i < entries.length; i++) {
			size += entries[i].RawProgress;
		}

		return size;
	}

	long computeAllPagesSize(List<ReceiveEntry[]> pages) {
		long size = 0;
		for (int i = 0; i < pages.size(); i++) {
			size += computeEntriesSize(pages.get(i));
		}
		return size;
	}

	public void updateData(int type, Object key, Object data) {
		if (type == GDDataProviderService.REQUESTTYPE_GETDOWNLOADSTATUS) {
		    ReceiveData receiveData = (ReceiveData) data;
			updateDownloadStatus(receiveData);
			
			if(receiveData != null){
			    String status = receiveData.SingleStatu;
				if(status == null)
					return;
	            mSignalState = status.equalsIgnoreCase("1") ? SignalStateOn
	                    : SignalStateOff;

	            setSignalState(mSignalState);
			}
			
		} else if (type == GDDataProviderService.REQUESTTYPE_GETTSSIGNALSTATUS) {
//			if (data == null)
//				return;
//
//			String status = (String) data;
//			mSignalState = status.equalsIgnoreCase("1") ? SignalStateOn
//					: SignalStateOff;
//
//			setSignalState(mSignalState);
		}

	}

	private long mCurSize = 0;
	
	public void updateDownloadStatus(ReceiveData data) {
		if (data == null)
			return;
		boolean newData = data.NewData;
		LogUtil.i(TAG, "newData = " + newData);
		ReceiveEntry[] entries = data.Entries;
		if (newData) {
			mPageDatas.clear();
			mPageNumber = 0;
			mPageCount = 0;
			mAdapter.setDataSet(null);
            mAdapter.notifyDataSetChanged();

            updatePageInfoView();
		}

		if (entries != null && entries.length > 0) {

			float speed = 0;
			if (mPageDatas.size() > 0) {

				long preSize = mCurSize;
				long curSize = 0;

				//TODO: this may cause overflow of long
//				preSize = computeAllPagesSize(mPageDatas);
				mCurSize = computeEntriesSize(entries);
				curSize = mCurSize;
				LogUtil.d(TAG, "preSize=" + preSize + " curSize=" + curSize);

				speed = (float) ((curSize - preSize) / 128)
						/ (float) UpdatePeriodInSecs;
			} else {
				mCurSize = computeEntriesSize(entries);
			}

			String strSpeed = StringUtil.formatFloatValue(speed) + "Kb/s";
			mDownloadSpeedView.setText(strSpeed);

			ArrayList<ReceiveEntry> entriesList = new ArrayList<ReceiveEntry>();
			for (int i = 0; i < entries.length; i++) {
				entriesList.add(entries[i]);
			}

			// LogUtil.d(TAG, "1 page size=" + mPageDatas.size() + " entry size="
			// + entriesList.size());
			if (mPageDatas.size() > 0) {
				updatePagesData(entriesList);
			} else {
				addNewPageDatas(entriesList);
			}

			mPageCount = mPageDatas.size();

			if (mPageNumber > (mPageCount - 1)) {
				mPageNumber = mPageCount - 1;
			}

			// update current page
			// mPageNumberView.setText(formPageText(mPageNumber));
			mAdapter.setDataSet(mPageDatas.get(mPageNumber));
			mAdapter.notifyDataSetChanged();

			updateDiskStatus();
			updatePageInfoView();
		}
	}

	private void updateDiskStatus() {
		GDDiskInfo.DiskInfo diskInfo = null;
		if (mBound) {
			String disk = mService.getStorageDisk();
			if (disk == null || disk.isEmpty())
				return;

			diskInfo = GDDiskInfo.getDiskInfo(disk, true);
			if (diskInfo != null) {
				// String diskSpaceStr = diskInfo.DiskSpace + "/"
				// + diskInfo.DiskSize;
				// LogUtil.d(TAG, " disk space = " + diskSpaceStr);
				mDiskInfoView.setText(diskInfo.DiskSpace);
			}
		}
	}

	void updatePageInfoView() {
		int pageNumber = mPageCount > 0 ? (mPageNumber + 1) : 0;
		String pagesInfo = mTextDi + pageNumber + mTextYe;
		pagesInfo += "/" + mTextGong + (mPageCount) + mTextYe;

		mPageNumberView.setText(pagesInfo);

		int startItem = 0;
		int endItem = 0;
		int totalItems = 0;

		if (mPageCount > 0) {
			startItem = mPageNumber * PageSize + 1;
			endItem = startItem + mPageDatas.get(mPageNumber).length - 1;
			totalItems = (mPageCount - 1) * PageSize;
			totalItems += mPageDatas.get(mPageCount - 1).length;
		}

		String itemsInfo = mTextDi + startItem + "~" + endItem + mTextTiao;
		itemsInfo += "/" + mTextGong + totalItems + mTextTiao;
		mPageItemsView.setText(itemsInfo);
	}

	@Override
	public void notifyEvent(int type, Object event) {
		super.notifyEvent(type, event);

		if (type == EventData.EVENT_DATASIGNAL) {
			EventData.DataSignalEvent signalEvent = (EventData.DataSignalEvent) event;

			mSignalState = signalEvent.hasSignal ? SignalStateOn
					: SignalStateOff;

			setSignalState(mSignalState);
		}

	}

	void setSignalState(int state) {
		String stateStr = "";
		switch (state) {
		case SignalStateOn: {
			stateStr = this.getResources().getString(
					R.string.receivestatus_status_hassignal);
			break;
		}
		case SignalStateOff: {
			stateStr = this.getResources().getString(
					R.string.receivestatus_status_nosignal);
			break;
		}
		default: {
			break;
		}
		}

		if (mSignalStatusView != null) {
			mSignalStatusView.setText(stateStr);
		}
	}

	public void initializeView() {
		// super.initializeView();

		mReceiveItemLightBackground = getResources().getDrawable(
				R.drawable.listitem_light_bg);
		mReceiveItemDarkBackground = getResources().getDrawable(
				R.drawable.listitem_dark_bg);

		mStatusWaitting = getResources().getString(
				R.string.receivestatus_status_waitting);
		mStatusDownloading = getResources().getString(
				R.string.receivestatus_status_downloading);
		mStatusFinished = getResources().getString(
				R.string.receivestatus_status_finished);

		mStatusFailed = getResources().getString(
				R.string.receivestatus_status_failed);

		mTextDi = getResources().getString(R.string.text_di);
		mTextYe = getResources().getString(R.string.text_ye);
		mTextGong = getResources().getString(R.string.text_gong);
		mTextTiao = getResources().getString(R.string.text_tiao);

		RelativeLayout mContainer = (RelativeLayout) findViewById(R.id.download_status_view_container);
		mDownloadSpeedView = (TextView) findViewById(R.id.download_speed);
		mSignalStatusView = (TextView) findViewById(R.id.signal_status);
		mDiskInfoView = (TextView) findViewById(R.id.disk_info);
		mPageNumberView = (TextView) findViewById(R.id.download_pages);
		mPageItemsView = (TextView) findViewById(R.id.download_items);

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
		
		HashMap<String, Bitmap> bitmaps = ImageUtil.parserXmlAndLoadPic(false, false, true);
		
		if (bitmaps != null && bitmaps.containsKey(ImageUtil.App_Key)) {
			mBitmap = bitmaps.get(ImageUtil.App_Key);
			if (mBitmap != null) 
				mContainer.setBackgroundDrawable(new BitmapDrawable(mBitmap));						
			else 
				mContainer.setBackgroundResource(R.drawable.view_background);							
		} else
			mContainer.setBackgroundResource(R.drawable.view_background);			
	}

	public class DownloadProgressAdapter extends BaseAdapter {

		ReceiveEntry[] mDataSet = null;

		public void setDataSet(ReceiveEntry[] dataSet) {
			mDataSet = dataSet;
		}

		private class ItemHolder {
			TextView Name;
			ProgressBar ProgressView;
			TextView Status;
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

			// LogUtil.d(TAG, " DownloadProgressAdapter count = " + size);
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
						R.layout.download_status_item, parent, false);

				holder.Name = (TextView) convertView
						.findViewById(R.id.text_name);

				holder.ProgressView = (ProgressBar) convertView
						.findViewById(R.id.progress_bar);

				holder.Status = (TextView) convertView
						.findViewById(R.id.text_status);

				holder.Progress = (TextView) convertView
						.findViewById(R.id.text_progress);

				holder.PercentView = (TextView) convertView
						.findViewById(R.id.text_percent);

				convertView.setTag(holder);
			} else {
				holder = (ItemHolder) convertView.getTag();
			}

			holder.Name.setText(mDataSet[position].Name);

			holder.ProgressView.setProgress(mDataSet[position].nProgress);

			String status = null;
			long rawProgress = mDataSet[position].RawProgress;
			if (rawProgress <= 0) {
				status = mStatusWaitting;
			} else {
				if (rawProgress == mDataSet[position].RawTotal) {
					status = mStatusFinished;
				} else {
					status = mStatusDownloading;
				}
			}

			holder.Status.setText(status);

			String strProgress = mDataSet[position].Progress + "/"
					+ mDataSet[position].Total;
			holder.Progress.setText(strProgress);

			holder.PercentView.setText(mDataSet[position].Percent);

			if (position % 2 == 0) {
				convertView.setBackgroundDrawable(mReceiveItemLightBackground);
			} else {
				convertView.setBackgroundDrawable(mReceiveItemDarkBackground);
			}

			return convertView;
		}
	}

}
