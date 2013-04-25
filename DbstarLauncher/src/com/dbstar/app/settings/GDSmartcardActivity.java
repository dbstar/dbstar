package com.dbstar.app.settings;

import java.util.List;

import android.app.Service;
import android.os.Bundle;
import android.view.KeyEvent;

import com.dbstar.R;
import com.dbstar.app.base.EngineInterface;
import com.dbstar.app.base.FragmentObserver;
import com.dbstar.app.base.MultiPanelActivity;
import com.dbstar.app.base.SmartcardInterface;

public class GDSmartcardActivity extends MultiPanelActivity implements
		EngineInterface, SmartcardInterface {

	private FragmentObserver mObserver = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBlockSmartcardPopup = true;
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

	public void onBuildHeaders(List<Header> target) {
		Header scInfoHeader = new Header();
		scInfoHeader.fragment = "com.dbstar.app.settings.smartcard.SmartcardInfoFragment";
		scInfoHeader.titleRes = R.string.smartcard_info;

		target.add(scInfoHeader);

		Header verHeader = new Header();
		verHeader.fragment = "com.dbstar.app.settings.smartcard.GDDrmVersionFragment";
		verHeader.titleRes = R.string.version_info;
		target.add(verHeader);
		
		Header caHeader = new Header();
		caHeader.fragment = "com.dbstar.app.settings.smartcard.CAManageFragment";
		caHeader.titleRes = R.string.authorization_manage;

		target.add(caHeader);

		Header versionInfoHeader = new Header();
		versionInfoHeader.fragment = "com.dbstar.app.settings.smartcard.MailBoxFragment";
		versionInfoHeader.titleRes = R.string.mailbox;

		target.add(versionInfoHeader);
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

	public void getSmartcardInfo(FragmentObserver observer, int type) {
		if (mService != null) {
			mService.getSmartcardInfo(this, type);
		}
	}

	public void manageCA(FragmentObserver observer, int type) {
		if (mService != null) {
			mService.manageCA(this, type);
		}
	}

	public void getMailContent(FragmentObserver observer, String id) {
		if (mService != null) {
			mService.getMailContent(this, id);
		}
	}
	
	public void queryDeviceInfo(FragmentObserver observer, String[] keys) {
		if (mService != null) {
			mService.getDeviceInfo(this, keys);
		}
	}

}
