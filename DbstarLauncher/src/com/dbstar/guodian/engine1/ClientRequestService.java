package com.dbstar.guodian.engine1;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dbstar.DbstarDVB.DbstarServiceApi;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.engine.GDClientObserver;
import com.dbstar.model.EventData;
import com.dbstar.service.client.GDDBStarClient;
import com.dbstar.util.GDNetworkUtil;
import com.dbstar.util.LogUtil;

public class ClientRequestService {
    
    private static final String TAG = "ClientRequestService";
    
    private static final String SOCKET_STATE_EXCEPTION = "-1";
    private static final String SOCKET_STATE_COLOSED = "0";
    private static final String SOCKET_STATE_CREATED = "1";
    private static final String SOCKET_STATE_UNCONNECTED = "2";
    private static final String SOCKET_STATE_CONNECTED = "3";
    
    
    private static final int SMARTLIFE_CONNECT_STATUS = 0x20200;
    private static final int SMARTLIFE_RECV = 0x20201;
    
    private static final int DBSTARSERVICE_CONNECTED =1;
    private static final int DBSTARSERVICE_UNCONNECTED =-1;
    
    private static final int REPEATLOGIN_COUNT = 3;
    
    private static final int CMD_SMARTLIFE_CONNECT_STATUS = 0x00113;
    private static final int  LOGIN_STATU_LOGINING = 100;
    private static final int  LOGIN_STATU_LOGINED = 200;
    private static final int  LOGIN_STATU_UNLOGIN = 300;
    private static final int  LOGIN_STATU_RELOGIN = 400;
    
    private int mLoginStatu;
    private GDDBStarClient mDBStarClient;
    private LoginData mLoginData;
    private String mUserId;
    
    private Context mContext;
    private RequestHandler mRequestHandler;
    private ResponseHandler mResponseHandler;
    private GDClientObserver mObserver;
    
    private String mSocketState;
    private boolean mIsLogin;
    private boolean mIsBoundDbstarServie;
    private int mRepeatLoginCount = 0;
    
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            int type = msg.arg1;
            Object event = msg.obj;
            if (mObserver != null) {
                mObserver.notifyEvent(type, event);
            }
        };
    };
    
    class RequestTask {
        public int TaskType;
        public String TaskId;
        public String Command;
        public String[] ResponseData;
        public Object ParsedData;
    }
    private LinkedList<RequestTask> mWaitingQueue = new LinkedList<RequestTask>();
    
    public ClientRequestService(Context context) {
        this.mContext = context;
        mRequestHandler = RequestHandler.getInstance(this);
        mResponseHandler = ResponseHandler.getInstance(this);
        reqisterSystemMessageReceiver();
        mLoginStatu = LOGIN_STATU_UNLOGIN;
    }
    
    public void start(GDDBStarClient starClient,GDClientObserver observer){
        Log.i(TAG, "start ------------------------------------------------------------------------------------");
        this.mDBStarClient = starClient;
        this.mObserver = observer;
        if(mLoginStatu == LOGIN_STATU_UNLOGIN && SOCKET_STATE_CONNECTED.equals(mSocketState)&& mDBStarClient.isBoundToServer()){
            login();
        }
    }
    
    public void notifySocketState(String state){
        mSocketState = state;
        
        LogUtil.i(TAG, "notifySocketState ------------------------------------------------------------------------------------" + mSocketState);
        if(mLoginStatu == LOGIN_STATU_UNLOGIN && SOCKET_STATE_CONNECTED.equals(mSocketState) && mDBStarClient.isBoundToServer()){
            login();
        }else if(mLoginStatu == LOGIN_STATU_LOGINED && SOCKET_STATE_CONNECTED.equals(mSocketState)&& mDBStarClient.isBoundToServer()){
            login();
            LogUtil.i(TAG, "notifySocketState  relogin ------------------------------------------------------------------------------------" + mSocketState);
        }else{
            EventData.GuodianEvent event = new EventData.GuodianEvent();
            notifyEvent(EventData.EVENT_GUODIAN_CONNECT_FAILED, event);
        }
        
    }
    
    public void notifyDbstarServiceState(String  state){
        LogUtil.i(TAG, "notifyDbstarServiceState ------------------------------------------------------------------------------------" + state);
        if(SOCKET_STATE_CONNECTED.equals(mSocketState)){
            if(mLoginStatu == LOGIN_STATU_UNLOGIN && mDBStarClient.isBoundToServer() ){
                login();
            }
        }else{
           if(mDBStarClient.isBoundToServer()){
              Intent intent =  sendCmd(CMD_SMARTLIFE_CONNECT_STATUS,"");
              byte[] bytes = intent.getByteArrayExtra("result");
              if (bytes != null) {
                 try {
                    String result = new String(bytes,"utf-8");
                    notifySocketState(result);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
              }
           } 
        }
    }
    public synchronized void  RequestData(RequestParams params){
        LogUtil.i(TAG, "RequestData ------------------------------------------------------------------------------------");
        params.put(RequestParams.KEY_TASK_USER_ID, mUserId);
        mRequestHandler.sendRequest(params);
    }
    public synchronized Intent  sendCmd(int cmd,String cmdStr){
        LogUtil.i(TAG, "sendCmd ------------------------------------------------------------------------------------");
        return mDBStarClient.sendRequestServerCommond(cmd,cmdStr);
    }
    public synchronized void  addTask(String taskId, int taskType){
        RequestTask task = new RequestTask();
        task.TaskId = taskId;
        task.TaskType = taskType;
        mWaitingQueue.add(task);
    }
    public synchronized RequestTask removeTask(String taskId){
        RequestTask task = null;

        for (RequestTask t : mWaitingQueue) {
            if (t.TaskId.equals(taskId)) {
                mWaitingQueue.remove(t);
                task = t;
                break;
            }
        }
        return task;
    }
    public void ReceiveData(String data){
        LogUtil.i(TAG, "ReceiveData ------------------------------------------------------------------------------------");
        mResponseHandler.handResponse(data);
    }
    
    public void handResponseFinish(RequestTask task){
        LogUtil.i(TAG, "handResponseFinish ------------------------------------------------------------------------------------");
        if(task.TaskType == GDRequestType.DATATYPE_LOGIN){
            mLoginData = (LoginData) task.ParsedData;
            loginFinished(mLoginData);
        }else{
            requestFinished(task.TaskType, task.ParsedData);
            }
    }

    private void loginFinished(LoginData data) {
        Log.d(TAG, "======= loginFinished=========");
        mLoginStatu = LOGIN_STATU_LOGINED;
        
        mLoginData = data;
        
        if (data != null) {
            
            if (data.UserData != null && data.UserData.UserInfo != null) {
                mUserId = data.UserData.UserInfo.Account;
            }
        }
        
        EventData.GuodianEvent event = new EventData.GuodianEvent();
        event.Type = GDRequestType.DATATYPE_LOGIN;
        event.Data = data;

        notifyEvent(EventData.EVENT_LOGIN_SUCCESSED, event);
    }
    
    private void requestFinished(int type, Object data) {
        EventData.GuodianEvent event = new EventData.GuodianEvent();
        event.Type = type;
        event.Data = data;
        notifyEvent(EventData.EVENT_GUODIAN_DATA, event);
    }
    
    private void responseError(int type ,Object data){
        EventData.GuodianEvent event = new EventData.GuodianEvent();
        event.Type = type;
        event.Data = data;
        notifyEvent(EventData.EVENT_GUODIAN_DATA_ERROR, event);
    }
    private void notifyEvent(int type, Object event) {
        Message message = handler.obtainMessage();
        message.arg1 = type;
        message.obj = event;
        message.sendToTarget();
    }
    public void handErrorResponse(RequestTask task){
        if(task == null){
            responseError(-1, null);
            return;
        }
        if(task.TaskType == GDRequestType.DATATYPE_LOGIN){
            mLoginStatu = LOGIN_STATU_UNLOGIN;
            mRepeatLoginCount++;
            
           LogUtil.i(TAG, " == handleRequestError == reeatlogin"  );
            
            if (mRepeatLoginCount == REPEATLOGIN_COUNT) {
                mRepeatLoginCount = 0;
                return;
            }
            
            login();
        }else{
            responseError(task.TaskType, task.ResponseData[7]);
        }
    }

    private void login(){
        Log.i("Futao", "login ------------------------------------------------------------------------------------");
        String macAddr = GDNetworkUtil.getMacAddress(mContext, true);
        mUserId = macAddr;
        RequestParams params = new RequestParams( GDRequestType.DATATYPE_LOGIN);
        params.put(RequestParams.KEY_SYSTEM_FLAG, "aut");
        params.put(RequestParams.KEY_METHODID, "m008f001");
        params.put("macaddr", macAddr);
        RequestData(params);
        mLoginStatu = LOGIN_STATU_LOGINING;
    }
    
    private void reqisterSystemMessageReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DbstarServiceApi.ACTION_NOTIFY);
        filter.addAction(DbstarServiceApi.ACTION_DBSTARTSERCIE);
        mContext.registerReceiver(mCmdMessageReceiver, filter);
    }
    
    BroadcastReceiver mCmdMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive type ------------------------- " + action);

            if (action.equals(DbstarServiceApi.ACTION_NOTIFY)) {

                int type = intent.getIntExtra("type", 0);
                Log.d(TAG, "onReceive type " + type);

                switch (type) {
                case  SMARTLIFE_RECV:
                    String response = getStringData(intent, "utf-8");
                    ReceiveData(response);
                  break;
                  
                case SMARTLIFE_CONNECT_STATUS:
                    String state =  getStringData(intent, "utf-8");
                    notifySocketState(state);
                    break;
                }
            }else if(action.equals(DbstarServiceApi.ACTION_DBSTARTSERCIE)){
                    String state = intent.getExtras().getString("message");
                    notifyDbstarServiceState(state);
            }
        }
    };
    private  String getStringData (Intent intent, String charset) {
        String info = null;

        byte[] bytes = intent.getByteArrayExtra("message");
        if (bytes != null) {
            try {
                info = new String(bytes, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        
        return info;
    }

    public LoginData getLoginData() {
        return mLoginData;
    }
}
