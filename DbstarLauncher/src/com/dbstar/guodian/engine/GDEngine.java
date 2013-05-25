package com.dbstar.guodian.engine;

import java.util.Map;

import com.dbstar.guodian.data.EPCConstitute;
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.engine.GDClient.Task;
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
	public static final int MSG_CONNECT_FAILED = 0x1004;
	public static final int MSG_REQUEST_FINISHED = 0x1005;
	public static final int MSG_REQUEST_ERROR = 0x1006;
	public static final int MSG_SOCKET_ERROR = 0x1007;
	
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
	private EPCConstitute mEleDimension;
	private String mCtrlNoGuid;
	private String mUserType;
	private String mUserId;
	private long mReconnectTime;
	
	private static final int REPEATLOGIN_COUNT = 20;
	private int mRepeatLoginCount = 0;

	public GDEngine(Context context) {
		mContext = context;
		mHander = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_CONNECTED: {
					handleConnected();
					break;
				}
				case MSG_CONNECT_ALREADY: {
					handleConnectedAlready();
					break;
				}
				case MSG_REQUEST_FINISHED: {
					handleFinishedRequest((Task) msg.obj);
					break;
				}
				case MSG_REQUEST_ERROR: {
					handleRequestError(msg.arg1, (Task) msg.obj);
					break;
				}
				
				case MSG_CONNECT_FAILED: {
					handleConnectFailed();
					break;
				}
				case MSG_SOCKET_ERROR: {
					handleSocketError();
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
			mState = STATE_CONNECTING;
			mClient.setHostAddress(ip, port);
			mClient.connectToServer();
		}
	}
	
	public void setReconnectTime (long time) {
		mReconnectTime = time;
	}
	
	public void restart() {
		Log.d(TAG, " == restart == ");

		mState = STATE_NONE;
		mLoginState = LOGIN_NOTLOGIN;
		
		mClient.connectToServerDelayed(mReconnectTime);
	}

	public void stop() {
		Log.d(TAG, " ===== stop guodian engine ======= ");
		mState = STATE_DISCONNECTED;
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
		case GDConstract.DATATYPE_CITYES:
		    String pid = (String) args;
		    getCitys(pid);
		    break;
		case GDConstract.DATATYPE_ZONES:
		    String cid = (String) args;
            getZones(cid);
		    break;
		case GDConstract.DATATYPE_ELECTRICAL_POWER_CONSUMPTION_CONSTITUTE:
		    Map<String, String> params = (Map<String, String>) args;
		    getElecDimension(params);
		case GDConstract.DATATYPE_PAYMENT_RECORDS:
		    getPaymentRecords((Map<String, String>)args);
		    break;
		case GDConstract.DATATYPE_YREAR_FEE_DETAIL:
		    getYearFeeDetail((Map<String, String>)args);
		    break;
		    
		case GDConstract.DATATYPE_FAMILY_POWER_EFFICENCY:
		    getFamilyPowerEfficency((Map<String, String>)args);
		    break;
		    
		case GDConstract.DATATYPE_STEP_POWER_CONSUMPTION_CONSTITUTE:
		    getSPCConstitute((Map<String, String>)args);
		    break;
		case GDConstract.DATATYPE_PERIOD_POWER_CONSUMPTION_CONSTITUTE:
            getPPCConstitute((Map<String, String>)args);
            break;
            
		case GDConstract.DATATYPE_STEP_POWER_CONSUMPTION_TRACK:
		    getStepPowerTrack((Map<String, String>)args);
		    break;
		    
		case GDConstract.DATATYPE_EQUMENTLIST:
		    getEqumentList((Map<String, String>)args);
		    break;
		    
		case GDConstract.DATATYPE_POWER_CONSUMPTION_TREND:
		    getPowerConsumptionTrend((Map<String, String>)args);
		    break;
		case GDConstract.DATATYPE_POWER_TIPS:
		    getPowerTips();
		    break;
		    
		case GDConstract.DATATYPE_ROOM_LIST:
		    getRoomList((Map<String, String>)args);
		    break;
		    
		case GDConstract.DATATYPE_ROOM_ELECTRICAL_LIST:
		    getRoomElectricalList((Map<String, String>)args);
            break;  
		case GDConstract.DATATYPE_TUNN_ON_OFF_ELECTRICAL:
		    turnOnOrOffElectrical((Map<String, String>)args);
		    break;
		case GDConstract.DATATYPE_REFRESH_ELECTRICAL:
		    refreshElectrical((Map<String, String>)args);
		    break;
		    
		case GDConstract.DATATYPE_MODEL_LIST:
		    getModelList((Map<String, String>)args);
		    break;
		case GDConstract.DATATYPE_MODEL_ELECTRICAL_LIST:
		    getModelElectricalList((Map<String, String>)args);
		    break;
		case GDConstract.DATATYPE_EXECUTE_MODE:
		    executeMode((Map<String, String>)args);
		    break;
		case GDConstract.DATATYPE_TIMED_TASK_LIST:
            getTimedTaskList((Map<String, String>)args);
            break;
		case GDConstract.DATATYPE_NO_TASK_ELCTRICAL_LIST:
            getNoTaskElectricalList((Map<String, String>)args);
            break;
		case GDConstract.DATATYPE_ADD_TIMED_TASK:
		    addTimeDTask((Map<String, String>)args);
		    break;
		case GDConstract.DATATYPE_MODIFY_TIMED_TASK:
		    modifyTimeDTask((Map<String, String>)args);
		    break;
		case GDConstract.DATATYPE_DELETE_TIMED_TASK:
		    deleteTimeDTask((Map<String, String>)args);
		    break;
		    
		case GDConstract.DATATYPE_EXECUTE_TIMED_TASK:
		    executeTimeDTask((Map<String, String>)args);
		    break;
		    
		case GDConstract.DATATYPE_DEFAULT_POWER_TARGET:
		    getDefaultPowerTarget((Map<String, String>)args);
		    break;
		case GDConstract.DATATYPE_POWER_TARGET:
		    getPowerTarget((Map<String, String>)args);
            break;
		case GDConstract.DATATYPE_SETTING_POWER_TARGET:
		    setPowerTarget((Map<String, String>)args);
		    break;
		}
	}


    public ElectricityPrice getElecPrice() {
		if (mLoginData != null) {
			return mLoginData.ElecPrice;
		}
		
		return null;
	}
    public LoginData getLoginData() {
        if (mLoginData != null) {
            return mLoginData;
        }
        
        return null;
    }
    public EPCConstitute getElectriDimension() {
        if (mEleDimension != null) {
            return mEleDimension;
        }
        
        return null;
    }
	private void getPowerPanelData() {
		Log.d(TAG, " ======== is connected ==== " + (mState == STATE_CONNECTED));
		if (mState == STATE_CONNECTED) {
			mClient.getPowerPanelData(mUserId, mCtrlNoGuid, mUserType);
		}
	}
	
	private void getBillDetailOfMonth(String date) {
		if (mState == STATE_CONNECTED) {
			mClient.getBillDetailOfMonth(mUserId, mCtrlNoGuid, date);
		}
	}
	
	private void getBillDetailOfRecent(String dateNum) {
		if (mState == STATE_CONNECTED) {
			mClient.getBillDetailOfRecent(mUserId, mCtrlNoGuid, dateNum);
		}
	}
	
	private void getBillMonthList(String yearNum) {
		if (mState == STATE_CONNECTED) {
			mClient.getBillMonthList(mUserId, mCtrlNoGuid, yearNum);
		}
	}
	
	private void getNotices() {
		if (mState == STATE_CONNECTED) {
			mClient.getNotices(mUserId, mCtrlNoGuid);
		}
	}
	
	private void getUserAreaInfo(String areaIdPath) {
		if (mState == STATE_CONNECTED) {
			mClient.getUserAreaInfo(mUserId, areaIdPath);
		}
	}
	
	private void getBusinessAreas(String areaId) {
		if (mState == STATE_CONNECTED) {
			mClient.getBusinessArea(mUserId, areaId);
		}
	}

	private void getCitys(String pid){
	    if (mState == STATE_CONNECTED) {
            mClient.getCitysArea(mUserId, pid);
        }
	}
	private void getZones(String pid){
        if (mState == STATE_CONNECTED) {
            mClient.getZonesArea(mUserId, pid);
        }
    }
	private void getElecDimension(Map<String, String> params) {
	    if (mState == STATE_CONNECTED) {
            mClient.getElecDimension(mUserId,params);
        }
    }
	private void getPaymentRecords(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.getPaymentRecords(mUserId, params);
	    }
	}
	private void getYearFeeDetail(Map<String, String> params){
        if(mState == STATE_CONNECTED){
            mClient.getYearFeeDetail(mUserId, params);
        }
    }
	private void getFamilyPowerEfficency(Map<String, String> params){
        if(mState == STATE_CONNECTED){
            mClient.getFamilyPowerEfficency(mUserId, params);
        }
    }
	private void getSPCConstitute(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
            mClient.getSPCConstitute(mUserId, params);
        }
	}
	private void getPPCConstitute(Map<String, String> params){
        if(mState == STATE_CONNECTED){
            mClient.getPPCConstitute(mUserId, params);
        }
    }
	private void getStepPowerTrack(Map<String, String> params){
        if(mState == STATE_CONNECTED){
            mClient.getStepPowerTrack(mUserId, params);
        }
    }
	
	private void getEqumentList(Map<String,String> params){
	    if(mState == STATE_CONNECTED){
            mClient.getEqumentList(mUserId, params);
        }
	}
	private void getPowerConsumptionTrend(Map<String,String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.getPowerConsumptionTrend(mUserId, params);
	    }
	}
	private void getPowerTips(){
	    if(mState == STATE_CONNECTED){
	        mClient.getPowerTips(mUserId);
	    }
	}
	
	private void getRoomList(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.getRoomList(mUserId,params);
	    }
	}
	private void getRoomElectricalList(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.getRoomElectricalList(mUserId,params);
	    }
	}
	private void turnOnOrOffElectrical(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.turnOnOrOffElectrical(mUserId,params);
	    }
	}
	private void refreshElectrical(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.refreshElectrical(mUserId,params);
	    }
	}

	private void getModelList(Map<String, String> params){
        if(mState == STATE_CONNECTED){
            mClient.getModelList(mUserId,params);
        }
    }
	private void getModelElectricalList(Map<String, String> params){
        if(mState == STATE_CONNECTED){
            mClient.getModelElectricalList(mUserId,params);
        }
    }
	private void executeMode(Map<String, String> params){
        if(mState == STATE_CONNECTED){
            mClient.executeMode(mUserId,params);
        }
    }
	private void getTimedTaskList(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.getTimedTaskList(mUserId,params);
	    }
	}
	private void getNoTaskElectricalList(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.getNoTaskElectricalList(mUserId,params);
	    }
	}
	private void addTimeDTask(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.addTimeDTask(mUserId,params);
	    }
	}
	private void modifyTimeDTask(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.modifyTimeDTask(mUserId,params);
	    }
	}
	private void executeTimeDTask(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.executeTimeDTask(mUserId,params);
	    }
	}
	private void getDefaultPowerTarget(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.getDefaultPowerTarget(mUserId,params);
	    }
	}
	private void getPowerTarget(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.getPowerTarget(mUserId,params);
	    }
	}
	private void setPowerTarget(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.setPowerTarget(mUserId,params);
	    }
	}
	private void deleteTimeDTask(Map<String, String> params){
	    if(mState == STATE_CONNECTED){
	        mClient.deleteTimeDTask(mUserId,params);
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
		case GDClient.REQUEST_CITYS:
		    requestFinished(GDConstract.DATATYPE_CITYES, task.ParsedData);
		    break;
		case GDClient.REQUEST_ZONES:
		    requestFinished(GDConstract.DATATYPE_ZONES, task.ParsedData);
		    break;
		case GDClient.REQUEST_ELECTRICAL_POWER_CONSUPTION_CONSTITUTE:
		    requestFinished(GDConstract.DATATYPE_ELECTRICAL_POWER_CONSUMPTION_CONSTITUTE, task.ParsedData);
		    if(task.ParsedData != null)
		        mEleDimension = (EPCConstitute) task.ParsedData;
		    break;
		    
		case GDClient.REQUEST_PAYMENT_RECORDS:
		    requestFinished(GDConstract.DATATYPE_PAYMENT_RECORDS, task.ParsedData);
		    break;
		    
		case GDClient.REQUEST_YEAR_FEE_DETAIL:
		    requestFinished(GDConstract.DATATYPE_YREAR_FEE_DETAIL, task.ParsedData);
		    break;
		case GDClient.REQUEST_FAMILY_POWER_EFFICENCY:
		    requestFinished(GDConstract.DATATYPE_FAMILY_POWER_EFFICENCY, task.ParsedData);
            break;
		case GDClient.REQUEST_STEP_POWER_CONSUPTION_CONSTITUTE:
		    requestFinished(GDConstract.DATATYPE_STEP_POWER_CONSUMPTION_CONSTITUTE ,task.ParsedData);
		    break;
		case GDClient.REQUEST_PERIOD_POWER_CONSUPTION_CONSTITUTE:
            requestFinished(GDConstract.DATATYPE_PERIOD_POWER_CONSUMPTION_CONSTITUTE ,task.ParsedData);
            break;
		case GDClient.REQUEST_STEP_POWER_CONSUMPTION_TRACK:
		    requestFinished(GDConstract.DATATYPE_STEP_POWER_CONSUMPTION_TRACK ,task.ParsedData);
		    break;
		case GDClient.REQUEST_EQUMENTLIST:
		    requestFinished(GDConstract.DATATYPE_EQUMENTLIST, task.ParsedData);
		    break;
		case GDClient.REQUEST_POWER_CONSUMPTION_TREND:
		    requestFinished(GDConstract.DATATYPE_POWER_CONSUMPTION_TREND, task.ParsedData);
		    break;
		case GDClient.REQUEST_POWER_TIPS:
		    requestFinished(GDConstract.DATATYPE_POWER_TIPS, task.ParsedData);
		    break;
		case GDClient.REQUEST_ROOM_LIST:
            requestFinished(GDConstract.DATATYPE_ROOM_LIST, task.ParsedData);
            break;
		case GDClient.REQUEST_ROOM_ELECTRICAL_LIST:
            requestFinished(GDConstract.DATATYPE_ROOM_ELECTRICAL_LIST, task.ParsedData);
            break;
		case GDClient.REQUEST_TURN_ON_OFF_ELECTRICAL:
            requestFinished(GDConstract.DATATYPE_TUNN_ON_OFF_ELECTRICAL, task.ParsedData);
            break;
		case GDClient.REQUEST_REFRESH_ELECTRICAL:
            requestFinished(GDConstract.DATATYPE_REFRESH_ELECTRICAL, task.ParsedData);
            break;
		case  GDClient.REQUEST_MODEL_LIST:
		    requestFinished(GDConstract.DATATYPE_MODEL_LIST, task.ParsedData);
             break;
		case GDClient.REQUEST_MODEL_ELECTRICAL_LIST:
		    requestFinished(GDConstract.DATATYPE_MODEL_ELECTRICAL_LIST, task.ParsedData);
		    break;
		case GDClient.REQUEST_EXECUTE_MODE:
            requestFinished(GDConstract.DATATYPE_EXECUTE_MODE, task.ParsedData);
            break;
		case GDClient.REQUEST_TIMED_TASK_LIST:
            requestFinished(GDConstract.DATATYPE_TIMED_TASK_LIST, task.ParsedData);
            break;
		case GDClient.REQUEST_NO_TASK_ELECTRICAL_LIST:
            requestFinished(GDConstract.DATATYPE_NO_TASK_ELCTRICAL_LIST, task.ParsedData);
            break;
		case GDClient.REQUEST_ADD_TIMED_TASK:
            requestFinished(GDConstract.DATATYPE_ADD_TIMED_TASK, task.ParsedData);
            break;
		case GDClient.REQUEST_MODIFY_TIMED_TASK:
		    requestFinished(GDConstract.DATATYPE_MODIFY_TIMED_TASK, task.ParsedData);
		    break;
		case GDClient.REQUEST_DELETE_TIMED_TASK:
            requestFinished(GDConstract.DATATYPE_DELETE_TIMED_TASK, task.ParsedData);
            break;
		case GDClient.REQUEST_EXECUTE_TIMED_TASK:
            requestFinished(GDConstract.DATATYPE_EXECUTE_TIMED_TASK, task.ParsedData);
            break;
		case GDClient.REQUEST_DEFAULT_POWER_TARGET:
            requestFinished(GDConstract.DATATYPE_DEFAULT_POWER_TARGET, task.ParsedData);
            break;
		case GDClient.REQUEST_POWER_TARGET:
            requestFinished(GDConstract.DATATYPE_POWER_TARGET, task.ParsedData);
            break;
		case GDClient.REQUEST_SET_POWER_TARGET:
            requestFinished(GDConstract.DATATYPE_SETTING_POWER_TARGET, task.ParsedData);
            break;
		}
	}
	
	private void handleRequestError(int error, Object task) {
		if (error == GDConstract.ErrorCodeRepeatLogin) {
			mRepeatLoginCount++;
			
			Log.d(TAG, " == handleRequestError == " + error);
			
			if (mRepeatLoginCount == REPEATLOGIN_COUNT) {
				mRepeatLoginCount = 0;
				return;
			}
			
			mClient.login();
		}else{
		    if(task instanceof Task){
		        EventData.GuodianEvent event = new EventData.GuodianEvent();
		        int type = ((Task) task).TaskType;
		        
		        switch (type) {
                case GDClient.REQUEST_TURN_ON_OFF_ELECTRICAL:
                    event.Type = GDConstract.DATATYPE_TUNN_ON_OFF_ELECTRICAL;
                    break;

                case GDClient.REQUEST_REFRESH_ELECTRICAL:
                    event.Type = GDConstract.DATATYPE_REFRESH_ELECTRICAL;
                    break;
                case GDClient.REQUEST_MODEL_ELECTRICAL_LIST:
                    event.Type  = GDConstract.DATATYPE_MODEL_ELECTRICAL_LIST;
                    break;
                case GDClient.REQUEST_EXECUTE_MODE:
                    event.Type = GDConstract.DATATYPE_EXECUTE_MODE;
                    break;
                case GDClient.REQUEST_EQUMENTLIST:
                    event.Type = GDConstract.DATATYPE_EQUMENTLIST;
                    break;
                case GDClient.REQUEST_TIMED_TASK_LIST:
                    event.Type = GDConstract.DATATYPE_TIMED_TASK_LIST;
                    break;
                case GDClient.REQUEST_ADD_TIMED_TASK:
                    event.Type = GDConstract.DATATYPE_ADD_TIMED_TASK;
                    break;
                case GDClient.REQUEST_MODIFY_TIMED_TASK:
                    event.Type = GDConstract.DATATYPE_MODIFY_TIMED_TASK;
                    break;
                case GDClient.REQUEST_DELETE_TIMED_TASK:
                    event.Type = GDConstract.DATATYPE_DELETE_TIMED_TASK;
                    break;
                case GDClient.REQUEST_EXECUTE_TIMED_TASK:
                    event.Type = GDConstract.DATATYPE_EXECUTE_TIMED_TASK;
                    break;
                case GDClient.REQUEST_SET_POWER_TARGET:
                    event.Type = GDConstract.DATATYPE_SETTING_POWER_TARGET;
                }
		        event.Data =  ((Task)task).ResponseData[7];
		        notifyEvent(EventData.EVENT_GUODIAN_DATA_ERROR, event);
		    }
		}
	}
	private void handleSocketError() {
		Log.d(TAG, " == handleSocketError == ");
		
		restart();
	}

	private void handleConnectFailed() {
		Log.d(TAG, " == handleConnectFailed == ");
		
		restart();
	}

	private void handleConnected() {
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
	
	private void handleConnectedAlready() {
		
		Log.d(TAG, " == handleConnectedAlready == state =" + mState + " login=" + mLoginState);
		
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
				mUserId = data.UserData.UserInfo.Account;
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
