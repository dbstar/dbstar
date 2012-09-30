package com.dbstar.guodian;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class GDWebChromeClient extends WebChromeClient {
	
	private static final String TAG = "GDWebChromeClient";

	Activity mParentActivity;
	
	private String WarningText;
	private String ConformationText;
	
	public GDWebChromeClient (Activity parent) {
		mParentActivity = parent;
		WarningText = mParentActivity.getResources().getString(R.string.warning_text);
		ConformationText = mParentActivity.getResources().getString(R.string.conformation_text);
	}
	
	@Override
	public boolean onJsAlert(WebView view, String url, String message,
			final JsResult result) {
		
		Log.d(TAG, "onJsAlert");
		
		Builder builder = new Builder(mParentActivity);
		builder.setTitle(WarningText);
		builder.setMessage(message);

		builder.setPositiveButton(android.R.string.ok,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				});
		builder.setCancelable(false);
		builder.create();
		builder.show();
		return true;
	}
	
	@Override
	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
		
		Log.d(TAG, "onJsConfirm");
		
		Builder builder = new Builder(mParentActivity);
		builder.setTitle(ConformationText);
		builder.setMessage(message);

		builder.setPositiveButton(android.R.string.ok,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				});
		builder.setNeutralButton(android.R.string.cancel,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();
					}
				});
		
		builder.setCancelable(false);
		builder.create();
		builder.show();
		return true;
	}
	
	@Override
	public boolean onJsPrompt(WebView view, String url, String message,
			String defaultValue, JsPromptResult result) {
		
		Log.d(TAG, "onJsPrompt");
		
		return super.onJsPrompt(view, url, message, defaultValue, result);
	}
}
