package com.dbstar.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.dbstar.R;
import com.dbstar.model.GDCommon;
import com.dbstar.util.upgrade.RebootUtils;
import com.dbstar.widget.AlertActivity;
import com.dbstar.widget.AlertController;

public class GDUpgradeActivity extends AlertActivity implements
		DialogInterface.OnClickListener {

	private static final int POSITIVE_BUTTON = AlertDialog.BUTTON_POSITIVE;

	private String mPackageFile = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent in = getIntent();
		mPackageFile = in.getStringExtra(GDCommon.KeyPackgeFile);
		
		final AlertController.AlertParams p = mAlertParams;
		p.mIconId = R.drawable.ic_dialog_alert;
		p.mTitle = getResources().getString(R.string.dialog_upgrade_title);
		
		String message = "";
		if (mPackageFile != null && !mPackageFile.isEmpty()) {
			message = mPackageFile + "\n";
		}
		message += getResources().getString(R.string.dialog_upgrade_notes);
		p.mMessage = message;
		
		p.mPositiveButtonText = getResources().getString(
				R.string.button_text_ok);
		p.mPositiveButtonListener = this;
		p.mNegativeButtonText = getResources().getString(
				R.string.button_text_cancel);
		p.mNegativeButtonListener = this;
		setupAlert();
	}

	public void onClick(DialogInterface dialog, int which) {

		if (which == POSITIVE_BUTTON) {
			rebootInstallPackage(mPackageFile);
		}

		// No matter what, finish the activity
		finish();
	}
	
	void rebootInstallPackage(String packageFile) {
		RebootUtils.rebootInstallPackage(this, packageFile);
	}
}
