package com.dbstar.app.settings.smartcard;

import com.dbstar.app.base.BaseFragment;
import com.dbstar.app.base.EngineInterface;
import com.dbstar.app.base.FragmentObserver;
import com.dbstar.app.base.SmartcardInterface;
import com.dbstar.service.GDDataProviderService;

public class GDSmartcardFragment extends BaseFragment implements FragmentObserver {
	protected boolean mBound = false;
	protected GDDataProviderService mService;
	protected EngineInterface mEngine = null;
	protected SmartcardInterface mSmartcardEngine = null;
	
	public void onStart() {
		super.onStart();
		
		if (mActivity instanceof SmartcardInterface) {
			mSmartcardEngine = (SmartcardInterface) mActivity;
		}
		
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
