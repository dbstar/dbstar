package com.dbstar.guodian.app.mypower;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.dbstar.R;
import com.dbstar.guodian.app.base.GDEngineActivity;
import com.dbstar.guodian.data.ElectricityPrice;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.guodian.data.UserPriceStatus;
import com.dbstar.guodian.engine.GDConstract;

public class GDMypowerActivity extends GDEngineActivity {

	private static final String BACK_STACK_PAGES = ":dbstar:smartpower:mypower";

	int mPriceType;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.smartpower_mypower);

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		mPriceType = intent.getIntExtra(GDConstract.KeyPriceType,
				GDConstract.PriceTypeStep);
		initializeView();

		if (mMenuPath != null) {
			String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
			showMenuPath(menuArray);
		}

		openPage(mPriceType, null);
	}

	public void onServiceStart() {
		super.onServiceStart();

	}
	
	public void handleLoginSuccessed() {
		LoginData loginData = mService.getLoginData();
		PowerPanelData panelData = loginData.PanelData;
		UserPriceStatus status = panelData.PriceStatus;
		
		if (status == null)
			return;

		if (status.PriceType == null) {
			return;
		}
		
		String priceType = status.PriceType;
		
		if (ElectricityPrice.PRICETYPE_STEP.equals(priceType)) {
			mPriceType = GDConstract.PriceTypeStep;
		} else if (ElectricityPrice.PRICETYPE_STEPPLUSTIMING.equals(priceType)) {
			mPriceType = GDConstract.PriceTypeStepPlusTiming;
		} else if(ElectricityPrice.PRICETYPE_SINGLE.equals(priceType)){
		    mPriceType = GDConstract.PriceTypeSingle;
		}else if(ElectricityPrice.PRICETYPE_TIMING.equals(priceType)){
		    mPriceType = GDConstract.PriceTypeTiming;
		}else{
			return;
		}
		
		openPage(mPriceType, null);
	}
	private void openPage(int priceType, Bundle args) {
		String fragmentName = null;
		if (mPriceType == GDConstract.PriceTypeStep ||mPriceType == GDConstract.PriceTypeSingle) {
			fragmentName = "com.dbstar.guodian.app.mypower.GDStepPowerFragment";
		} else if (mPriceType == GDConstract.PriceTypeStepPlusTiming || mPriceType == GDConstract.PriceTypeTiming) {
			fragmentName = "com.dbstar.guodian.app.mypower.GDTimingStepPowerFragment";
		} else {
		   showErrorMsg(R.string.no_login);
			return;
		}

		getFragmentManager().popBackStack(BACK_STACK_PAGES,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		Fragment f = Fragment.instantiate(this, fragmentName, args);
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.page_content, f);
		transaction.commitAllowingStateLoss();
	}
}
