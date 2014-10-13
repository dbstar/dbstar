package com.settings.components;

import com.settings.ottsettings.R;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class HelpSettingsViewWrapper {
	private Context context;
	private TextView txtContent;

	public HelpSettingsViewWrapper(Context context) {
		this.context = context;
	}

	public void initView(View view) {
		txtContent = (TextView) view.findViewById(R.id.help_settings_content);
		// 设置TextView本身的滚动条
		txtContent.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		txtContent.requestFocus();
	}
}
