package com.guodian.checkdevicetool.testentry;

import android.content.Context;
import android.os.Handler;

import com.guodian.checkdevicetool.R;

public class NetWorkSignallampTest extends TestTask{
    private final static String TAG = "NetWorkSignallampTest";
    public NetWorkSignallampTest(Context context, Handler handler, int viewId,
            boolean isAuto) {
        super(context, handler, viewId, isAuto);
    }
    
    @Override
    public void start() {
         super.start();
        this.isAutoToNext= false;
        this.isShowResult = false;
        sendFailMsg(context.getResources().getString(R.string.test_network_singal_lamp_comfig));
        
    }
    

   
}
