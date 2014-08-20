package com.settings.components;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.settings.ottsettings.R;

public class WiredSettingsViewWrapper {

	private RadioGroup radioGroup;
	private RadioButton rbAutoGet;
	private RadioButton rbManualSet;
	private Button btnOk;
	private EditText mIpaddr;
	private EditText mDns, mBackupDns;
	private EditText mGw;
	private EditText mMask;

	private Context context;

	public WiredSettingsViewWrapper(Context context) {
		this.context = context;
	}

	public WiredSettingsViewWrapper() {
	}

	// private View.OnClickListener mOnClickListener = new
	// View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// if (v.getId() == R.id.okbutton) {
	// mManager.nextPage(SettingsCommon.PAGE_ETHERNET2,
	// SettingsCommon.PAGE_FINISH);
	//
	// mController.saveConfigure();
	// } else if (v.getId() == R.id.prevbutton) {
	// mManager.prevPage(SettingsCommon.PAGE_ETHERNET2);
	// }
	// }
	// };

	public void initView(View view) {
//		radioGroup = (RadioGroup) view.findViewById(R.id.dhcp_rg_getStyle);
		rbAutoGet = (RadioButton) view.findViewById(R.id.dhcp_switch_indicator);
		rbManualSet = (RadioButton) view.findViewById(R.id.manual_switch_indicator);
		btnOk = (Button) view.findViewById(R.id.eth_btn_confirm);
		mIpaddr = (EditText) view.findViewById(R.id.eth_ip);
		mMask = (EditText) view.findViewById(R.id.eth_mask);
		mDns = (EditText) view.findViewById(R.id.eth_dns);
		mBackupDns = (EditText) view.findViewById(R.id.eth_backup_dns);
		mGw = (EditText) view.findViewById(R.id.eth_gateway);

		btnOk.setOnClickListener(new OnClickListener() {
			private long lastClick = 0l;

			@Override
			public void onClick(View v) {
				if (System.currentTimeMillis() - lastClick < 800l) {
					return;
				}
				lastClick = System.currentTimeMillis();

				// TODO: 点击进入配置页面
				// 这里需要根据选择的radioButton来确定要进入哪个页面。
				if (radioGroup.getCheckedRadioButtonId() == R.id.wired_settings_rb_autoGet) {
					// 跳转到自动获取页面
				} else {
					// 跳转到手动设置页面
				}
			}
		});

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

			}
		});
	}

}
