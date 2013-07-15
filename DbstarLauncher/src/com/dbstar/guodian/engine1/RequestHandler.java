package com.dbstar.guodian.engine1;

import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;

public class RequestHandler {
    
    private static final int CMD_SMARTLIFE_SEND = 0x00111;
    private HandlerThread mThread;
    
    private Handler mHandler;
    
    private ClientRequestService mService;
    
    private RequestHandler(ClientRequestService service){
        
        this.mService = service;
        
        mThread = new HandlerThread("RequestHandler", Process.THREAD_PRIORITY_BACKGROUND);
        mThread.start();
        mHandler = new Handler(mThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                
                RequestParams params = (RequestParams) msg.obj;
                String taskId = generateUID();
                String userId = params.getUserId();
                String cmd =  CmdHelper.constructCmd(params, userId, taskId);
                mService.addTask(taskId, params.getRequestType());
                mService.sendCmd(CMD_SMARTLIFE_SEND,cmd);
            }
        };
    }
    
    
    private static RequestHandler mInstance;
    
    public synchronized static RequestHandler getInstance(ClientRequestService service){
        if(mInstance == null)
            mInstance = new RequestHandler(service);
        return mInstance;
    }
    
    public void sendRequest(RequestParams params){
        Message message = mHandler.obtainMessage();
        Bundle data = new Bundle();
        message.obj = params;
        message.setData(data);
        message.sendToTarget();
        
    }
    private  String generateUID() {
        String uid = null;
        long currentTime = SystemClock.currentThreadTimeMillis();
        Random random = new Random(currentTime);
        long randomValue = random.nextLong();
        uid = String.valueOf(currentTime) + String.valueOf(randomValue);
        return uid;
    }
}
