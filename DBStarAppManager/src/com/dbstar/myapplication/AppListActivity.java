package com.dbstar.myapplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class AppListActivity extends Activity {
    private final static int PAGE_MAX_COUNT = 18;
    private final static String TAG = "HomeActivity";
    private ArrayList<AppInfo> mApplications;
    private AppInfoAdapter mAppAdapter;
    private GridView mGrid;
    private ViewFlipper mFlipper;
    private WindowManager mWindowManager;
    private AppAnimViewGroup mlayout;
    private ArrayList<AppInfo[]> pages;
    private CircleFlowIndicator mIndicator;
    private int mCurrentPage;
    private View mLastSelectedVew;
    private ImageView mReflected;
    Animation push_left_in, push_left_out, push_right_in, push_right_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        loadApplications();
        constructPages();
        initView();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mFlipper.post(new Runnable() {
            @Override
            public void run() {
                if (mFlipper.getChildCount() > 0) {
                    View view = mFlipper.getChildAt(0);
                    GridView gridView = (GridView) view.findViewById(R.id.all_apps);
                    if (gridView != null && gridView.getChildCount() > 0) {
                        prepareScaleAnimation(gridView.getChildAt(0));
                    }
                }
            }
        });
        registerAppModifyBroadCast();
    }

    private void constructPages() {
        pages = new ArrayList<AppInfo[]>();
        int size = mApplications.size();
        int pageCount = (size + PAGE_MAX_COUNT - 1) / PAGE_MAX_COUNT;
        int index = 0;
        for (int i = 0; i < pageCount; i++) {
            int pageSize = Math.min(PAGE_MAX_COUNT, size - index);
            AppInfo[] page = new AppInfo[pageSize];
            for (int j = 0; j < pageSize; j++) {
                page[j] = mApplications.get(index);
                index++;
            }
            pages.add(page);
        }

    }

    private void initView() {
        
        mIndicator = (CircleFlowIndicator) findViewById(R.id.indicator);
        mReflected = (ImageView) findViewById(R.id.reflected);
        
        push_left_in = AnimationUtils.loadAnimation(this, R.anim.sns_push_left_in);
        push_left_out = AnimationUtils.loadAnimation(this, R.anim.sns_push_left_out);
        push_right_in = AnimationUtils.loadAnimation(this, R.anim.sns_push_right_in);
        push_right_out = AnimationUtils.loadAnimation(this, R.anim.sns_push_right_out);

        mFlipper = (ViewFlipper) findViewById(R.id.app_viewFilpper);
        for (int i = 0; i < pages.size(); i++) {
            View child = LayoutInflater.from(this).inflate(R.layout.app_gridview, null);
            GridView grid = (GridView) child.findViewById(R.id.all_apps);
            mAppAdapter = new AppInfoAdapter(this, pages.get(i));
            grid.setAdapter(mAppAdapter);
            mFlipper.addView(child, i);
            initListener(grid);
        }
        mFlipper.setDisplayedChild(mCurrentPage);
        mIndicator.setPageCount(pages.size());
        mIndicator.setCurrentPage(0);
        push_left_in.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
               stopScaleAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                View view = mFlipper.getCurrentView();
                GridView gridView = (GridView) view.findViewById(R.id.all_apps);
                gridView.setSelection(0);
                prepareScaleAnimation(gridView.getChildAt(0));
                mIndicator.setCurrentPage(mFlipper.getDisplayedChild());
            }
        });
        push_right_in.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                stopScaleAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                View view = mFlipper.getCurrentView();
                GridView gridView = (GridView) view.findViewById(R.id.all_apps);
                gridView.setSelection(0);
                prepareScaleAnimation(gridView.getChildAt(0));
                mIndicator.setCurrentPage(mFlipper.getDisplayedChild());
            }
        });

    }

    private void initListener(GridView view) {
        view.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                mGrid = (GridView) mFlipper.getCurrentView().findViewById(R.id.all_apps);
                int action = event.getAction();
                if (action == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                        int currentItem = mGrid.getSelectedItemPosition();
                        if (currentItem != 0 && currentItem % 6 == 0) {
                            mGrid.setSelection(currentItem - 1);

                        } else if (currentItem == 0) {
                            if (true) {
                                mFlipper.setInAnimation(push_right_in);
                                mFlipper.setOutAnimation(push_right_out);
                                mFlipper.showPrevious();
                            }
                        }
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_RIGHT: {
                        int currentItem = mGrid.getSelectedItemPosition();
                        if (currentItem == PAGE_MAX_COUNT - 1 || currentItem == (mGrid.getChildCount() -1)) {
                            if (true) {
                                mFlipper.setInAnimation(push_left_in);
                                mFlipper.setOutAnimation(push_left_out);
                                mFlipper.showNext();
                            }
                        } else {
                            if ((currentItem + 1) % 6 == 0) {
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
        view.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view == null) {
                    return;
                }
                if(mLastSelectedVew != null)
                    mLastSelectedVew.setVisibility(View.VISIBLE);
                view.setVisibility(View.INVISIBLE);
                prepareScaleAnimation(view);
                mLastSelectedVew = view;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        view.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.setVisibility(View.VISIBLE);
                mWindowManager.removeView(mlayout);
                mlayout = null;
                mGrid = (GridView) mFlipper.getCurrentView().findViewById(R.id.all_apps);
                AppInfoAdapter adapter = (AppInfoAdapter) mGrid.getAdapter();
                unInstallApp(adapter.getAppInof(position));
                return true;
            }
        });

        view.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                view.setVisibility(View.VISIBLE);
                mWindowManager.removeView(mlayout);
                mlayout = null;
                mGrid = (GridView) mFlipper.getCurrentView().findViewById(R.id.all_apps);
                AppInfoAdapter adapter = (AppInfoAdapter) mGrid.getAdapter();
                AppInfo appInfo = adapter.getAppInof(position);
                if (appInfo.componentName != null) {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(appInfo.componentName.getPackageName());
                    intent.putExtra("boolean", true);
                    if (intent != null)
                        startActivity(intent);
                }

            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mGrid = (GridView) mFlipper.getCurrentView().findViewById(R.id.all_apps);
        prepareScaleAnimation(mGrid.getChildAt(mGrid.getSelectedItemPosition()));
        
    }
    private void prepareScaleAnimation(View view){
        if(view == null)
            return;
        int x =  mFlipper.getLeft() +view.getLeft();
        int y =  mFlipper.getTop() +  view.getTop();
        
        
        if(mlayout == null){
            mlayout = new AppAnimViewGroup(this);
            mlayout.setLayout(x - 40-20, y - 40-20, x + 252 - 40 + 30, y + 252 - 40 + 30);
            View v = mlayout.clone(view);
            mlayout.addView();
            
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();

            params.type = LayoutParams.TYPE_TOAST; // 设置window type

            params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

            // 设置Window flag

            params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL

            | LayoutParams.FLAG_NOT_FOCUSABLE;

            params.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角，便于调整坐标

            // 以屏幕左上角为原点，设置x、y初始值

            params.x = 0;

            params.y = 0;

            // 设置悬浮窗口长宽数据

            params.width = 1280;

            params.height = 720;
            
            mWindowManager.addView(mlayout, params); 
            //mlayout.startAnim();
        }else{
            mlayout.setLayout(x - 40-25, y - 40-25, x + 252 - 40 + 25, y + 252 - 40 + 25);
            View v = mlayout.clone(view);
            mlayout.startAnim();
        }
        
    }

    private void stopScaleAnimation() {
        if (mlayout != null) {
            mlayout.stopAnim();
        }
    }


    private void unInstallApp(AppInfo info) {
        if (info == null || info.componentName == null || !isInstalled(info)) {
            Toast.makeText(this, "该应用不存在", 1).show();
            return;
        }
        Uri packageURI = Uri.parse("package:" + info.intent.getComponent().getPackageName());
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        startActivity(uninstallIntent);
    }

    private boolean isInstalled(AppInfo info) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(info.componentName.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
            if (packageInfo != null)
                return true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void registerAppModifyBroadCast() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_UMS_CONNECTED);
        intentFilter.addDataScheme("package");
        registerReceiver(appBroadcastReceiver, intentFilter);


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
                // if((info.activityInfo.applicationInfo.flags &
                // ApplicationInfo.FLAG_SYSTEM) > 0)
                // continue;
                //
                
                if(	info.activityInfo.applicationInfo.packageName.equals("com.dbstar.myapplication")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.amlogic.PPPoE")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.farcore.videoplayer")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.amlogic.mediacenter")
             //   	|| info.activityInfo.applicationInfo.packageName.equals("com.mbx.settingsmbox")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.amlapp.update.otaupgrade")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.android.gallery3d")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.amlogic.netfilebrowser")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.android.providers.downloads.ui")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.android.music")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.android.music.ArtistAlbumBrowserActivity")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.gsoft.appinstall")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.dbstar")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.media.android.dbstarplayer")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.dbstar.settings")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.dbstar.DbstarDVB")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.adobe.flashplayer")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.amlogic.miracast")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.moretv.tvapp")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.dbstar.ottlauncher")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.android.service.remotecontrol")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.amlogic.mediaboxlauncher")
                	|| info.activityInfo.applicationInfo.packageName.equals("app.android.applicationxc")
                	|| info.activityInfo.applicationInfo.packageName.equals("com.hycstv.android")){
					Log.i("AllApps3D", "do not show Built-in app(packageName): " + info.activityInfo.applicationInfo.packageName);
					continue;
                }
                application.title = info.loadLabel(manager);
                application.setActivity(new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name), Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                application.icon = info.activityInfo.loadIcon(manager);

                Log.i("AllApps3D", "" + application.title.toString() + "/" + application.icon.getIntrinsicWidth() + "/" + application.icon.getIntrinsicHeight());

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
            constructPages();
            if(pages.size() > mFlipper.getChildCount()){
                mCurrentPage = pages.size() -1; 
                mFlipper.removeAllViews();
                initView();
                return;
            }
           
            mCurrentPage = mFlipper.getDisplayedChild();
            
            if(pages.size() < mFlipper.getChildCount()){
                int count = mFlipper.getChildCount() - pages.size();
                for(int i = 0;i<count; i++){
                    mFlipper.removeViewAt(mFlipper.getChildCount()-1);
                }
            }
            
            for(int i = 0;i< mFlipper.getChildCount();i++){
                View v = mFlipper.getChildAt(i);
                GridView gridView = (GridView) v.findViewById(R.id.all_apps);
                AppInfoAdapter adapter = new AppInfoAdapter(AppListActivity.this, pages.get(i));
                gridView.setAdapter(adapter);
            }
            mIndicator.setPageCount(pages.size());
            if(mCurrentPage > (pages.size() -1)){
                mIndicator.setCurrentPage(pages.size() -1);
            }else{
                mIndicator.setCurrentPage(mCurrentPage);
            }
        }
    };


   

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(appBroadcastReceiver);
        mWindowManager.removeView(mlayout);
    };
    public class AppInfoAdapter extends ArrayAdapter<AppInfo> {
        private AppInfo [] mApplications;
        private Activity activty;

        public AppInfoAdapter(Context context, AppInfo [] apps) {
            super(context, 0, apps);
            activty = (Activity) context;
            mApplications = apps;
        }
        
        public AppInfo getAppInof(int postion){
            if(mApplications != null && mApplications.length > postion){
                return mApplications [postion];
            }
            return null;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final AppInfo info = mApplications[position];
            if (convertView == null) {
                final LayoutInflater inflater = activty.getLayoutInflater();
                convertView = inflater.inflate(R.layout.application, parent, false);
            }
            ImageView mIconView = (ImageView) convertView.findViewById(R.id.icon);
            TextView mTitleView = (TextView) convertView.findViewById(R.id.title);
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.child);
            mIconView.setBackgroundDrawable(info.icon);
            mTitleView.setText(info.title);
            String picStr = "item_child_";
            picStr = picStr + ((position + 1) % 8);
            int picId = activty.getResources().getIdentifier(picStr, "drawable", activty.getPackageName());
            layout.setBackgroundDrawable(activty.getResources().getDrawable(picId));
            return convertView;
        }
    }
    
    public AppInfo getCurrentSelectedAppInof(){
        mGrid = (GridView) (mFlipper.getCurrentView() == null ? null : mFlipper.getCurrentView().findViewById(R.id.all_apps));
        if(mGrid == null )
            return null;
        AppInfoAdapter adapter = (AppInfoAdapter) mGrid.getAdapter();
        if(adapter == null)
            return null;
        int position = mGrid.getSelectedItemPosition();
        return adapter.getAppInof(position);
    }
    
}
