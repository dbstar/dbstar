package com.settings.components;

import com.settings.base.BaseFragment;
import com.settings.ethernet.EthernetConfigController;
import com.settings.ottsettings.R;
import com.settings.utils.SettingsCommon;

import android.app.Activity;
import android.content.Context;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class WiredSettingsView{

//	private Button mOkButton, mPrevButton;;
	private RelativeLayout container;
	private Button btnConfirm;
	private EthernetConfigController mController;
	private Activity mActivity;
	private int Ethernet_Network_Mode;
	
	public WiredSettingsView(Activity activity, View view, int mode) {
		this.mActivity = activity;
		mController = new EthernetConfigController(activity, (EthernetManager) activity.getSystemService(Context.ETH_SERVICE), mode);
		Ethernet_Network_Mode = mode;
	}

	public void initView(View view) {
		container = (RelativeLayout) view.findViewById(R.id.network_container);
		btnConfirm = (Button) view.findViewById(R.id.eth_btn_confirm);
		
		onResume();
		btnConfirm.setOnClickListener(mOnClickListener);
	}
	
	public void onResume() {
		if (mController != null) {
			mController.resume();
		}
	}

	public void onPause() {
		if (mController != null) {
			mController.pause();
		}
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (container == null) {
				return;
			}
			
			Ethernet_Network_Mode = mController.saveConfigure();
			
			switchToFinishSettings(Ethernet_Network_Mode);
			
			onPause();
		}
	};

	private void switchToFinishSettings(int mode) {
		if (container != null && container.getChildCount() > 0) {
			View view = container.getChildAt(0);
			Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.fade_out);
			view.startAnimation(animation);
			container.removeAllViews();
		}
		// 构造器
		LayoutInflater inflater = mActivity.getLayoutInflater();
		View view = inflater.inflate(R.layout.lt_page_network_setup_endview, null);
		Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.fade_in);
		view.startAnimation(animation);
		container.addView(view);
		
		FinishSettingsViewWrapper wrapper = new FinishSettingsViewWrapper(mActivity, mode);
		wrapper.initView(view);
	}

}
