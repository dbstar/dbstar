package com.dbstar.app.alert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

import com.dbstar.R;
import com.dbstar.model.GDCommon;
import com.dbstar.util.upgrade.RebootUtils;

public class GDUpgradeActivity extends Activity implements View.OnClickListener {

	private String mPackageFile = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent in = getIntent();
		mPackageFile = in.getStringExtra(GDCommon.KeyPackgeFile);

		setContentView(R.layout.alert_upgrade);

		Button okButton = (Button) findViewById(R.id.buttonOK);
		Button cancelButton = (Button) findViewById(R.id.buttonCancel);
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
	}

	void rebootInstallPackage(String packageFile) {
		RebootUtils.rebootInstallPackage(this, packageFile);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.buttonOK) {
			rebootInstallPackage(mPackageFile);
		} else {
			Intent cancelIntent = new Intent(GDCommon.ActionUpgradeCancelled);
			cancelIntent.putExtra(GDCommon.KeyPackgeFile, mPackageFile);
			sendBroadcast(cancelIntent);
		}

		// No matter what, finish the activity
		finish();
	}
}
