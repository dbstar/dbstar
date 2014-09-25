package com.guodian.checkdevicetool.testentry;

import android.content.Context;
import android.os.Handler;

import com.guodian.checkdevicetool.R;

public class PowerLightTest extends TestTask{
    public PowerLightTest(Context context, Handler handler, int viewId,
            boolean isAuto) {
        super(context, handler, viewId, isAuto);
    }
    
    @Override
    public void start() {
        super.start();
      isShowResult = false;
      isAutoToNext = false;
      sendFailMsg(context.getResources().getString(R.string.test_power_light_comfig));
    }
    

   
}
