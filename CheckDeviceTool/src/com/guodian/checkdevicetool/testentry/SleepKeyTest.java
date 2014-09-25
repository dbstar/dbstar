package com.guodian.checkdevicetool.testentry;

import android.content.Context;
import android.os.Handler;

import com.guodian.checkdevicetool.R;

public class SleepKeyTest extends TestTask{
    public SleepKeyTest(Context context, Handler handler, int viewId,
            boolean isAuto) {
        super(context, handler, viewId, isAuto);
    }
    
    @Override
    public void start() {
        super.start();
      isShowResult = false;
      isAutoToNext = false;
      sendFailMsg(context.getResources().getString(R.string.test_sleep_key_comfig));
    }
    

   
}
