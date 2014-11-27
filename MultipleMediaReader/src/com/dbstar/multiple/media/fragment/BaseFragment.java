package com.dbstar.multiple.media.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.view.KeyEvent;

import com.dbstar.multiple.media.shelf.activity.NewsPaperActivity;
import com.dbstar.multiple.media.util.GLog;

public class BaseFragment extends Fragment{
    
    protected GLog mLog;
    protected NewsPaperActivity mActivity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mLog = GLog.getLogger("RM BaseFragment");
        mActivity = (NewsPaperActivity) activity;
    }
    public void SetData(Object object){
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event){
       return false;
   }
}
