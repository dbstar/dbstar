package com.dbstar.openuser.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.bean.OBean;
import com.dbstar.util.LogUtil;
import com.dbstar.util.ToastUtils;

public class UserAgreementActivity extends Activity {

	// 用户须知的文本
	private TextView userMustKnow;
	private CheckBox checkBox;
	// 开始使用按钮
	private ImageButton btnAgree;
//	private LinearLayout layout;
	private static Map<String, Object> objectMap = new HashMap<String, Object>();// 每个按钮的id对应的每个key值
	private static Map<String, Object> objectMap2 = new HashMap<String, Object>();// 每个按钮对应的上下左右的key值
	
	private static String curFocusPosition = "0";
	private static String lastcurFocusPosition = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lt_page_user_agreement);
	}

	@Override
		protected void onResume() {
			super.onResume();
			initView();
			populateData();
		}

	private void populateData() {
		
		// 设置TextView本身的滚动条
		userMustKnow.setMovementMethod(ScrollingMovementMethod.getInstance());
		// 如果想要滚动条时刻显示，必须加上下面的一句
//		userMustKnow.setScrollbarFadingEnabled(false);

		checkBox.setFocusable(true);
		
		// 只有checkBox被选中了，“开始使用”按钮才可以点击跳转到下一个页面
		btnAgree.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkBox.isChecked()) {
					Intent intent = new Intent();
					intent.setClass(UserAgreementActivity.this, UserRegisterActivity.class);
					startActivity(intent);
					// 根据需求，跳转到注册页面，就不能返回到上一个activity，必须注册完成，所以这里需要将“用户协议”页面finish掉
					finish();
				} else {
					ToastUtils.showToast(UserAgreementActivity.this, getString(R.string.lt_page_user_beginUser_toast));
				}
			}
		});
		
	}

	private void initView() {
		findViews();
		
		curFocusPosition = "0";
		objectMap.put("0", R.id.userAgreement_mustknow);
		objectMap.put("1", R.id.userAgreement_checkBox);
		objectMap.put("0_1", R.id.userAgreement_agreeBtn);
		
		objectMap2.put("0", new OBean("0", "1", "0", "0", "0"));
		objectMap2.put("1", new OBean("0", "0_1", "1", "1", "1"));
		objectMap2.put("0_1", new OBean("1", "0_1", "0_1", "0_1", "0_1"));
		
		userMustKnow.requestFocus();
	}
	
	private void findViews() {
		userMustKnow = (TextView) findViewById(R.id.userAgreement_mustknow);
		checkBox = (CheckBox) findViewById(R.id.userAgreement_checkBox);
		btnAgree = (ImageButton) findViewById(R.id.userAgreement_agreeBtn);
//		layout = (LinearLayout) findViewById(R.id.userAgreement_container);
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		OBean oBean = (OBean) objectMap2.get(curFocusPosition);
		if (oBean == null) {
			return true;
		}
		
		lastcurFocusPosition = curFocusPosition;
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			if (oBean.getUp() != null && !"".equals(oBean.getUp())) {
				curFocusPosition = oBean.getUp();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (oBean.getDown() != null && !"".equals(oBean.getDown())) {
				curFocusPosition = oBean.getDown();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (oBean.getLeft() != null && !"".equals(oBean.getLeft())) {
				curFocusPosition = oBean.getLeft();
			}
			break;
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_ESCAPE:
			// 根据需求，当有网络连接的时候，跳转到该页面，只能往下进行操作，不能返回到上一个页面
			// 所以这里需要将当前activity销毁掉
			this.finish();
			break;
		}
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			requestFocusForView(oBean.getUp());
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			requestFocusForView(oBean.getDown());
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			requestFocusForView(oBean.getLeft());
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			requestFocusForView(oBean.getRight());
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		
		return true;
	}


	private void requestFocusForView(String direction) {
		String view_id;
		if (direction != null && !"".equals(direction)) {
			view_id = String.valueOf(objectMap.get(curFocusPosition));
			if (view_id != null && !"".equals(view_id) && !"null".equals(view_id)) {

				View view = null;
				if ("0".equals(curFocusPosition) || "1".equals(curFocusPosition) || "0_1".equals(curFocusPosition)) {
					try {
						view = this.findViewById(Integer.parseInt(view_id));
					} catch (Exception e) {
						LogUtil.w("UserAgreementActivity", "UserAgreementActivity :: 请求焦点失败!");
					}
					if (view != null) {
						view.setFocusable(true);
						view.requestFocus();
					}
				}
			}
		}
	}
}
