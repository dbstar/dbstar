package com.settings.components;

import com.settings.ottsettings.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ShowAdjustSettingsViewWrapper {
	
	private Context context;
	private Button btnShowAdjust;

	public ShowAdjustSettingsViewWrapper(Context context) {
		this.context = context;
	}

	public void initView(View view) {
		btnShowAdjust = (Button) view.findViewById(R.id.showAdjust_settings_btn);
		btnShowAdjust.requestFocus();
		
		btnShowAdjust.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent();
//				Bundle bundle = new Bundle();
//				intent .setComponent(new ComponentName("com.android.settings", "com.android.settings.PositionSetting"));
//				intent.putExtras(bundle);
//				context.startActivity(intent);
				
			}
		});
		
	}
	
	
}
