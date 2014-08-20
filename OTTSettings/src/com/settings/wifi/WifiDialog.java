package com.settings.wifi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.settings.ottsettings.R;
import com.settings.utils.LogUtil;


public class WifiDialog extends Dialog {

	private static final String TAG = "WifiDialog";
	private final AccessPoint mAccessPoint;

	private View mView;
	private WifiConfigController mController;
	View.OnClickListener mClickListener;

	static WifiDialog newInstance(Context context, View.OnClickListener l, AccessPoint accessPoint) {
		WifiDialog f = new WifiDialog(context, l, accessPoint);
		return f;
	}

	public WifiDialog(Context context, View.OnClickListener l, AccessPoint accessPoint) {
		super(context, R.style.WifiDialog);
		mAccessPoint = accessPoint;
		mClickListener = l;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mView = getLayoutInflater().inflate(R.layout.wifi_dialog, null);
		// setView(mView);
		setContentView(mView);

		mController = new WifiConfigController(this, mView, mAccessPoint, mClickListener);
		LogUtil.d(TAG, "WifiDialog   onCreate");
	}

	public WifiConfigController getController() {
		return mController;
	}
}
