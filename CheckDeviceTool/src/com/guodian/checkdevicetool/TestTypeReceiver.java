package com.guodian.checkdevicetool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.dbstar.DbstarDVB.common.Configs;
import com.guodian.checkdevicetool.util.GLog;

public class TestTypeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            
            SharedPreferences preferences = context.getSharedPreferences(Configs.TEST_TYPE_ORDER_FILE_NAME, Context.MODE_PRIVATE);
            int value = preferences.getInt(Configs.TEST_TYPE, Configs.TYPE_BOARD_TEST);
            Intent startInTent = null;
            
           GLog.getLogger("Futao").i("onReceive" + value);
            //mainboard test
            if(value == Configs.TYPE_BOARD_TEST){
                startInTent = new Intent(context, BoardOrAllTestActivity.class);
                startInTent.putExtra(Configs.TEST_TYPE, Configs.TYPE_BOARD_TEST);
                
            }
          //all test
            else if(value == Configs.TYPE_ALL_TEST){ 
                startInTent = new Intent(context, BoardOrAllTestActivity.class);
                startInTent.putExtra(Configs.TEST_TYPE, Configs.TYPE_ALL_TEST);
            }
            
            //play video test
            else if (value == Configs.TYPE_AGING_TEST){
                startInTent = new Intent(context, AgingTestActivity.class);
                startInTent.putExtra(Configs.TEST_TYPE, Configs.TYPE_AGING_TEST);
            }
            // selector test include all test and play video test
            else if(value == Configs.TYPE_SELECTOR_TEST){
                startInTent = new Intent(context, SelectTestActivity.class);
            }
            
            if(startInTent != null){
                startInTent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startInTent);
            }
    }
}
