package com.guodian.checkdevicetool.testentry;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.dbstar.DbstarDVB.common.Configs;
import com.guodian.checkdevicetool.BoardOrAllTestActivity;
import com.guodian.checkdevicetool.R;
import com.guodian.checkdevicetool.util.GLog;

public class TestTask {
    public static int count = 0;
    public LayoutInflater inflater;
    public Context context;
    private int viewId;
    public Handler mHandler;
    public boolean isAutoToNext = true;
    public boolean isShowResult = true;
    public int order = 0;
    protected LinearLayout mLayout;
    protected String mName;
    protected String mResult = "fail";
    protected GLog mLog;
    public TestTask(Context context ,Handler handler,int viewId,boolean isAuto){
        this.context = context;
        this.viewId = viewId;
        this.isAutoToNext = isAuto;
        this.mHandler = handler;
        inflater = LayoutInflater.from(context);
        count ++;
        order = count;
        mLog = GLog.getLogger("FactoryTest");
    }
    //LinearLayout start(LinearLayout parentView, LayoutInflater inflater, TestTools tools);
    public void start(){
        LinearLayout parentView = (LinearLayout) ((Activity)context).findViewById(R.id.parent);
        mLayout = (LinearLayout) inflater.inflate(viewId, null);
        if(parentView.findViewWithTag(viewId) == null){
            parentView.addView(mLayout,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            ((TextView)mLayout.findViewById(R.id.order)).setText(order + ":");
            mLayout.setTag(viewId);
        }
        else
            mLayout = (LinearLayout) parentView.findViewWithTag(viewId);
        
        mName = ((TextView)mLayout.findViewById(R.id.name)).getText().toString();
        ((BoardOrAllTestActivity)context).setCurrentTestView(mLayout);
    }
    
    void sendSuccessMsg (){
        synchronized (TestTask.class) {
            Message message  = mHandler.obtainMessage();
            message.what = Configs.TEST_SUCCESS;
            Bundle bundle = new Bundle();
            bundle.putBoolean(Configs.MSG_SHOW_RESULT, new Boolean(isShowResult));
            bundle.putBoolean(Configs.MSG_SHOW_AUTO_NEXT,new Boolean(isAutoToNext));
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    } 
    void sendSuccessMsg (String args){
        synchronized (TestTask.class) {
            Message message  = mHandler.obtainMessage();
            message.what = Configs.TEST_SUCCESS;
            Bundle bundle = new Bundle();
            bundle.putBoolean(Configs.MSG_SHOW_RESULT, new Boolean(isShowResult));
            bundle.putBoolean(Configs.MSG_SHOW_AUTO_NEXT,new Boolean(isAutoToNext));
            if(args != null){
                bundle.putString(Configs.MESSAGE, args);
           }
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    } 
    
    void sendFailMsg(String args){
        synchronized (TestTask.class) {
            Message message  = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean(Configs.MSG_SHOW_RESULT, new Boolean(isShowResult));
            bundle.putBoolean(Configs.MSG_SHOW_AUTO_NEXT,new Boolean(isAutoToNext));
            if(args != null){
                 bundle.putString(Configs.MESSAGE, args);
            }
            message.setData(bundle);
            message.what = Configs.TEST_FAIL;
            mHandler.sendMessage(message);
        }
    }
    public void stop(){
        
    }
    
    public void release(){
        count = 0;
    }
    public String getName(){
        return mName;
    }
    public void setResult(String result){
        this.mResult = result;
    }
    
    public String getResult(){
        return mResult;
    }
}
