package com.dbstar.guodian.app.base;

import com.dbstar.app.base.BaseFragment;
import com.dbstar.app.base.EngineInterface;
import com.dbstar.app.base.FragmentObserver;
import com.dbstar.service.GDDataProviderService;

public class GDBaseFragment  extends BaseFragment implements FragmentObserver {
	protected boolean mBound = false;
	protected GDDataProviderService mService;
	protected EngineInterface mEngine = null;
	
	public void onStart() {
		super.onStart();
		
		if (mActivity instanceof EngineInterface) {
			mEngine = (EngineInterface) mActivity;
			mEngine.registerObserver(this);
			//Notes: service may be null now.
			// then wait serviceReay and get service there
			mService = (GDDataProviderService) mEngine.getService();
			
			if (mService != null) {
				serviceStart();
			}
		}
	}
	
	public void onStop() {
		super.onStop();
		
		if (mEngine != null) {
			mEngine.unregisterObserver(this);
		}
	}
	
	public void serviceReady(EngineInterface engine) {
		mEngine = engine;
		mService = (GDDataProviderService) mEngine.getService();
		
		if (mService != null) {
			serviceStart();
		}
	}
	
	public void serviceStop() {
		mEngine = null;
		mService = null;
	}
	
	// Method used to request data and receive data or event.
	// Request data at this point
	public void serviceStart() {
		
	}
	
	// Receive data at this point
	public void updateData(FragmentObserver observer, int type, Object key, Object data) {
		
	}
	
	// handle event at this point
	public void notifyEvent(FragmentObserver observer, int type, Object event) {
		
	}
	
	public boolean onBackkeyPress() {
		return false;
	}
}
