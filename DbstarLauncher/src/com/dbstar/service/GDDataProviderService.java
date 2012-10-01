package com.dbstar.service;


import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dbstar.util.GDNetworkUtil;
import com.dbstar.model.ColumnData;
import com.dbstar.model.ContentData;
import com.dbstar.model.EntityObject;
import com.dbstar.model.GDCommon;
import com.dbstar.model.GDDataAccessor;
import com.dbstar.model.GDDataModel;
import com.dbstar.model.GDNetModel;
import com.dbstar.model.ReceiveEntry;
import com.dbstar.model.TV;
import com.dbstar.model.TV.EpisodeItem;
import com.dbstar.service.client.GDDBStarClient;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Binder;
import android.os.Message;
import android.util.Log;
import android.os.Process;

public class GDDataProviderService extends Service {

	private static final String TAG = "GDDataProviderService";

	public static final int REQUESTTYPE_GETCOLUMNS = 1;
	public static final int REQUESTTYPE_GETCONTENTS = 2;
	public static final int REQUESTTYPE_GETCONTENTSCOUNT = 3;
	public static final int REQUESTTYPE_GETDESCRIPTION = 4;
	public static final int REQUESTTYPE_GETIMAGE = 5;
	public static final int REQUESTTYPE_GETDETAILSDATA = 6;
	public static final int REQUESTTYPE_GETDOWNLOADSTATUS = 7;

	public static final int REQUESTTYPE_GETPOWERCONSUMPTION = 8;
	public static final int REQUESTTYPE_GETTOTALCOSTBYCHARGETYPE = 9;
	public static final int REQUESTTYPE_GETWEATHER = 10;

	public static final int REQUESTTYPE_SETSETTINGS = 11;
	public static final int REQUESTTYPE_GETSETTINGS = 12;

	public static final int REQUESTTYPE_GETALLPUBLICATIONS = 13;
	public static final int REQUESTTYPE_GETTVDATA = 14;
	public static final int REQUESTTYPE_STARTGETTASKINFO = 15;
	public static final int REQUESTTYPE_STOPGETTASKINFO = 16;

	private static final String PARAMETER_COLUMN_ID = "column_id";
	private static final String PARAMETER_PAGENUMBER = "page_number";
	private static final String PARAMETER_PAGESIZE = "page_size";
	private static final String PARAMETER_CONTENTDATA = "content_data";

	private static final String PARAMETER_CCID = "cc_id";
	private static final String PARAMETER_DATESTART = "date_start";
	private static final String PARAMETER_DATEEND = "date_end";
	private static final String PARAMETER_CHAREGTYPE = "charge_type";

	private static final String PARAMETER_KEY = "key";
	private static final String PARAMETER_VALUE = "value";

	private Object mTaskQueueLock = new Object();
	private LinkedList<RequestTask> mTaskQueue = null;

	private Object mFinishedTaskQueueLock = new Object();
	private LinkedList<RequestTask> mFinishedTaskQueue = null;

	private GDDataModel mModel = null;
	private GDNetModel mNetModel = null;
	GDDataAccessor mDataAccessor = new GDDataAccessor();
	ConnectivityManager mConnectManager;
	GDDiskSpaceMonitor mDiskMonitor;

	GDDBStarClient mDBStarClient;
	GDApplicationObserver mApplicationObserver = null;

	public void registerAppObserver(GDApplicationObserver observer) {
		mApplicationObserver = observer;
	}

	public void unRegisterAppObserver(GDApplicationObserver observer) {
		if (mApplicationObserver == observer) {
			mApplicationObserver = null;
		}
	}

	String mMacAddress = "";

	private int mThreadCount = 2;
	private List<WorkerThread> mThreadPool = new LinkedList<WorkerThread>();

	private int mMainThreadId;
	private int mMainThreadPriority;

	private final IBinder mBinder = new DataProviderBinder();

	public class DataProviderBinder extends Binder {
		public GDDataProviderService getService() {
			return GDDataProviderService.this;
		}
	}

	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");

		mMainThreadId = Process.myTid();
		mMainThreadPriority = Process.getThreadPriority(mMainThreadId);
		Log.d(TAG, "main thread id " + mMainThreadId + " priority "
				+ mMainThreadPriority);

		mDataAccessor.configure();

		mDiskMonitor = new GDDiskSpaceMonitor(mHandler);
		String disk = mDataAccessor.getStorageDisk();
		Log.d(TAG, "monitor disk " + disk);
		if (!disk.isEmpty()) {
			mDiskMonitor.addDiskToMonitor(disk);
		}

		mDiskMonitor.startMonitor();

		mModel = new GDDataModel(this);
		mModel.initialize();
		// set localization
		mModel.setLocalization(GDCommon.LangCN);

		mNetModel = new GDNetModel(this);
		mNetModel.initialize();

		mTaskQueue = new LinkedList<RequestTask>();
		mFinishedTaskQueue = new LinkedList<RequestTask>();

		for (int i = 0; i < mThreadCount; i++) {
			WorkerThread thread = new WorkerThread();
			thread.start();

			mThreadPool.add(thread);
		}

		registerUSBReceiver();
		reqisterConnectReceiver();

		mNetworkIsReady = isNetworkConnected();
		Log.d(TAG, "network is connected " + mNetworkIsReady);
		
		mDBStarClient = new GDDBStarClient(this);
		mDBStarClient.start();
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");

		mDBStarClient.stop();
		mDiskMonitor.stopMonitor();

		mModel.deInitialize();

		unregisterReceiver(mNetworkReceiver);
		unregisterReceiver(mUSBReceiver);

		cancelAllRequests();

		for (int i = 0; i < mThreadCount; i++) {
			WorkerThread thread = mThreadPool.get(i);
			thread.setExit(true);
		}

		synchronized (mTaskQueueLock) {
			mTaskQueueLock.notifyAll();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	boolean mDiskIsReady = false;
	boolean mNetworkIsReady = false;

//	private static final String DVBPrepertyName = "dbstar.dvbpush.started";
	private static final String SmartHomePrepertyName = "dbstar.smarthome.started";

	private void startDbStarService() {
		Log.d(TAG, "++++++++++++++++++startDbStarService++++++++++++++++++++");
		SharedPreferences settings = null;
		SharedPreferences.Editor editor = null;
//		settings = getSharedPreferences(DVBPrepertyName, 0);
//		SharedPreferences.Editor editor = settings.edit();
//		editor.putInt(DVBPrepertyName, 1);
//		editor.commit();

		settings = getSharedPreferences(SmartHomePrepertyName, 0);
		editor = settings.edit();
		editor.putInt(SmartHomePrepertyName, 1);
		editor.commit();
		
		mDBStarClient.startDvbpush();
	}

	private void stopDbStarService() {
		Log.d(TAG, "stopDbStarService");

		SharedPreferences settings = null;
		SharedPreferences.Editor editor = null;
		
//		settings = getSharedPreferences(DVBPrepertyName, 0);
//		editor = settings.edit();
//		editor.putInt(DVBPrepertyName, 0);
//		editor.commit();

		settings = getSharedPreferences(SmartHomePrepertyName, 0);
		editor = settings.edit();
		editor.putInt(SmartHomePrepertyName, 0);
		editor.commit();
		
		mDBStarClient.stopDvbpush();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GDCommon.MSG_TASK_FINISHED: {
				RequestTask task = dequeueFinishedTask();

				if (task != null) {
					handleTaskFinished(task);
				}
				break;
			}

			case GDCommon.MSG_MEDIA_MOUNTED: {
				Bundle data = msg.getData();
				String disk = data.getString("disk");
				// Log.d(TAG, "mount storage = " + disk);
				Log.d(TAG, " ++++++++++++++++ mount storage +++++++++++++++ " + disk);

				String storage = mDataAccessor.getStorageDisk();

				if (storage.equals("")) {
					mDataAccessor.configure();
					storage = mDataAccessor.getStorageDisk();
					if (storage.equals("")) {
						break;
					}
				}
				
				if (disk.equals(storage) && mApplicationObserver != null) {
					mDiskIsReady = true;
					mDataAccessor.configure();
					mApplicationObserver.initializeApp();

//					disk = mDataAccessor.getStorageDisk();
					Log.d(TAG, " +++++++++++ monitor disk ++++++++" + disk);
					mDiskMonitor.removeDiskFromMonitor(disk);
					mDiskMonitor.addDiskToMonitor(disk);
					mDiskMonitor.startMonitor();

					if (mDiskIsReady && mNetworkIsReady) {
						startDbStarService();
					}
				}
				break;
			}
			case GDCommon.MSG_MEDIA_REMOVED:
				break;

			case GDCommon.MSG_NETWORK_CONNECT:
				getMacAddress();
				Log.d(TAG,
						" ++++++++++++++++ network connected +++++++++++++++");
				mNetworkIsReady = true;
				if (mDiskIsReady && mNetworkIsReady) {
					startDbStarService();
				}

				break;
			case GDCommon.MSG_NETWORK_DISCONNECT:
				Log.d(TAG,
						" ++++++++++++++++ network disconnected +++++++++++++++");
				mNetworkIsReady = false;
				stopDbStarService();
				break;

			case GDCommon.MSG_DISK_SPACEWARNING: {
				Bundle data = msg.getData();
				String disk = (String) data.get(GDCommon.KeyDisk);
				if (mApplicationObserver != null) {
					mApplicationObserver.handleNotifiy(
							GDCommon.MSG_DISK_SPACEWARNING, disk);
				}
				break;
			}

			default:
				break;
			}
		}

	};

	private void handleTaskFinished(RequestTask task) {
		Log.d(TAG, "handleTaskFinished type [" + task.Type + "]");

		switch (task.Type) {
		case REQUESTTYPE_GETCOLUMNS: {
			if (task.Observer != null) {
				task.Observer.updateData(task.Type, task.ColumnLevel,
						task.Index, task.Data);
			}
			break;
		}

		case REQUESTTYPE_GETALLPUBLICATIONS: {
			if (task.Observer != null) {
				task.Observer.updateData(task.Type, null, task.Data);
			}
			break;
		}

		case REQUESTTYPE_GETTVDATA: {
			if (task.Observer != null) {
				task.Observer.updateData(task.Type, null, task.Data);
			}
			break;
		}

		case REQUESTTYPE_GETCONTENTSCOUNT: {
			if (task.Observer != null) {
				task.Observer.updateData(task.Type, null, task.Data);
			}
			break;
		}
		case REQUESTTYPE_GETCONTENTS: {
			if (task.Observer != null) {
				task.Observer.updateData(task.Type, task.PageNumber,
						task.PageSize, task.Data);
			}
			break;
		}

		case REQUESTTYPE_GETDETAILSDATA:
		case REQUESTTYPE_GETDESCRIPTION:
		case REQUESTTYPE_GETIMAGE: {
			if (task.Observer != null) {
				task.Observer.updateData(task.Type, task.PageNumber,
						task.Index, task.Data);
			}
			break;
		}

		case REQUESTTYPE_GETDOWNLOADSTATUS: {
			if (task.Observer != null) {
//				task.Observer.updateData(task.Type, task.PageNumber,
//						task.PageSize, task.Data);
				task.Observer.updateData(task.Type, null, task.Data);
			}
			break;
		}

		case REQUESTTYPE_GETTOTALCOSTBYCHARGETYPE:
		case REQUESTTYPE_GETPOWERCONSUMPTION: {
			if (task.Observer != null) {
				task.Observer.updateData(task.Type, 0, 0, task.Data);
			}
			break;
		}

		case REQUESTTYPE_GETSETTINGS: {
			if (task.Observer != null) {
				task.Observer.updateData(task.Type, task.Key, task.Data);
			}
			break;
		}

		case REQUESTTYPE_GETWEATHER: {
			if (task.Observer != null) {
				task.Observer.updateData(task.Type, task.Key, task.Data);
			}
			break;
		}
		default:
			break;
		}
	}

	private class RequestTask {
		public static final int INVALID = 0;
		public static final int ACTIVE = 1;

		long Id;
		int Type;
		int Flag;

		ClientObserver Observer;
		int Index;

		int PageNumber;
		int PageSize;

		int ColumnLevel;

		Map<String, Object> Parameters;

		Object Key;
		Object Data;

		public RequestTask() {
			Flag = ACTIVE;
		}
	};

	private void enqueueTask(RequestTask task) {
		synchronized (mTaskQueueLock) {
			mTaskQueue.add(task);
			if (mTaskQueue.size() == 1) {
				mTaskQueueLock.notifyAll();
			}
		}
	}

	private RequestTask dequeueTask() {
		synchronized (mTaskQueueLock) {

			RequestTask task = mTaskQueue.poll();

			if (task != null && task.Flag == RequestTask.INVALID) {
				task = null;
			}

			return task;
		}
	}

	private void enqueueFinishedTask(RequestTask task) {

		if (task != null && task.Flag == RequestTask.ACTIVE) {
			synchronized (mFinishedTaskQueueLock) {
				mFinishedTaskQueue.add(task);
			}

			mHandler.sendEmptyMessage(GDCommon.MSG_TASK_FINISHED);
		} else {
			Log.d(TAG, "taskFinished : invalide task, dropped!");
		}
	}

	private RequestTask dequeueFinishedTask() {

		synchronized (mFinishedTaskQueueLock) {
			RequestTask task = mFinishedTaskQueue.poll();

			if (task != null && task.Flag == RequestTask.INVALID) {
				task = null;
			}

			return task;
		}
	}

	private class WorkerThread extends Thread {

		private int mThreadId = -1;
		private int mThreadPriority = -100;
		private Object mProcessingTaskLock = new Object();
		private Object mExitLock = new Object();
		private RequestTask mProcessingTask;

		public boolean mExit = false;

		public void cancelProcessingTask(ClientObserver observer) {
			synchronized (mProcessingTaskLock) {
				if (mProcessingTask != null) {
					if (mProcessingTask.Observer == observer) {
						mProcessingTask.Flag = RequestTask.INVALID;
					}
				}
			}
		}

		public void cancelAllProcessingTask() {
			synchronized (mProcessingTaskLock) {
				if (mProcessingTask != null) {
					mProcessingTask.Flag = RequestTask.INVALID;
				}
			}
		}

		private void setProcessingTask(RequestTask task) {
			synchronized (mProcessingTaskLock) {
				mProcessingTask = task;
			}
		}

		private void taskFinished(RequestTask task) {
			Log.d(TAG, "Task [" + task.Id + "] Finished - Thread Id ["
					+ mThreadId + "]");

			enqueueFinishedTask(task);
			setProcessingTask(null);
		}

		private boolean checkExit() {
			synchronized (mExitLock) {
				return mExit;
			}
		}

		public void setExit(boolean exit) {
			synchronized (mExitLock) {
				mExit = exit;
			}
		}

		public void run() {
			mThreadId = Process.myTid();
			mThreadPriority = Process.getThreadPriority(mThreadId);
			Log.d(TAG, "Worker Thread [" + mThreadId + "] Priority ["
					+ mThreadPriority + "]");

			Process.setThreadPriority(Process.THREAD_PRIORITY_LESS_FAVORABLE);
			mThreadPriority = Process.getThreadPriority(mThreadId);
			Log.d(TAG, "Worker Thread [" + mThreadId + "] Priority ["
					+ mThreadPriority + "]");

			while (true) {
				Log.d(TAG, "@@@ 1 Thread [" + mThreadId + "]-- Begin Run");

				if (checkExit()) {
					break;
				}

				synchronized (mTaskQueueLock) {
					if (mTaskQueue.size() == 0) {
						try {
							mTaskQueueLock.wait();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				RequestTask task = dequeueTask();

				if (task == null) {
					// in this case, we call notify and try to wake up this
					// thread to let it exit.
					continue;
				}

				setProcessingTask(task);

				switch (task.Type) {
				case REQUESTTYPE_GETCOLUMNS: {
					Object value = null;
					value = task.Parameters.get(PARAMETER_COLUMN_ID);
					String columnId = String.valueOf(value);

					ColumnData[] coloumns = mModel.getColumns(columnId);

					for (int i = 0; coloumns != null && i < coloumns.length; i++) {
						ColumnData column = coloumns[i];
						String iconRootPath = mDataAccessor.getIconRootDir();
						coloumns[i].IconNormal = mModel.getImage(iconRootPath
								+ "/" + column.IconNormalPath);
						coloumns[i].IconFocused = mModel.getImage(iconRootPath
								+ "/" + column.IconFocusedPath);
						// coloumns[i].IconClicked =
						// mModel.getImage(iconRootPath + "/" +
						// column.IconClickedPath);
					}

					task.Data = coloumns;

					taskFinished(task);
					break;
				}

				case REQUESTTYPE_GETALLPUBLICATIONS: {
					Object value = null;
					value = task.Parameters.get(PARAMETER_COLUMN_ID);
					String columnId = String.valueOf(value);

					ContentData[] datas = mModel.getReadyPublications(columnId);
					task.Data = datas;

					taskFinished(task);
					break;
				}

				case REQUESTTYPE_GETTVDATA: {
					Object value = null;
					value = task.Parameters.get(PARAMETER_COLUMN_ID);
					String columnId = String.valueOf(value);

					EntityObject[] entities = mModel.getAllEntities(columnId);

					TV[] tvs = new TV[entities.length];
					for (int i = 0; i < entities.length; i++) {
						tvs[i] = mModel.getTVData(entities[i].Id);
						TV.EpisodeItem[] items = tvs[i].Episodes;
						for (int j = 0; j < items.length; j++) {
							ContentData content = new ContentData();
							content.XMLFilePath = items[j].Url + GDDataModel.DefaultDesFile;
							String xmlFile = getDetailsDataFile(content);
							mModel.getDetailsData(xmlFile, content);
							Log.d(TAG, "xmlFile " + xmlFile);
							Log.d(TAG, "content "+ content.MainFile);
							items[j].Content = content;
						}
					}
					task.Data = tvs;

					taskFinished(task);
					break;
				}

				case REQUESTTYPE_GETCONTENTSCOUNT: {
					Object value = null;
					value = task.Parameters.get(PARAMETER_COLUMN_ID);
					String columnId = String.valueOf(value);

					int count = mModel.getContentsCount(columnId);
					task.Data = new Integer(count);

					taskFinished(task);
					break;
				}

				case REQUESTTYPE_GETCONTENTS: {
					Object value = null;
					value = task.Parameters.get(PARAMETER_COLUMN_ID);
					String columnId = String.valueOf(value);
					value = task.Parameters.get(PARAMETER_PAGENUMBER);
					int pageNumber = ((Integer) value).intValue();
					value = task.Parameters.get(PARAMETER_PAGESIZE);
					int pageSize = ((Integer) value).intValue();

					ContentData[] contents = mModel.getContents(columnId,
							pageNumber, pageSize);
					task.Data = contents;

					taskFinished(task);
					break;
				}

				case REQUESTTYPE_GETDETAILSDATA: {
					Object value = null;
					value = task.Parameters.get(PARAMETER_CONTENTDATA);
					ContentData content = (ContentData) value;
					String xmlFile = getDetailsDataFile(content);
					mModel.getDetailsData(xmlFile, content);
					task.Data = content;

					taskFinished(task);
					break;
				}

				case REQUESTTYPE_GETDESCRIPTION: {
					Object value = null;
					value = task.Parameters.get(PARAMETER_CONTENTDATA);
					ContentData content = (ContentData) value;
					String file = getDescritpionFile(content);

					String text = mModel.getTextContent(file);
					task.Data = text;

					taskFinished(task);
					break;
				}

				case REQUESTTYPE_GETIMAGE: {
					Object value = null;
					value = task.Parameters.get(PARAMETER_CONTENTDATA);
					ContentData content = (ContentData) value;
					String file = getThumbnailFile(content);

					Bitmap image = mModel.getImage(file);
					task.Data = image;

					taskFinished(task);
					break;
				}
				
				case REQUESTTYPE_STARTGETTASKINFO: {
					mDBStarClient.startTaskInfo();
					break;
				}
				
				case REQUESTTYPE_STOPGETTASKINFO: {
					mDBStarClient.stopTaskInfo();
					break;
				}

				case REQUESTTYPE_GETDOWNLOADSTATUS: {
					ReceiveEntry[] entries = mDBStarClient.getTaskInfo(); 
//							mModel.getDownloadStatus(
//							task.PageNumber, task.PageSize);
					task.Data = entries;
					taskFinished(task);
					break;
				}

				case REQUESTTYPE_GETPOWERCONSUMPTION: {
					Object value = null;
					value = task.Parameters.get(PARAMETER_CCID);
					String cc_id = (String) value;
					value = task.Parameters.get(PARAMETER_DATESTART);
					String date_start = (String) value;
					value = task.Parameters.get(PARAMETER_DATEEND);
					String date_end = (String) value;

					String powerConsumption = mNetModel.getPowerConsumption(
							cc_id, date_start, date_end);
					task.Data = powerConsumption;

					taskFinished(task);
					break;
				}

				case REQUESTTYPE_GETTOTALCOSTBYCHARGETYPE: {
					Object value = null;
					value = task.Parameters.get(PARAMETER_CCID);
					String cc_id = (String) value;
					value = task.Parameters.get(PARAMETER_DATESTART);
					String date_start = (String) value;
					value = task.Parameters.get(PARAMETER_DATEEND);
					String date_end = (String) value;
					value = task.Parameters.get(PARAMETER_CHAREGTYPE);
					String charge_type = (String) value;

					String totalCost = mNetModel.getTotalCostByChargeType(
							cc_id, date_start, date_end, charge_type);
					task.Data = totalCost;

					taskFinished(task);
					break;
				}

				case REQUESTTYPE_GETSETTINGS: {
					task.Data = mModel.getSettingValue((String) task.Key);
					taskFinished(task);
					break;
				}

				case REQUESTTYPE_SETSETTINGS: {
					Object value = null;
					value = task.Parameters.get(PARAMETER_KEY);
					String key = (String) value;
					value = task.Parameters.get(PARAMETER_VALUE);
					String sValue = (String) value;

					boolean ret = mModel.setSettingValue(key, sValue);
					break;
				}

				default:
					break;
				}
			}

			Log.d(TAG, "Thread [" + mThreadId + "] exit!");
		}
	};

	public void getColumns(ClientObserver observer, int level, int index,
			String columnId) {
		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Observer = observer;
		task.Type = REQUESTTYPE_GETCOLUMNS;
		task.Parameters = new HashMap<String, Object>();
		task.Parameters.put(PARAMETER_COLUMN_ID, columnId);
		task.Index = index;
		task.ColumnLevel = level;
		enqueueTask(task);
	}

	public void getAllPublications(ClientObserver observer, String columnId) {
		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Observer = observer;
		task.Type = REQUESTTYPE_GETALLPUBLICATIONS;
		task.Parameters = new HashMap<String, Object>();
		task.Parameters.put(PARAMETER_COLUMN_ID, columnId);

		enqueueTask(task);
	}

	public void getTVData(ClientObserver observer, String columnId) {
		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Observer = observer;
		task.Type = REQUESTTYPE_GETTVDATA;
		task.Parameters = new HashMap<String, Object>();
		task.Parameters.put(PARAMETER_COLUMN_ID, columnId);

		enqueueTask(task);
	}

	public void getContentsCount(ClientObserver observer, String columnId) {
		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Observer = observer;
		task.Type = REQUESTTYPE_GETCONTENTSCOUNT;
		task.Parameters = new HashMap<String, Object>();
		task.Parameters.put(PARAMETER_COLUMN_ID, columnId);

		enqueueTask(task);
	}

	public void getContents(ClientObserver observer, String columnId,
			int pageNumber, int pageSize) {
		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Observer = observer;
		task.Type = REQUESTTYPE_GETCONTENTS;
		task.Parameters = new HashMap<String, Object>();
		task.Parameters.put(PARAMETER_COLUMN_ID, columnId);
		task.Parameters.put(PARAMETER_PAGENUMBER, pageNumber);
		task.Parameters.put(PARAMETER_PAGESIZE, pageSize);
		task.PageNumber = pageNumber;
		task.PageSize = pageSize;

		enqueueTask(task);
	}

	public void getDescription(ClientObserver observer, int pageNumber,
			int index, ContentData content) {
		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Observer = observer;
		task.Type = REQUESTTYPE_GETDESCRIPTION;
		task.PageNumber = pageNumber;
		task.Index = index;
		task.Parameters = new HashMap<String, Object>();
		task.Parameters.put(PARAMETER_CONTENTDATA, content);
		enqueueTask(task);
	}

	public void getDetailsData(ClientObserver observer, int pageNumber,
			int index, ContentData content) {
		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Observer = observer;
		task.Type = REQUESTTYPE_GETDETAILSDATA;
		task.PageNumber = pageNumber;
		task.Index = index;
		task.Parameters = new HashMap<String, Object>();
		task.Parameters.put(PARAMETER_CONTENTDATA, content);
		enqueueTask(task);
	}

	public void getImage(ClientObserver observer, int pageNumber, int index,
			ContentData content) {
		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Observer = observer;
		task.Type = REQUESTTYPE_GETIMAGE;
		task.PageNumber = pageNumber;
		task.Index = index;
		task.Parameters = new HashMap<String, Object>();
		task.Parameters.put(PARAMETER_CONTENTDATA, content);
		enqueueTask(task);
	}

	public void getDownloadStatus(ClientObserver observer) {
		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Observer = observer;
		task.Type = REQUESTTYPE_GETDOWNLOADSTATUS;

//		task.PageNumber = pageNumber;
//		task.PageSize = pageSize;
		enqueueTask(task);
	}

	public void getPowerConsumption(ClientObserver observer, String cc_id,
			String date_start, String date_end) {
		if (!mNetworkIsReady)
			return;

		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Observer = observer;
		task.Type = REQUESTTYPE_GETPOWERCONSUMPTION;

		task.Parameters = new HashMap<String, Object>();
		task.Parameters.put(PARAMETER_CCID, cc_id);
		task.Parameters.put(PARAMETER_DATESTART, date_start);
		task.Parameters.put(PARAMETER_DATEEND, date_end);

		enqueueTask(task);
	}

	public void getTotalCostByChargeType(ClientObserver observer, String cc_id,
			String date_start, String date_end, String charge_type) {
		if (!mNetworkIsReady)
			return;

		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Observer = observer;
		task.Type = REQUESTTYPE_GETTOTALCOSTBYCHARGETYPE;

		task.Parameters = new HashMap<String, Object>();
		task.Parameters.put(PARAMETER_CCID, cc_id);
		task.Parameters.put(PARAMETER_DATESTART, date_start);
		task.Parameters.put(PARAMETER_DATEEND, date_end);
		task.Parameters.put(PARAMETER_CHAREGTYPE, charge_type);

		enqueueTask(task);
	}

	public void getSettingsValue(ClientObserver observer, String key) {
		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Observer = observer;
		task.Type = REQUESTTYPE_GETSETTINGS;
		task.Key = key;

		enqueueTask(task);
	}

	public void setSettingsValue(String key, String value) {
		RequestTask task = new RequestTask();
		task.Id = System.currentTimeMillis();
		task.Type = REQUESTTYPE_SETSETTINGS;

		task.Parameters = new HashMap<String, Object>();
		task.Parameters.put(PARAMETER_KEY, key);
		task.Parameters.put(PARAMETER_VALUE, value);

		enqueueTask(task);
	}

	public void startGetTaskInfo() {

		RequestTask task = new RequestTask();
		task.Observer = null;
		task.Id = System.currentTimeMillis();
		task.Type = REQUESTTYPE_STARTGETTASKINFO;

		enqueueTask(task);
	}
	
	public void stopGetTaskInfo() {
		RequestTask task = new RequestTask();
		task.Observer = null;
		task.Id = System.currentTimeMillis();
		task.Type = REQUESTTYPE_STOPGETTASKINFO;

		enqueueTask(task);
	}
	
	public void getWeatherData(ClientObserver observer, String location) {

		if (!mNetworkIsReady)
			return;

		RequestTask task = new RequestTask();
		task.Observer = observer;
		task.Id = System.currentTimeMillis();
		task.Type = REQUESTTYPE_GETWEATHER;
		task.Key = location;

		enqueueTask(task);
	}

	private String getThumbnailFile(ContentData content) {
		return mDataAccessor.getThumbnailFile(content);
	}

	private String getDescritpionFile(ContentData content) {
		return mDataAccessor.getDescritpionFile(content);
	}

	private String getDetailsDataFile(ContentData content) {
		return mDataAccessor.getDetailsDataFile(content);
	}

	public String getMediaFile(ContentData content) {
		return mDataAccessor.getMediaFile(content);
	}

	public String getEBookFile(String category) {
		return mDataAccessor.getEbookFile(category);
	}

	public String getStorageDisk() {
		return mDataAccessor.getStorageDisk();
	}

	// cancel the requests from this observer
	public void cancelRequests(ClientObserver observer) {
		synchronized (mTaskQueueLock) {
			for (int i = 0; i < mTaskQueue.size(); i++) {
				RequestTask task = mTaskQueue.get(i);
				if (task.Observer == observer) {
					task.Flag = RequestTask.INVALID;
				}
			}
		}

		for (int i = 0; i < mThreadPool.size(); i++) {
			WorkerThread thread = mThreadPool.get(i);
			thread.cancelProcessingTask(observer);
		}

		synchronized (mFinishedTaskQueueLock) {
			for (int i = 0; i < mFinishedTaskQueue.size(); i++) {
				RequestTask task = mFinishedTaskQueue.get(i);
				if (task.Observer == observer) {
					task.Flag = RequestTask.INVALID;
				}
			}
		}
	}

	public void cancelAllRequests() {
		Log.d(TAG, "cancelAllRequests!");

		synchronized (mTaskQueueLock) {
			mTaskQueue.clear();
		}

		for (int i = 0; i < mThreadPool.size(); i++) {
			WorkerThread thread = mThreadPool.get(i);
			thread.cancelAllProcessingTask();
		}

		synchronized (mFinishedTaskQueueLock) {
			mFinishedTaskQueue.clear();
		}
	}

	public String getCategoryContent(String category) {
		return mDataAccessor.getCategoryContent(category);
	}

	public String getDemoMovie() {
		String file = mDataAccessor.getDemoMovie();
		Log.d(TAG, "getDemoMovie file =" + file);
		File f = new File(file);
		if (f == null || !f.exists()) {
			file = "";
		}
		return file;
	}

	public String getDemoPic() {
		String file = mDataAccessor.getDemoPic();
		Log.d(TAG, "getDemoPic file =" + file);
		File f = new File(file);
		if (f == null || !f.exists()) {
			file = "";
		}
		return file;
	}

	public String getHomePage() {
		return mDataAccessor.getHomePage();
	}

	public void getPushedMessage(List<String> retMessages) {
		mDataAccessor.getPushedMessage(retMessages);
	}

	public String getMacAddress() {

		if (mMacAddress.equals("")) {
			mMacAddress = GDNetworkUtil.getMacAddress(this, mConnectManager);
		}

		return mMacAddress;
	}

	public boolean isNetworkConnected() {
		NetworkInfo networkInfo = mConnectManager.getActiveNetworkInfo();

		return networkInfo != null && networkInfo.isConnected();
	}

	private void registerUSBReceiver() {
		IntentFilter usbFilter = new IntentFilter();
		/* receive USB status change messages */
		usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		usbFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		// usbFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		usbFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		usbFilter.addDataScheme("file");

		registerReceiver(mUSBReceiver, usbFilter);
	}

	private void reqisterConnectReceiver() {
		mConnectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		IntentFilter networkFilter = new IntentFilter();
		/* receive connection change messages */
		networkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mNetworkReceiver, networkFilter);
	}

	private BroadcastReceiver mUSBReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Uri uri = intent.getData();
			Log.d(TAG, "---- USB device " + action);
			Log.d(TAG, "---- URI:" + uri.toString());

			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				Message message = mHandler
						.obtainMessage(GDCommon.MSG_MEDIA_MOUNTED);
				Bundle data = new Bundle();
				data.putString("disk", uri.getPath());
				message.setData(data);

				mHandler.sendMessage(message);
			} else if (action.equals(Intent.ACTION_MEDIA_REMOVED)
					|| action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)
			/* ||action.equals(Intent.ACTION_MEDIA_UNMOUNTED) */) {

				Message message = mHandler
						.obtainMessage(GDCommon.MSG_MEDIA_REMOVED);
				Bundle data = new Bundle();
				data.putString("disk", uri.getPath());
				message.setData(data);

				mHandler.sendMessage(message);
			}
		}

	};

	private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
				return;

			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

			Log.d(TAG, "noConnectivity = " + noConnectivity);
			if (noConnectivity) {
				// There are no connected networks at all
				mHandler.sendEmptyMessage(GDCommon.MSG_NETWORK_DISCONNECT);
				return;
			}

			// case 1: attempting to connect to another network, just wait for
			// another broadcast
			// case 2: connected
			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (networkInfo != null) {
				Log.d(TAG, "getTypeName() = " + networkInfo.getTypeName());
				Log.d(TAG, "isConnected() = " + networkInfo.isConnected());

				if (networkInfo.isConnected()) {
					mHandler.sendEmptyMessage(GDCommon.MSG_NETWORK_CONNECT);
				}
			}
		}

	};

}
