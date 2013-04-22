package com.android.settings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.util.Log;

import android.os.SystemProperties;

public class HDMICheckService extends Service
{
        private final static String mHDMIStatusConfig = "/sys/class/amhdmitx/amhdmitx0/hpd_state";
        private final String mOutputStatusConfig = "/sys/class/amhdmitx/amhdmitx0/disp_cap";
        private final static String mCurrentResolution = "/sys/class/display/mode";
        
        private final String ACTION_OUTPUTMODE_CHANGE = "android.intent.action.OUTPUTMODE_CHANGE";
        private final String ACTION_OUTPUTMODE_SAVE = "android.intent.action.OUTPUTMODE_SAVE";
        private final String ACTION_OUTPUTMODE_CANCEL = "android.intent.action.OUTPUTMODE_CANCEL";
        private final String OUTPUT_MODE = "output_mode";
        private final String CVBS_MODE = "cvbs_mode";
        
        private static final String PROPERTY_AUTO_OUTPUT_MODE = "auto.output.mode.property";
        private static final String ACTION_AUTO_OUTPUT_MODE = "android.intent.action.AUTO_OUTPUT_MODE";
        private static final String BOOLEAN_AUTO_OUTPUT_MODE = "auto_output_mode";
        private static final String PREFERENCE_AUTO_OUTPUT_MODE = "preference_auto_output_mode";
        
        private BroadcastReceiver mReceiver = null;
        private boolean mAutoOutputMode = false;
        
        private static final String FREQ_DEFAULT = "";
        private static final String FREQ_SETTING = "50hz";
        
        public static final boolean mAutoStartConfig = false;
        
	@Override
        public void onCreate()
        {
                super.onCreate();
                
                SharedPreferences sharedpreference = getSharedPreferences(PREFERENCE_AUTO_OUTPUT_MODE, 
                                Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
                String defaultProperty = null;
                
                if(mAutoStartConfig)
                {
                        defaultProperty = sharedpreference.getString(PROPERTY_AUTO_OUTPUT_MODE, "true");
                }
                else
                {
                        defaultProperty = sharedpreference.getString(PROPERTY_AUTO_OUTPUT_MODE, "false");
                }
                
                Editor editor = sharedpreference.edit();
                editor.putString(PROPERTY_AUTO_OUTPUT_MODE, defaultProperty);
                editor.commit();
                
                if("true".equalsIgnoreCase(defaultProperty))
                {
                        mAutoOutputMode = true;
                }
                else
                {
                        mAutoOutputMode = false;
                }
                
                mReceiver = new BroadcastReceiver()
                {
                        @Override
                        public void onReceive(Context context, Intent intent)
                        {
                                if(intent.getAction().equalsIgnoreCase(ACTION_AUTO_OUTPUT_MODE))
                                {
                                        String str = intent.getStringExtra(BOOLEAN_AUTO_OUTPUT_MODE);
                                        if(str.equalsIgnoreCase("true"))
                                        {
                                                mAutoOutputMode = true;
                                        }
                                        else
                                        {
                                                mAutoOutputMode = false;
                                        }
                                        
                                        Editor editor = getSharedPreferences(PREFERENCE_AUTO_OUTPUT_MODE, 
                                                        Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE).edit();
                                        if(mAutoOutputMode)
                                        {
                                                editor.putString(PROPERTY_AUTO_OUTPUT_MODE, "true");
                                        }
                                        else
                                        {
                                                editor.putString(PROPERTY_AUTO_OUTPUT_MODE, "false");
                                        }
                                        
                                        editor.commit();
                                }
                        }
               };
               
               IntentFilter filter = new IntentFilter();
               filter.addAction(ACTION_AUTO_OUTPUT_MODE);
               
               registerReceiver(mReceiver, filter);
        }

        @Override
        public void onDestroy()
        {
                super.onDestroy();
                
                unregisterReceiver(mReceiver);
        }
        
        public static boolean isHDMIPlugged()
        {
                int hdmiStatus = 0;
                FileInputStream inputStream = null;
                try
                {
                        inputStream = new FileInputStream(mHDMIStatusConfig);
                } 
                catch (FileNotFoundException e)
                {
                        e.printStackTrace();
                }
                
                try
                {
                        hdmiStatus = inputStream.read();
                }
                catch (IOException e)
                {
                        
                        e.printStackTrace();
                }
                
                // ASCII 49 = '1'
                // ASCII 48 = '0'
                if(hdmiStatus == 49)
                {
                        hdmiStatus = 1;
                }
                
                if(hdmiStatus == 48)
                {
                        hdmiStatus = 0;
                }
                
                try
                {
                        inputStream.close();
                } 
                catch (IOException e)
                {
                        e.printStackTrace();
                }
                
                Config.Logd("HDMICheckService", "hdmiStatus is: " + hdmiStatus);
                
                if(hdmiStatus == 1)
                {
                        return true;
                }
                else
                {
                        return false;
                }
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId)
        {
                ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
                Runnable runnable = new Runnable()
                {
                        @Override
                        public void run()
                        {
                                if(isHDMIPlugged()  && mAutoOutputMode)
                                {
                                        if(!isAmlogicVideoPlayerRunning())
                                        {
                                                FileReader fileReader = null;
                                                try
                                                {
                                                        fileReader = new FileReader(mOutputStatusConfig);
                                                } 
                                                catch (FileNotFoundException e)
                                                {
                                                        e.printStackTrace();
                                                }
                                                
                                                BufferedReader bufferedReader = null;
                                                bufferedReader = new BufferedReader(fileReader);
                                                
                                                String readLine = null;
                                                
                                                try
                                                {
                                                        while((readLine = bufferedReader.readLine()) != null)
                                                        {
                                                                if(readLine.contains("*"))
                                                                {
                                                                        break;
                                                                }
                                                        }
                                                } 
                                                catch (IOException e)
                                                {
                                                        e.printStackTrace();
                                                }
                                                
                                                setOutputResolution(readLine);
                                                
                                                try
                                                {
                                                        bufferedReader.close();
                                                } 
                                                catch (IOException e)
                                                {
                                                        e.printStackTrace();
                                                }
                                                
                                                try
                                                {
                                                        fileReader.close();
                                                } 
                                                catch (IOException e)
                                                {
                                                        e.printStackTrace();
                                                }
                                        }
                                }
                        }
                };
                
                scheduledExecutor.scheduleWithFixedDelay(runnable, 500, 3000, TimeUnit.MILLISECONDS);
                
                return super.onStartCommand(intent, flags, startId);
        }
        
        public static String getOutputResolution()
        {
                String currentMode = null;
                
                FileReader fileReader = null;
                try
                {
                        fileReader = new FileReader(mCurrentResolution);
                } 
                catch (FileNotFoundException e)
                {
                        e.printStackTrace();
                }
                
                BufferedReader bufferedReader = null;
                bufferedReader = new BufferedReader(fileReader);
                
                try
                {
                        currentMode = bufferedReader.readLine();
                } 
                catch (IOException e)
                {
                        e.printStackTrace();
                }          
                
                try
                {
                        bufferedReader.close();
                } 
                catch (IOException e)
                {
                        e.printStackTrace();
                }
                
                try
                {
                        fileReader.close();
                }
                catch (IOException e)
                {
                        e.printStackTrace();
                }
                
                return currentMode;
        }
        
        private void setOutputResolution(String resolution)
        {
                Intent change_intent = new Intent(ACTION_OUTPUTMODE_CHANGE);
                Intent save_intent = new Intent(ACTION_OUTPUTMODE_SAVE);
                
                String newMode = null;
                String currentMode = null;
                
                FileReader fileReader = null;
                try
                {
                        fileReader = new FileReader(mCurrentResolution);
                } 
                catch (FileNotFoundException e)
                {
                        e.printStackTrace();
                }
                
                BufferedReader bufferedReader = null;
                bufferedReader = new BufferedReader(fileReader);
                
                try
                {
                        currentMode = bufferedReader.readLine();
                } 
                catch (IOException e)
                {
                        e.printStackTrace();
                }          
                
                try
                {
                        bufferedReader.close();
                } 
                catch (IOException e)
                {
                        e.printStackTrace();
                }
                
                try
                {
                        fileReader.close();
                }
                catch (IOException e)
                {
                        e.printStackTrace();
                }
                
                // Force to set to 720p
                if(resolution == null)
                {        
                        change_intent.putExtra(OUTPUT_MODE, "720p" + FREQ_DEFAULT);
                        save_intent.putExtra(OUTPUT_MODE, "720p" + FREQ_DEFAULT);
                }
                else
                {
                        if(resolution.contains("480i"))
                        {
                                change_intent.putExtra(OUTPUT_MODE, "480i");
                                save_intent.putExtra(OUTPUT_MODE, "480i");
                        }
                        else if(resolution.contains("480p"))
                        {
                                change_intent.putExtra(OUTPUT_MODE, "480p");
                                save_intent.putExtra(OUTPUT_MODE, "480p");
                        }
                        else if(resolution.contains("576i"))
                        {
                                change_intent.putExtra(OUTPUT_MODE, "576i");
                                save_intent.putExtra(OUTPUT_MODE, "576i");
                        }
                        else if(resolution.contains("576p"))
                        {
                                change_intent.putExtra(OUTPUT_MODE, "576p");
                                save_intent.putExtra(OUTPUT_MODE, "576p");
                        }
                        else if(resolution.contains("720p"))
                        {
                                if(resolution.contains(FREQ_SETTING))
                                {
                                        change_intent.putExtra(OUTPUT_MODE, "720p" + FREQ_SETTING);
                                        save_intent.putExtra(OUTPUT_MODE, "720p" + FREQ_SETTING);
                                }
                                else
                                {
                                        change_intent.putExtra(OUTPUT_MODE, "720p" + FREQ_DEFAULT);
                                        save_intent.putExtra(OUTPUT_MODE, "720p" + FREQ_DEFAULT);
                                }
                        }
                        else if(resolution.contains("1080i"))
                        {
                                if(resolution.contains(FREQ_SETTING))
                                {
                                        change_intent.putExtra(OUTPUT_MODE, "1080i" + FREQ_SETTING);
                                        save_intent.putExtra(OUTPUT_MODE, "1080i" + FREQ_SETTING);
                                }
                                else
                                {
                                        change_intent.putExtra(OUTPUT_MODE, "1080i" + FREQ_DEFAULT);
                                        save_intent.putExtra(OUTPUT_MODE, "1080i" + FREQ_DEFAULT);
                                }
                        }
                        else if(resolution.contains("1080p"))
                        {
                                if(resolution.contains(FREQ_SETTING))
                                {
                                        change_intent.putExtra(OUTPUT_MODE, "1080p" + FREQ_SETTING);
                                        save_intent.putExtra(OUTPUT_MODE, "1080p" + FREQ_SETTING);
                                }
                                else
                                {
                                        change_intent.putExtra(OUTPUT_MODE, "1080p" + FREQ_DEFAULT);
                                        save_intent.putExtra(OUTPUT_MODE, "1080p" + FREQ_DEFAULT);
                                }
                        }
                }
                
                newMode = change_intent.getStringExtra(OUTPUT_MODE);
                if(newMode != null)
                {
                        Config.Logd(getClass().getName(), "new mode is: " + newMode);
                }
                else
                {
                        Config.Logd(getClass().getName(), "new mode is: " + "null");
                }
                
                if(currentMode != null)
                {
                        Config.Logd(getClass().getName(), "current mode is: " + currentMode);
                }
                else
                {
                        Config.Logd(getClass().getName(), "current mode is: " + "null");
                }
                
                if(currentMode != null && newMode != null)
                {
                        if(currentMode.equals(newMode))
                        {  
                                return;
                        }
                }
                
                sendBroadcast(change_intent);
                sendBroadcast(save_intent);
        }
        
        private boolean isAmlogicVideoPlayerRunning()
        {
                ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName componentName = activityManager.getRunningTasks(1).get(0).topActivity;
                String className = componentName.getClassName();
                String packageName = componentName.getPackageName();
                
                String videoPlayerClassName = "com.farcore.videoplayer.playermenu";
                
                if(className.equalsIgnoreCase(videoPlayerClassName))
                {
                        return true;
                }
                
                return false;
        }

        @Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
}
