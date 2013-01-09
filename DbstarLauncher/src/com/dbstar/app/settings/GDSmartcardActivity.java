package com.dbstar.app.settings;

import java.util.List;

import android.os.Bundle;

import com.dbstar.R;
import com.dbstar.app.base.MultiPanelActivity;

public class GDSmartcardActivity extends MultiPanelActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mBlockSmartcardPopup = true;
	}

	public void onBuildHeaders(List<Header> target) {
		Header scInfoHeader = new Header();
		scInfoHeader.fragment="com.dbstar.app.settings.smartcard.SmartcardInfoFragment";
		scInfoHeader.titleRes = R.string.smartcard_info;
		
		target.add(scInfoHeader);

		Header caHeader = new Header();
		caHeader.fragment="com.dbstar.app.settings.smartcard.CAManageFragment";
		caHeader.titleRes = R.string.authorization_manage;
		
		target.add(caHeader);
		
		Header versionInfoHeader = new Header();
		versionInfoHeader.fragment="com.dbstar.app.settings.smartcard.MailBoxFragment";
		versionInfoHeader.titleRes = R.string.mailbox;
		
		target.add(versionInfoHeader);
    }

}
