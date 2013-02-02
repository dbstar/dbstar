package com.dbstar.guodian.egine;

import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.guodian.egine.GDClient.Task;
import com.dbstar.model.EventData;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GDEngine {
	private static final String TAG = "GDEngine";
	
	public static final int MSG_CONNECTED = 0x1001;
	public static final int MSG_DISCONNECTED= 0x1002;
	public static final int MSG_CONNECT_ALREADY = 0x1003;
	public static final int MSG_REQUEST_FINISHED = 0x1004;
	
	private static final int STATE_NONE = 0x01 ;
	private static final int STATE_CONNECTING = 0x02 ;
	private static final int STATE_CONNECTED = 0x03 ;
	private static final int STATE_DISCONNECTING = 0x04 ;
	private static final int STATE_DISCONNECTED = 0x5 ;
	
	private static final int LOGIN_ISLOGINGIN = 0x1;
	private static final int LOGIN_ISLOGIN = 0x2;
	private static final int LOGIN_NOTLOGIN = 0x3;

	private static final String UserId = "daotang0104";

	private GDClient mClient = null;
	private Handler mHander = null;
	private Context mContext = null;

	private int mState = STATE_NONE;
	private int mLoginState = LOGIN_NOTLOGIN;
	private GDClientObserver mObserver;
	
	private LoginData mLoginData;
	private String mCtrlNoGuid;
	private String mUserType;
	
	private boolean mIsFirstTimeForDetailBill =  true;

	public GDEngine(Context context) {
		mContext = context;
		mHander = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_CONNECTED: {
					connected();
					break;
				}
				case MSG_DISCONNECTED: {
					disconnected();
					break;
				}
				case MSG_CONNECT_ALREADY: {
					connectAlready();
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

	// this maybe called many times, event the socket is
	// connected already.
	// when called with socket connected already, 
	// connectSuccessed() will not be invoked, so there is no
	// chance to set mIsConnected to true again.
	public void start(String ip, int port, GDClientObserver observer) {
		mObserver = observer;
		
		if (mState != STATE_CONNECTED && mState != STATE_CONNECTING) {
			// not connected
			mClient.setHostAddress(ip, port);
			mClient.connectToServer();
		}
	}

	public void stop() {
		Log.d(TAG, " ===== stop guodian engine ======= ");

		mClient.stop();
	}
	
	public void destroy() {
		mObserver = null;
		mClient.destroy();
	}

	public void requestData(int type, Object args) {
		switch(type) {
		case GDConstract.DATATYPE_POWERPANELDATA: {
			getPowerPanelData();
			break;
		}
		
		case GDConstract.DATATYPE_BILLDETAILOFMONTH: {
			String date = (String) args;
			getBillDetailOfMonth(date);
			break;
		}
		
		case GDConstract.DATATYPE_BILLDETAILOFRECENT: {
			String dateNum = (String) args;
			getBillDetailOfRecent(dateNum);
			break;
		}
		case GDConstract.DATATYPE_BILLMONTHLIST: {
			String yearNum = (String) args;
			getBillMonthList(yearNum);
			break;
		}
		
		case GDConstract.DATATYPE_NOTICES: {
			getNotices();
			break;
		}
		
		case GDConstract.DATATYPE_USERAREAINFO: {
			String areaIdPath = (String) args;
			getUserAreaInfo(areaIdPath);
			break;
		}
		
		case GDConstract.DATATYPE_BUSINESSAREA: {
			String areaId = (String) args;
			getBusinessAreas(areaId);
			break;
		}
		}
	}

	public ElectricityPrice getElecPrice() {
		if (mLoginData != null) {
			return mLoginData.ElecPrice;
		}
		
		return null;
	}
	
	private void getPowerPanelData() {
		Log.d(TAG, " ======== is connected ==== " + (mState == STATE_CONNECTED));
		if (mState == STATE_CONNECTED) {
			mClient.getPowerPanelData(UserId, mCtrlNoGuid, mUserType);
		}
	}
	
	private void getBillDetailOfMonth(String date) {
		if (mState == STATE_CONNECTED) {
			mClient.getBillDetailOfMonth(UserId, mCtrlNoGuid, date);
		}
	}
	
	private void getBillDetailOfRecent(String dateNum) {
		if (mState == STATE_CONNECTED) {
			mClient.getBillDetailOfRecent(UserId, mCtrlNoGuid, dateNum);
		}
	}
	
	private void getBillMonthList(String yearNum) {
		if (mState == STATE_CONNECTED) {
			mClient.getBillMonthList(UserId, mCtrlNoGuid, yearNum);
		}
	}
	
	private void getNotices() {
		if (mState == STATE_CONNECTED) {
			mClient.getNotices(UserId, mCtrlNoGuid);
		}
	}
	
	private void getUserAreaInfo(String areaIdPath) {
		if (mState == STATE_CONNECTED) {
			mClient.getUserAreaInfo(UserId, areaIdPath);
		}
	}
	
	private void getBusinessAreas(String areaId) {
		if (mState == STATE_CONNECTED) {
			mClient.getBusinessArea(UserId, areaId);
		}
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
		
		case GDClient.REQUEST_BILLMONTHLIST: {
			requestFinished(GDConstract.DATATYPE_BILLMONTHLIST, task.ParsedData);
			break;
		}
		
		case GDClient.REQUEST_BILLDETAILOFMONTH: {
			requestFinished(GDConstract.DATATYPE_BILLDETAILOFMONTH, task.ParsedData);
			break;
		}
		case GDClient.REQUEST_BILLDETAILOFRECENT: {
			requestFinished(GDConstract.DATATYPE_BILLDETAILOFRECENT, task.ParsedData);
			break;
		}
		case GDClient.REQUEST_NOTICE: {
			requestFinished(GDConstract.DATATYPE_NOTICES, task.ParsedData);
			break;
		}
		case GDClient.REQUEST_USERAREAINFO: {
			requestFinished(GDConstract.DATATYPE_USERAREAINFO, task.ParsedData);
			break;
		}
		case GDClient.REQUEST_BUSINESSAREA: {
			requestFinished(GDConstract.DATATYPE_BUSINESSAREA, task.ParsedData);
			break;
		}
		}
	}

	private void connected() {
		Log.d(TAG, "======= connectSuccessed=========");
		mState = STATE_CONNECTED;
		
		if (mLoginState != LOGIN_ISLOGIN) {
			mLoginState = LOGIN_ISLOGINGIN;
			mClient.login();
		} else {
			if (mLoginData != null) {
				EventData.GuodianEvent event = new EventData.GuodianEvent();
				event.Type = GDConstract.DATATYPE_LOGIN;
				event.Data = mLoginData;

				notifyEvent(EventData.EVENT_LOGIN_SUCCESSED, event);
			}
		}
	}
	
	private void disconnected() {
		mState = STATE_DISCONNECTED;
	}
	
	private void connectAlready() {
		if (mState != STATE_CONNECTED) {
			mState = STATE_CONNECTED;
		}
		
		if (mLoginState != LOGIN_ISLOGIN && mLoginState != LOGIN_ISLOGINGIN) {
			mLoginState = LOGIN_ISLOGINGIN;
			mClient.login();
		}
	}

	private void loginFinished(LoginData data) {
		Log.d(TAG, "======= loginFinished=========");
		mLoginState = LOGIN_ISLOGIN;
		
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
