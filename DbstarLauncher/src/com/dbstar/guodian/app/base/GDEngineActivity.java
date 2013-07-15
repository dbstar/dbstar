package com.dbstar.guodian.app.base;

import android.app.Service;
import android.view.KeyEvent;

import com.dbstar.app.base.EngineInterface;
import com.dbstar.app.base.FragmentObserver;
import com.dbstar.guodian.engine1.RequestParams;

public class GDEngineActivity extends GDSmartActivity implements EngineInterface {
	private FragmentObserver mObserver = null;

	@Override
	public void onServiceStart() {
		super.onServiceStart();

		if (mObserver != null) {
			mObserver.serviceReady(this);
		}
	}

	@Override
	public void onServiceStop() {
		super.onServiceStop();

		if (mObserver != null) {
			mObserver.serviceStop();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean ret = false;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (mObserver != null) {
					ret = mObserver.onBackkeyPress();
				}
			}
		}

		if (ret) {
			return ret;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void updateData(int type, Object key, Object data) {
		super.updateData(type, key, data);

		if (mObserver != null) {
			mObserver.updateData(mObserver, type, key, data);
		}
	}

	public void notifyEvent(int type, Object event) {
		super.notifyEvent(type, event);

		if (mObserver != null) {
			mObserver.notifyEvent(mObserver, type, event);
		}
	}

	@Override
	public Service getService() {
		return mService;
	}

	public void registerObserver(FragmentObserver observer) {
		mObserver = observer;
	}

	public void unregisterObserver(FragmentObserver observer) {
		if (mObserver == observer) {
			mObserver = null;
		}
	}
	
//	public void request(int type, Object args) {
//		requestData(type, args);
//	}

    @Override
    public void request(RequestParams params) {
        requestData(params);
    }

}
