package com.dbstar.myapplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.dbstar.myapplication.AppListActivity.AppInfoAdapter;

public class AppListActivity_bk extends Activity {
    private final static String TAG = "HomeActivity";
    private ArrayList<AppInfo> mApplications;
    private AppInfoAdapter mAppAdapter;
    private GridView mGrid;
    private WindowManager windowManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initView();
        loadApplications();
        bindApplications();
        registerAppModifyBroadCast(); 
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    } 
    private void showDialog(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(getApplicationContext());  
        dialog.setTitle("提示");  
        dialog.setIcon(android.R.drawable.ic_dialog_info);  
        dialog.setMessage("插入Sdcard！ 是否打开文件管理器");  
        dialog.setPositiveButton("确定", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.fb.FileBrower");
                startActivity(intent);
                
            }
        });
        dialog.setNegativeButton("取消", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog = dialog.create();  
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告，关键  
        mDialog.show();  
        
    }
    
    private  void initView(){
        mGrid = (GridView) findViewById(R.id.all_apps);
        mGrid.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                unInstallApp(mApplications.get(position));
                return false;
            }
        });
        
        mGrid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                AppInfo appInfo = mApplications.get(position);
                if(appInfo.componentName != null){
                    Intent intent = getPackageManager().getLaunchIntentForPackage(appInfo.componentName.getPackageName());
                    if(intent != null)
                        startActivity(intent);
                }
                
                
            }
        });
        
        
        mGrid.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(view == null){
                    return;
                }
                
                int x = mGrid.getLeft() + view.getLeft();
                int y = mGrid.getTop() + view.getTop();
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache(true);
                Bitmap bitmap=  view.getDrawingCache();
                int loaction [] = new int[2];
                view.getLocationOnScreen(loaction);
                addView(bitmap,x,y);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
        
        mGrid.setOnKeyListener(new OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                
                int action = event.getAction();
                if (action == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                        int currentItem = mGrid
                                .getSelectedItemPosition();
                        if (currentItem != 0 && currentItem % 6 == 0) {
                            mGrid.setSelection(currentItem -1);
                            
                        }
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_RIGHT: {
                        int currentItem = mGrid.getSelectedItemPosition();
                        if(currentItem == mApplications.size() - 1){
                            
                        }else{
                            if((currentItem+1) % 6 == 0 ){
                                mGrid.setSelection(currentItem + 1);
                            }
                        }
                        break;
                    }
                    }
                }
                return false;
            }
        });
    }
    
    private void addView(Bitmap bitmap,int x,int y){
        if(layout != null){
            layout.removeAllViews();
            final ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.shadow_child_shortcut);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ScaleType.CENTER_INSIDE);
            layout.setLayout(x-40, y-40, x + 252 -40, y+252 - 40);
            layout.addView(imageView);
            startAnimation(imageView);
            return;
        }
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        
        params.type=LayoutParams.TYPE_PHONE;   //设置window type
       
        params.format=PixelFormat.RGBA_8888;   //设置图片格式，效果为背景透明
        
                //设置Window flag
        
        params.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL
        
                                      | LayoutParams.FLAG_NOT_FOCUSABLE;
        
        params.gravity=Gravity.LEFT|Gravity.TOP;   //调整悬浮窗口至左上角，便于调整坐标
        
                //以屏幕左上角为原点，设置x、y初始值
        
        params.x=0;
        
        params.y=0;
                 
                //设置悬浮窗口长宽数据
        
        params.width=1280;
        
        params.height=720;
        layout = new AppAnimViewGroup(this);
        final ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.shadow_child_shortcut);
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ScaleType.CENTER_INSIDE);
        layout.setLayout(x-40, y-40, x + 252 -40, y+252 - 40);
        layout.addView(imageView);
        layout.requestLayout();
        windowManager.addView(layout, params);
        startAnimation(imageView);
//        imageView.postDelayed(new Runnable() {
//        
//        @Override
//        public void run() {
//        }
//    },0);
        
    }
    
    public void startAnima(View view){
        
        TranslateAnimation animation = new TranslateAnimation(0, 100, 0, 100);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        view.startAnimation(animation);
        
    }
    public void startAnimation(final View view){
        ScaleAnimation animation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(200);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        view.startAnimation(animation);
        
       
    }
    private void unInstallApp(AppInfo info){
        if(info == null || info.componentName == null || !isInstalled(info)){
            Toast.makeText(this, "该应用不存在", 1).show();
            return;
        }
        Uri packageURI = Uri.parse("package:" + info.intent.getComponent().getPackageName());   
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);   
        startActivity(uninstallIntent);
    }
    
    private boolean isInstalled(AppInfo info){
        try {
           PackageInfo packageInfo =  getPackageManager().getPackageInfo(info.componentName.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
           if(packageInfo != null)
               return true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void bindApplications() {
        //mAppAdapter = new AppInfoAdapter(this, mApplications);
        if (mGrid == null) {
            mGrid = (GridView) findViewById(R.id.all_apps);
        }
        Log.i("TAG", ""+mApplications.size());
        mGrid.setAdapter(mAppAdapter);
        mGrid.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                View view = mGrid.getChildAt(0);
                int x = mGrid.getLeft() + view.getLeft();
                int y = mGrid.getTop() + view.getTop();
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache(true);
                Bitmap bitmap=  view.getDrawingCache();
                int loaction [] = new int[2];
                view.getLocationOnScreen(loaction);
                addView(bitmap,x,y);
                
            }
        },0);

    }
    private void registerAppModifyBroadCast(){
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);  
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);  
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);  
        intentFilter.addAction(Intent.ACTION_UMS_CONNECTED);
        intentFilter.addDataScheme("package");
        registerReceiver(appBroadcastReceiver, intentFilter);  
        
        IntentFilter intentFilter1 =
                new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter1.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter1.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter1.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter1.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter1.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter1.addDataScheme("file");
        registerReceiver(SdcardAndUsbReciever, intentFilter1);
        
    }
    private void loadApplications() {

      PackageManager manager = getPackageManager();

      Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
      mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

      final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
      Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

      if (apps != null) {
          final int count = apps.size();

          if (mApplications == null) {
              mApplications = new ArrayList<AppInfo>(count);
          }
          mApplications.clear();

          for (int i = 0; i < count; i++) {
              AppInfo application = new AppInfo();
              ResolveInfo info = apps.get(i);
//              
//              if((info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)
//                  continue;
//              

              application.title = info.loadLabel(manager);
              application.setActivity(new ComponentName(
                      info.activityInfo.applicationInfo.packageName,
                      info.activityInfo.name),
                      Intent.FLAG_ACTIVITY_NEW_TASK
                      | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
              application.icon = info.activityInfo.loadIcon(manager);
              
              Log.i("AllApps3D", ""+application.title.toString() + "/" + application.icon.getIntrinsicWidth() + "/" +application.icon.getIntrinsicHeight() );
              
              mApplications.add(application);
          }
      }
  }
  
    private void loadAllDataApp() {
        int count;
        PackageManager pckMan = getPackageManager();
        List<PackageInfo> packs = pckMan.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        count = packs.size();
        String name;
        int installedNum = 0;
        for (int i = 0; i < count; i++) {
            PackageInfo p = packs.get(i);
            if (p.versionName == null) {
                continue;
            }
            // 判断该软件包是否在/data/app目录下
            ApplicationInfo appInfo = p.applicationInfo;
            /**
             *  * Value for {@link #flags}: if set, this application is
             * installed in  * the device's system image.  
             */
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                // 系统程序
                name = p.applicationInfo.loadLabel(pckMan).toString();
                Log.e(" 系统程序app name==", name);
            } else {
                // 不是系统程序
                name = p.applicationInfo.loadLabel(pckMan).toString();
                Log.e(" 不是系统程序app name==", name);
                AppInfo info = new AppInfo();
                
            }
        }
        
    }
    
    private final BroadcastReceiver appBroadcastReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
              loadApplications();
              bindApplications();
        }
    };
    
    private final BroadcastReceiver SdcardAndUsbReciever = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, action);
            
            if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){
                showDialog();
            }else if(action.equals(Intent.ACTION_MEDIA_UNMOUNTED)){
                if(mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
            }
        }
        
    };
    
    private AlertDialog mDialog;
    private AppAnimViewGroup layout;
    
    protected void onDestroy() {
            super.onDestroy();
            unregisterReceiver(appBroadcastReceiver);
    };
    
}
