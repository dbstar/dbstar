package com.dbstar.multiple.media.shelf.share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.sax.StartElementListener;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Futao", "onReceive ----------");
        Intent i = new Intent(context, ShareService.class);
        context.startService(i);
    }
    
}
