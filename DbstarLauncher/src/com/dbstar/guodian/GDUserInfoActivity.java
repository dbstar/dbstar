package com.dbstar.guodian;

import com.dbstar.guodian.util.GDNetworkUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class GDUserInfoActivity extends GDBaseActivity {

	private TextView mVersionView, mCardNumberView, mMacAddressView,
			mCardValidityTermView, mUerBussiness, mUserResidualAmountView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.userinfo_view);

		mVersionView = (TextView) findViewById(R.id.text_version);
		mCardNumberView = (TextView) findViewById(R.id.text_usercardnumber);
		mMacAddressView = (TextView) findViewById(R.id.text_macaddress);
		mCardValidityTermView = (TextView) findViewById(R.id.text_usercard_validityterm);
		mUerBussiness = (TextView) findViewById(R.id.text_user_bussiness);
		mUserResidualAmountView = (TextView) findViewById(R.id.text_user_residual_amount);
		
		initializeView();
		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}
	
	public void onServiceStart() {
		super.onServiceStart();
		
		//TODO: the network maybe not startup, so this call may not get the mac address.
		//TODO: so consider using some other mechanism.
		String macAddr = mService.getMacAddress();
		mMacAddressView.setText(macAddr);
	}
}
