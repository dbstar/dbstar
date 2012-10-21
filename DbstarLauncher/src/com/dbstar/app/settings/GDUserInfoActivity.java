package com.dbstar.app.settings;

import com.dbstar.R;
import com.dbstar.model.UserData;
import com.dbstar.service.GDDataProviderService;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GDUserInfoActivity extends GDSettingActivity {

	private TextView mOperatorView, mCardNumberView;
	ListView mProductView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.userinfo_view);

		initializeView();
		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}

	public void onServiceStart() {
		super.onServiceStart();

		mService.getUserData(this);
	}

	public void updateData(int type, String key, Object data) {
		if (type == GDDataProviderService.REQUESTTYPE_GETUSERDATA) {
			if (data != null) {
				UserData userData = (UserData) data;
				String operatorInfo = userData.OperatorData;
				if (operatorInfo != null && !operatorInfo.isEmpty()) {
					mOperatorView.setText(operatorInfo);
				}

				String cardId = userData.CardId;
				if (cardId != null && !cardId.isEmpty()) {
					mCardNumberView.setText(cardId);
				}

				String products = userData.Products;
				if (products != null && !products.isEmpty()) {
					String[] productsArray = new String[1];
					productsArray[0] = products;
					ArrayAdapter<String> productAdapter = new ArrayAdapter<String>(
							this, R.layout.userinfo_view_productitem,
							productsArray);
					mProductView.setAdapter(productAdapter);
					productAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	public void initializeView() {
		super.initializeView();

		mOperatorView = (TextView) findViewById(R.id.text_operator);
		mCardNumberView = (TextView) findViewById(R.id.text_usercardnumber);

		mProductView = (ListView) findViewById(R.id.userinfo_orderedproduct);
	}
}
