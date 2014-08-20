package com.dbstar.multiple.media.common;

import java.sql.Date;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TestService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Date date = new Date(System.currentTimeMillis());
        Log.i("Futao", "test service on onStartCommand " + date.toLocaleString());
        return super.onStartCommand(intent, flags, startId);
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.i("Futao", "test service on create ");
        
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Futao", "test service on onDestroy ");
    }
}
