package com.dbstar.browser;

import com.dbstar.util.LogUtil;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GDWebViewClient extends WebViewClient {

	private static final String TAG = "GDWebViewClient";

//	private long mLoadPageStartTime;
//	private long mLoadPageFinishedTime;

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		LogUtil.d(TAG, "shouldOverrideUrlLoading " + url);
		//view.loadUrl(url);
		return super.shouldOverrideUrlLoading(view, url);
	}
	
	public void doUpdateVisitedHistory(WebView view, String url,
			boolean isReload) {
		LogUtil.d(TAG, "doUpdateVisitedHistory " + url);

		super.doUpdateVisitedHistory(view, url, isReload);
	}

	public void onFormResubmission(WebView view, Message dontResend,
			Message resend) {
		LogUtil.d(TAG, "onFormResubmission ");

		super.onFormResubmission(view, dontResend, resend);
	}

	public void onReceivedHttpAuthRequest(WebView view,
			HttpAuthHandler handler, String host, String realm) {
		LogUtil.d(TAG, "onReceivedHttpAuthRequest ");

		super.onReceivedHttpAuthRequest(view, handler, host, realm);
	}

	public void onReceivedLoginRequest(WebView view, String realm,
			String account, String args) {
		LogUtil.d(TAG, "onReceivedLoginRequest ");
		super.onReceivedLoginRequest(view, realm, account, args);
	}

	public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error) {
		LogUtil.d(TAG, "onReceivedSslError ");

		super.onReceivedSslError(view, handler, error);
	}

	public void onScaleChanged(WebView view, float oldScale, float newScale) {
		LogUtil.d(TAG, "onScaleChanged ");

		super.onScaleChanged(view, oldScale, newScale);
	}

	public void onTooManyRedirects(WebView view, Message cancelMsg,
			Message continueMsg) {
		LogUtil.d(TAG, "onTooManyRedirects ");

		super.onTooManyRedirects(view, cancelMsg, continueMsg);
	}

	public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
		LogUtil.d(TAG, "onUnhandledKeyEvent ");
		super.onUnhandledKeyEvent(view, event);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
//		mLoadPageStartTime = System.currentTimeMillis();
		LogUtil.d(TAG, "onPageStarted url=" + url);

		super.onPageStarted(view, url, favicon);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
//		mLoadPageFinishedTime = System.currentTimeMillis();
//		LogUtil.d(TAG, "onPageFinished "
//				+ (mLoadPageFinishedTime - mLoadPageStartTime));

		super.onPageFinished(view, url);
	}

	@Override
	public void onLoadResource(WebView view, String url) {
//		LogUtil.d(TAG, "onLoadResource ");

		super.onLoadResource(view, url);
	}

	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		LogUtil.d(TAG, "onReceivedError " + errorCode + " " + description + " "
				+ failingUrl);

		super.onReceivedError(view, errorCode, description, failingUrl);
	}

	/*
	 * public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
	 * LogUtil.d(TAG, "shouldOverrideKeyEvent keyCode " + event.getKeyCode());
	 * 
	 * return super.shouldOverrideKeyEvent(view, event); }
	 */

	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		//LogUtil.d(TAG, "shouldInterceptRequest url " + url);

		return super.shouldInterceptRequest(view, url);
	}
}
