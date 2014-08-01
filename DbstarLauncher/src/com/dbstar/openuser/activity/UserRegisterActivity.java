package com.dbstar.openuser.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.dbstar.R;
import com.dbstar.app.DbstarOTTActivity;
import com.dbstar.bean.OBean;
import com.dbstar.http.HttpConnect;
import com.dbstar.http.SimpleWorkPool.ConnectWork;
import com.dbstar.http.SimpleWorkPool.SimpleWorkPoolInstance;
import com.dbstar.model.GDDataModel;
import com.dbstar.model.GDSystemConfigure;
import com.dbstar.util.Constants;
import com.dbstar.util.DbstarUtil;
import com.dbstar.util.LogUtil;
import com.dbstar.util.MD5;
import com.dbstar.util.ToastUtils;

public class UserRegisterActivity extends Activity {

	// 姓氏输入框
	private EditText etName;
	private RadioGroup rgSex;
	// 男
	private RadioButton rbBoy;
	// 女
	private RadioButton rbGirl;
	// 手机号码输入框
	private EditText etPhone;
	// 验证码输入框
	private EditText etCode;
	// 获取验证码按钮
	private ImageView imgCode;
	// 注册按钮
	private ImageView imgRegister;
	
	private String deviceModel;
	private String productSN; 

	private static Map<String, Object> objectMap = new HashMap<String, Object>();// 每个按钮的id对应的每个key值
	private static Map<String, Object> objectMap2 = new HashMap<String, Object>();// 每个按钮对应的上下左右的key值

	private static String curFocusPosition = "0";
	private static String lastcurFocusPosition = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 设置页面无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lt_page_user_register);
		
		// 从数据库中读取数据
//		deviceModel = SqliteUtils.getInstance().queryValue("DeviceModel");
//		productSN = SqliteUtils.getInstance().queryValue("ProductSN");
		GDDataModel dataModel = new GDDataModel();
		GDSystemConfigure mConfigure = new GDSystemConfigure();
		dataModel.initialize(mConfigure);
		deviceModel = dataModel.getHardwareType();
		productSN = dataModel.getDeviceSearialNumber();
		LogUtil.d(getClass().getName() + "注册", "-----------==========" + deviceModel);
		LogUtil.d(getClass().getName() + "注册", "-----------==========" + productSN);		
	}

	@Override
	protected void onResume() {
		super.onResume();
		initView();
		populateData();
		setEventListener();
	}

	private void populateData() {
		etPhone.addTextChangedListener(new TextWatcherWrapper(etPhone));
		etCode.addTextChangedListener(new TextWatcherWrapper(etCode));
		
		// 只有填上手机号码，才可以发送验证码
		if (etPhone.getText() != null && !"".equals(etPhone.getText().toString()) && etPhone.getText().toString().length() < 11) {
			imgCode.setClickable(true);
			imgCode.setFocusable(true);
		} else {
			imgCode.setClickable(false);
			imgCode.setFocusable(false);
//			ToastUtils.showToast(UserRegisterActivity.this, "请输入正确的手机号！");
		}
		
		// 当所有信息都填写完成之后，才可以点击“注册”按钮进行开户
		if (etName.getText() != null && !"".equals(etName.getText().toString()) 
				&& etPhone.getText() != null && !"".equals(etPhone.getText().toString()) && etPhone.getText().toString().length() < 11
				&& etCode.getText() != null && !"".equals(etCode.getText().toString())
				&& (rbBoy.isChecked() || rbGirl.isChecked())) {
			imgRegister.setClickable(true);
			imgRegister.setFocusable(true);
		} else {
			imgRegister.setClickable(false);
			imgRegister.setFocusable(false);
		}
		
	}

	private void setEventListener() {
		imgCode.setOnClickListener(new OnClickListener() {
			private long lastClick = 0l;
			
			@Override
			public void onClick(View v) {
				if (System.currentTimeMillis() - lastClick < 800l) {
					return;
				}
				lastClick = System.currentTimeMillis();
				getIdentifyCode();
			}
		});
		
		imgRegister.setOnClickListener(new OnClickListener() {
			private long lastClick = 0l;
			
			@Override
			public void onClick(View v) {
				if (System.currentTimeMillis() - lastClick < 800l) {
					return;
				}
				register();
			}
		});
		
		rgSex.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.register_rb_boy) {
					rbBoy.setChecked(true);
				} else {
					rbGirl.setChecked(true);
				}
			}
		});
	}
	
	private void getIdentifyCode() {
		String mac = DbstarUtil.getLocalMacAddress(true);
		String md5String = MD5.getMD5("OEM$" + deviceModel + "$" + productSN + "$" + mac);
		// 参数
		List<NameValuePair> paramsList = new LinkedList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("TERMINALUNIQUE", "OEM$" + deviceModel + "$" + productSN));
		paramsList.add(new BasicNameValuePair("TERMINALPROFILE", "1.0.0.0"));
		paramsList.add(new BasicNameValuePair("AUTHENTICATOR", md5String));
		paramsList.add(new BasicNameValuePair("MOBILEPHONE", etPhone.getText().toString()));

		// 对参数进行编码
		String param = URLEncodedUtils.format(paramsList, "UTF-8");
		String url = Constants.Server_Url_Identify + param;
		
		ConnectWork<HashMap<Integer, String>> work = new ConnectWork<HashMap<Integer,String>>(HttpConnect.POST, url, paramsList) {
			
			@Override
			public HashMap<Integer, String> processResult(HttpEntity entity) {
				// Http响应成功，但是需要解析返回的内容，判断rc值，才能知道是否成功
				HashMap<Integer, String> map = null;
				if (entity != null) {
					map = parseResponse(entity);
				}
				return map;
			}

			@Override
			public void connectComplete(HashMap<Integer, String> hashMap) {
				
				if (hashMap == null || hashMap.isEmpty()) {
					return;
				}
				
				if (hashMap.containsKey(0)) {
					LogUtil.i("IdentifyCode", "获取验证码成功");
					// 获取验证码成功，则“请输入验证码”一栏设置成可以获取焦点
					etCode.setFocusable(true);
					etCode.setClickable(true);
					etCode.setEnabled(true);
				} else if (hashMap.containsKey(-2101)) {
					LogUtil.i("IdentifyCode", "终端未登记");
					etCode.setFocusable(false);
					etCode.setClickable(false);
					etCode.setEnabled(false);
				} else if (hashMap.containsKey(-2113)) {
					LogUtil.i("IdentifyCode", "MAC地址不匹配");
					etCode.setFocusable(false);
					etCode.setClickable(false);
					etCode.setEnabled(false);
				} else if (hashMap.containsKey(-2115)) {
					LogUtil.i("IdentifyCode", "生成验证码失败");
					etCode.setFocusable(false);
					etCode.setClickable(false);
					etCode.setEnabled(false);
				} else {
					LogUtil.i("IdentifyCode", "响应码为0，生成验证码失败");
					ToastUtils.showToast(UserRegisterActivity.this, "生成验证码失败，请检查网络！");
				}
			}
		}; 
		SimpleWorkPoolInstance.instance().execute(work);
	}
	
	private HashMap<Integer, String> parseResponse(HttpEntity entity) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		try {
			String entityString = EntityUtils.toString(entity, "UTF-8");
			JSONObject jsonObject = new JSONObject(entityString);
			
			JSONObject json = jsonObject.getJSONObject("Response");
			JSONObject object = json.getJSONObject("Header");
			
			int rc = object.getInt("RC");
			String rm = object.getString("RM");
			// 将RC作为key，RM作为value
			map.put(rc, rm);
		} catch (ParseException e) {
			LogUtil.d("parserLoginResponse::", "解析异常");
		} catch (IOException e) {
			LogUtil.d("parserLoginResponse::", "解析时IO异常");
		} catch (JSONException e) {
			LogUtil.d("parserLoginResponse::", "解析时Json异常");
		}
		return map;
	}
	
	private void register() {
		String mac = DbstarUtil.getLocalMacAddress(true);
		String md5String = MD5.getMD5("OEM$" + deviceModel + "$" + productSN + "$" + mac);
		// 参数
		List<NameValuePair> paramsList = new LinkedList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("TERMINALUNIQUE", "OEM$" + deviceModel + "$" + productSN));
		paramsList.add(new BasicNameValuePair("TERMINALPROFILE", "1.0.0.0"));
		paramsList.add(new BasicNameValuePair("AUTHENTICATOR", md5String));
		paramsList.add(new BasicNameValuePair("MAC", mac));
		paramsList.add(new BasicNameValuePair("CUSTOMERNAME", etName.getText().toString()));
		paramsList.add(new BasicNameValuePair("MOBILPHONE", etPhone.getText().toString()));
		paramsList.add(new BasicNameValuePair("SEX", String.valueOf(getSex(rgSex.getCheckedRadioButtonId()))));
		paramsList.add(new BasicNameValuePair("CODE", etCode.getText().toString()));

		// 对参数进行编码
		String param = URLEncodedUtils.format(paramsList, "UTF-8");
		String url = Constants.Server_Url_Register + param;
		
		ConnectWork<HashMap<Integer, String>> work = new ConnectWork<HashMap<Integer, String>>(HttpConnect.POST, url, paramsList) {
			
			@Override
			public HashMap<Integer, String> processResult(HttpEntity entity) {
				HashMap<Integer, String> map = null;
				if (entity != null) {
					map = parseResponse(entity);
				}
				return map;
			}

			@Override
			public void connectComplete(HashMap<Integer, String> hashMap) {
				
				if (hashMap == null || hashMap.isEmpty()) {
					return;
				}
				
				if (hashMap.containsKey(0)) {
					LogUtil.i("Register--Open", "开户成功");
					ToastUtils.showToast(UserRegisterActivity.this, "开户成功");
					// 开户成功之后，就跳转到海报页面
					Intent intent = new Intent();
					intent.setClass(UserRegisterActivity.this, DbstarOTTActivity.class);
					startActivity(intent);
				} else if (hashMap.containsKey(-2101)) {
					LogUtil.i("Register--Open", "终端未登记");
				} else if (hashMap.containsKey(-2113)) {
					LogUtil.i("Register--Open", "MAC地址不匹配");
				} else if (hashMap.containsKey(-2114)) {
					LogUtil.i("Register--Open", "验证码错误");
				} else if (hashMap.containsKey(-2102)) {
					LogUtil.i("Register--Open", "未开户");
				} else {
					LogUtil.i("Register--Open", "开户失败");					
				}
			}
		};
		SimpleWorkPoolInstance.instance().execute(work);
	}

	private void initView() {
		findViews();
		objectMap.put("0", R.id.register_userName);
		objectMap.put("0_1", R.id.register_rb_boy);
		objectMap.put("0_2", R.id.register_rb_girl);
		objectMap.put("1", R.id.register_userNum);
		objectMap.put("2", R.id.register_userCode);
		objectMap.put("3_1", R.id.register_imgcode);
		objectMap.put("3_2", R.id.register_imgregister);

		objectMap2.put("0", new OBean("0", "1", "0", "0_1", "0"));
		objectMap2.put("0_1", new OBean("0_1", "1", "0", "0_2", "0_1"));
		objectMap2.put("0_2", new OBean("0_2", "1", "0_1", "0_2", "0_2"));
		objectMap2.put("1", new OBean("0", "2", "1", "1", "1"));
		objectMap2.put("2", new OBean("1", "3_1", "2", "2", "2"));
		objectMap2.put("3_1", new OBean("2", "3_1", "3_1", "3_2", "3_1"));
		objectMap2.put("3_2", new OBean("2", "3_2", "3_1", "3_2", "3_2"));

		etName.requestFocus();
	}

	private void findViews() {
		etName = (EditText) findViewById(R.id.register_userName);
		rgSex = (RadioGroup) findViewById(R.id.register_rg_sex);
		rbBoy = (RadioButton) findViewById(R.id.register_rb_boy);
		rbGirl = (RadioButton) findViewById(R.id.register_rb_girl);
		etPhone = (EditText) findViewById(R.id.register_userNum);
		etCode = (EditText) findViewById(R.id.register_userCode);
		imgCode = (ImageView) findViewById(R.id.register_imgcode);
		imgRegister = (ImageView) findViewById(R.id.register_imgregister);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		OBean oBean = (OBean) objectMap2.get(curFocusPosition);
		if (oBean == null) {
			return true;
		}

		lastcurFocusPosition = curFocusPosition;
		switch (keyCode) {
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
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (oBean.getRight() != null && !"".equals(oBean.getRight())) {
				curFocusPosition = oBean.getRight();
			}
			break;
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_ESCAPE:
			break;
		}

		switch (keyCode) {
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
				if ("0".equals(curFocusPosition) || "0_1".equals(curFocusPosition) || "0_2".equals(curFocusPosition) || "1".equals(curFocusPosition) 
						|| "2".equals(curFocusPosition) || "3_1".equals(curFocusPosition) || "3_2".equals(curFocusPosition)) {
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

	private class TextWatcherWrapper implements TextWatcher {
		private EditText editText;

		public TextWatcherWrapper(EditText editText) {
			this.editText = editText;
		}

		@Override
		public void afterTextChanged(Editable edit) {
			String input = edit.toString();
			
			if (editText.getId() == R.id.register_userNum) {
				if (input.length() > 11) {
					edit.delete(11, editText.length());
					ToastUtils.showToast(UserRegisterActivity.this,"最多输入11位数字！");
				}				
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

	}
	
	private int getSex(int checkedId) {
		// 0--未知，1--男，2--女
		int sex = 0;
		if (checkedId == R.id.register_rb_boy) {
			sex = 1;
		} else if (checkedId == R.id.register_rb_girl) {
			sex = 2;
		}
		return sex;
	}
}
