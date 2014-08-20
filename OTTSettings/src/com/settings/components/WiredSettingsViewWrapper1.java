package com.settings.components;

import com.settings.base.BaseFragment;
import com.settings.base.PageManager;
import com.settings.ethernet.EthernetConfigController;
import com.settings.ottsettings.R;
import com.settings.utils.LogUtil;
import com.settings.utils.SettingsCommon;

import android.app.Activity;
import android.content.Context;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class WiredSettingsViewWrapper1 {

	// private RadioGroup radioGroup;
	// private RadioButton rbAutoGet;
	// private RadioButton rbManualSet;
	private Button mOkButton, mPrevButton;;
	private EthernetConfigController mController;
	private Activity mActivity;
	private PageManager mManager = null;

	public WiredSettingsViewWrapper1(Activity activity) {
		this.mActivity = activity;
	}

	public WiredSettingsViewWrapper1() {
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

//	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			if (v.getId() == R.id.okbutton) {
//				mManager.nextPage(SettingsCommon.PAGE_ETHERNET2, SettingsCommon.PAGE_FINISH);
//
//				mController.saveConfigure();
//			} else if (v.getId() == R.id.prevbutton) {
//				mManager.prevPage(SettingsCommon.PAGE_ETHERNET2);
//			}
//		}
//	};

	public void initView(View view) {
//		mController = new EthernetConfigController(mActivity, (EthernetManager) mActivity.getSystemService(Context.ETH_SERVICE));
//		LogUtil.d("WiredSettingsViewWrapper1", "<<<<<<<<----initView" + mController);
//		mOkButton = (Button) mActivity.findViewById(R.id.okbutton);
//		mPrevButton = (Button) mActivity.findViewById(R.id.prevbutton);
//		mOkButton.setOnClickListener(mOnClickListener);
//		mPrevButton.setOnClickListener(mOnClickListener);
//
//		if (mActivity instanceof PageManager) {
//			mManager = (PageManager) mActivity;
//		}
//		// radioGroup = (RadioGroup)
//		// view.findViewById(R.id.wired_settings_rg_getStyle);
//		// rbAutoGet = (RadioButton)
//		// view.findViewById(R.id.wired_settings_rb_autoGet);
//		// rbManualSet = (RadioButton)
//		// view.findViewById(R.id.wired_settings_rb_manualSet);
//		// btnBegin = (Button) view.findViewById(R.id.wired_settings_btn_begin);
//		//
//		// rbAutoGet.setChecked(true);
//		//
//		// btnBegin.setOnClickListener(new OnClickListener() {
//		// private long lastClick = 0l;
//		// @Override
//		// public void onClick(View v) {
//		// if (System.currentTimeMillis() - lastClick < 800l) {
//		// return;
//		// }
//		// lastClick = System.currentTimeMillis();
//		//
//		// // TODO: 点击进入配置页面
//		// // 这里需要根据选择的radioButton来确定要进入哪个页面。
//		// if (radioGroup.getCheckedRadioButtonId() ==
//		// R.id.wired_settings_rb_autoGet) {
//		// // 跳转到自动获取页面
//		// } else {
//		// // 跳转到手动设置页面
//		// }
//		// }
//		// });
	}

}
