package com.dbstar.settings;


import com.dbstar.settings.R;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class GDUserInfoActivity extends GDSettingsActivity {

	private TextView mOperatorView, mCardNumberView;
	ListView mProductView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.userinfo_view);

		initializeView();
		Intent intent = getIntent();
//		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
//		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}
	
	public void onServiceStart() {
//		super.onServiceStart();
	}
	
	public void initializeView() {
//		super.initializeView();
		
		mOperatorView = (TextView) findViewById(R.id.text_operator);
		mCardNumberView = (TextView) findViewById(R.id.text_usercardnumber);
		
		mProductView = (ListView) findViewById(R.id.userinfo_orderedproduct);
	}
}
