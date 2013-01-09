package com.dbstar.guodian;

import com.dbstar.guodian.GDClient.Task;
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.model.EventData;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GDEngine {
	private static final String TAG = "GDEngine";
	
	public static final int MSG_CONNECT_SUCCESSED = 0x1001;
	public static final int MSG_REQUEST_FINISHED = 0x1002;

	private static final String UserId = "daotang0104";

	private GDClient mClient = null;
	private Handler mHander = null;
	private Context mContext = null;

	private boolean mIsConnected = false;
	private GDClientObserver mObserver;
	
	private LoginData mLoginData;
	private String mCtrlNoGuid;
	private String mUserType;

	public GDEngine(Context context) {
		mContext = context;
		mHander = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_CONNECT_SUCCESSED: {
					connectSuccessed();
					break;
				}
				case MSG_REQUEST_FINISHED: {
					handleFinishedRequest((Task) msg.obj);
					break;
				}
				}
			}
		};

		mClient = new GDClient(context, mHander);
	}

	public void start(String ip, int port, GDClientObserver observer) {
		mObserver = observer;
		mIsConnected = false;
		mClient.setHostAddress(ip, port);
		mClient.connectToServer();
	}

	public void stop() {
		mClient.stop();
	}
	
	public void destroy() {
		mObserver = null;
		mClient.destroy();
	}

	public void requestData(int type) {
		switch(type) {
		case GDConstract.DATATYPE_POWERPANELDATA: {
			getPowerPanelData();
			break;
		}
		}
	}
	
	private void getPowerPanelData() {
		mClient.getPowerPanelData(mCtrlNoGuid, UserId, mUserType);
	}

	private void handleFinishedRequest(Task task) {
		int requestType = task.TaskType;
		switch (requestType) {
		case GDClient.REQUEST_LOGIN: {
			loginFinished((LoginData) task.ParsedData);
			break;
		}
		
		case GDClient.REQUEST_POWERPANELDATA: {
			requestFinished(GDConstract.DATATYPE_POWERPANELDATA, task.ParsedData);
			break;
		}
		}
	}

	private void connectSuccessed() {
		Log.d(TAG, "======= connectSuccessed=========");
		mIsConnected = true;
		mClient.login();
	}

	private void loginFinished(LoginData data) {
		Log.d(TAG, "======= loginFinished=========");
		
		mLoginData = data;
		
		if (data != null) {
			if (data.CtrlNo != null) {
				mCtrlNoGuid = data.CtrlNo.CtrlNoGuid;
			}
			
			if (data.UserData != null && data.UserData.UserInfo != null) {
				mUserType = data.UserData.UserInfo.UserType;
			}
		}
		
		EventData.GuodianEvent event = new EventData.GuodianEvent();
		event.Type = GDConstract.DATATYPE_LOGIN;
		event.Data = data;

		notifyEvent(EventData.EVENT_LOGIN_SUCCESSED, event);
	}
	
	private void requestFinished(int type, Object data) {
		EventData.GuodianEvent event = new EventData.GuodianEvent();
		event.Type = type;
		event.Data = data;
		notifyEvent(EventData.EVENT_GUODIAN_DATA, event);
	}

	private void notifyEvent(int type, Object event) {
		if (mObserver != null) {
			mObserver.notifyEvent(type, event);
		}
	}
}
