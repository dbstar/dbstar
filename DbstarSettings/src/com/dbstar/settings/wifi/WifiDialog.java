package com.dbstar.settings.wifi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dbstar.settings.R;

public class WifiDialog extends Dialog {

	private static final String TAG = "WifiDialog";
	private final AccessPoint mAccessPoint;

	private View mView;
	private WifiConfigController mController;
	View.OnClickListener mClickListener;

	static WifiDialog newInstance(Context context, View.OnClickListener l,
			AccessPoint accessPoint) {
		WifiDialog f = new WifiDialog(context, l, accessPoint);

		return f;
	}

	public WifiDialog(Context context, View.OnClickListener l,
			AccessPoint accessPoint) {
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

		mController = new WifiConfigController(this, mView, mAccessPoint,
				mClickListener);
	}

	public WifiConfigController getController() {
		return mController;
	}
}
