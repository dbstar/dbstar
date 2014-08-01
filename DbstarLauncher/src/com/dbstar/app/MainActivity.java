package com.dbstar.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.dbstar.R;

/**
 * 过渡页面
 * @author john
 *
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置成无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lt_page_background);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Bundle bundle = getIntent().getExtras();
//				if (bundle.containsKey("appShop") && bundle.getBoolean("appShop")) {
//					Intent intent = new Intent();
//					intent.setClass(MainActivity.this, DbstarAppShopActivity.class);
//					startActivity(intent);
//				} else {
					String packageName = (String) bundle.get("packageName");
					Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
					startActivity(intent);					
//				}
				MainActivity.this.finish();
			}
		}, 1000);
	}

}
