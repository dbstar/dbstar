package com.settings.components;


public class WifiHotspotSettingsView {
//	
//	private static final String TAG = "WifiHotspotSettingsViewWrapper";
//	
//	private static final String Data_Key_SSID = "com.settings.ssid";
//	private static final String Data_Key_SECURITY = "com.settings.security";
//	private static final String Data_Key_PWD = "com.settings.password";
//	private static final String Data_Key_IsOpenWifiHotspot = "com.settings.isOpenWifiHotspot";
//	
//	private CheckBox mOpenHotspot;
//	private LinearLayout mTxtContainer;
//	private LinearLayout mSettingsContainer;
//	private TextView txtopen, txtSettingsContent;
//	private EditText mSSID;
//	private Spinner mSecurity;
//	private EditText mPassword;
//	private CheckBox mShowPwd;
//	private Button mBtnOk, mBtnCancel;
//	
//	private Context mContext;
//	private WifiManager mWifiManager;
//	private ArrayAdapter<String> mSecurityAdapter;
//	private String[] mTxtSecurity = new String[]{"Open", "WPA PSK", "WPA2 PSK"};
//	private WifiHotspot wifiHotspot = null;
//	
//	private boolean isOpenWifiHotspot = false;
//	private WifiAdmin wifiAdmin;
//
//	public WifiHotspotSettingsView(Context context) {
//		this.mContext = context;
//		mWifiManager =  (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//	}
//
//	public void initView(View view) {
//		findViews(view);
//		
//		mSettingsContainer.setVisibility(View.GONE);
//		mOpenHotspot.requestFocus();
//		
//		wifiHotspot = new WifiHotspot();
//		String ssid = DataUtils.getPreference(mContext, Data_Key_SSID, "DbstarAP");
//		String password = DataUtils.getPreference(mContext, Data_Key_PWD, "12345678");
//		String security = DataUtils.getPreference(mContext, Data_Key_SECURITY, "WPA PSK");
//		wifiHotspot.setSsid(ssid);
//		wifiHotspot.setPassword(password);
//		wifiHotspot.setSecurity(security);
//		
//		boolean isOpenWifiHotspot = DataUtils.getPreference(mContext, Data_Key_IsOpenWifiHotspot, false);
//		LogUtil.d(TAG, "initView   isOpenWifiHotspot------== ==" + isOpenWifiHotspot);
//		if (WifiHotspotConfig.getInstance(mContext).shouldRestoreWifiHotspot() && isOpenWifiHotspot) {
//			// 如果选中，则可以点击
//			LogUtil.d(TAG, "initView   isOpenWifiHotspot------==mOpenHotspot.isChecked() == true");
//			mOpenHotspot.setChecked(true);
//			
//			mTxtContainer.setVisibility(View.VISIBLE);
//		} else {
//			// 如果mOpenHotspot没有选中，则“设置Wi-Fi热点”不可点击。
//			LogUtil.d(TAG, "initView   isOpenWifiHotspot------==mOpenHotspot.isChecked() == false");
//			mOpenHotspot.setChecked(false);	
//			mTxtContainer.setVisibility(View.GONE);
//			mSettingsContainer.setVisibility(View.GONE);			
//		}
//		
//		if (wifiHotspot != null) {
//			mSSID.setText(wifiHotspot.getSsid());
//			mPassword.setText(wifiHotspot.getPassword());			
//		}
//		
//		mSecurityAdapter = new ArrayAdapter<String>(mContext, R.layout.lt_common_spinner_item, mTxtSecurity){
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				View view = super.getView(position, convertView, parent);
//				if (view != null && view instanceof TextView) {
//					TextView textView = (TextView) view;
//					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.gl_text_size_22sp));
//				}
//				return view;
//			}
//			
//			@Override
//			public View getDropDownView(int position, View convertView, ViewGroup parent) {
//				TextView txtSecurity;
//				if (convertView == null) {
//					LayoutInflater inflater = LayoutInflater.from(mContext);
//					convertView = inflater.inflate(R.layout.lt_common_spinner_item, null);
//				}
//				txtSecurity = (TextView) convertView.findViewById(R.id.txt_spinner);
//				txtSecurity.setBackgroundResource(R.drawable.lt_common_rectangle_txt_selector);
//				txtSecurity.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.gl_text_size_22sp));
//				txtSecurity.setText(getItem(position));
//				return convertView;
//			}
//		};
//		
//		mSecurity.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				switch (position) {
//				case 0:
//					wifiHotspot.setSecurity(mTxtSecurity[0]);
//					// TODO:
//					break;
//
//				case 1:
//					wifiHotspot.setSecurity(mTxtSecurity[1]);
//					
//					break;
//				case 2:
//					wifiHotspot.setSecurity(mTxtSecurity[2]);
//					
//					break;
//				}
//				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//			}
//		});
//		
//		mSecurity.setAdapter(mSecurityAdapter);
//		// 安全性默认是WPA2 PSK
//		mSecurity.setSelection(2);
//		
//		wifiHotspot.setSecurity((String) mSecurity.getSelectedItem());
//		
//		setEventListener(view);
//	}
//	
//	private void setEventListener(View view) {
//		
//		OnEditFocusChangeListner onEditFocusChangeListner = new OnEditFocusChangeListner();
//		mSSID.setOnFocusChangeListener(onEditFocusChangeListner);
//		mPassword.setOnFocusChangeListener(onEditFocusChangeListner);
//		
//		mShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if (isChecked) {
//					mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//					LogUtil.d(TAG, "mShowPwd-----" + isChecked + "===pwd is show");
//				} else {
//					mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//					LogUtil.d(TAG, "mShowPwd-----" + isChecked + "===pwd is hidden");
//				}
//				mPassword.postInvalidate();
//			}
//		});
//		
//		mOpenHotspot.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if (isChecked) {
//					mTxtContainer.setVisibility(View.VISIBLE);
//					mSettingsContainer.setVisibility(View.VISIBLE);
//					
//					wifiHotspotConnected();
//				} else {
//					mTxtContainer.setVisibility(View.GONE);
//					mSettingsContainer.setVisibility(View.GONE);
//					
//					WifiApAdmin.closeWifiAp(mContext);
//					if (!mWifiManager.isWifiEnabled()) {						
//						mWifiManager.setWifiEnabled(true);
//					}
//					
//				}
//				
//				DataUtils.savePreference(mContext, Data_Key_IsOpenWifiHotspot, isChecked);
//				WifiHotspotConfig.getInstance(mContext).setRestoreWifiHotspot(mContext, isChecked);
//				
//				DataUtils.savePreference(mContext, Data_Key_SSID, wifiHotspot.getSsid());
//				DataUtils.savePreference(mContext, Data_Key_PWD, wifiHotspot.getPassword());
//				DataUtils.savePreference(mContext, Data_Key_SECURITY, wifiHotspot.getSecurity());
//			}
//		});
//		
//		// TODO:
//		mBtnOk.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				wifiHotspotConnected();
////				wifiAdmin.addNetwork(wifiHotspot.getSsid(), wifiHotspot.getPassword(),  WifiAdmin.TYPE_WPA);
//				
//				ToastUtils.showToast(mContext, R.string.page_wifi_hotspot_settingsOk);
//				
//				if (WifiHotspotConfig.getInstance(mContext).shouldRestoreWifiHotspot()) {
//					LogUtil.d(TAG, "mBtnOk   mOpenHotspot.isChecked()------== ==" + mOpenHotspot.isChecked());
////					DataUtils.savePreference(mContext, Data_Key_IsOpenWifiHotspot, true);
//					DataUtils.savePreference(mContext, Data_Key_SSID, wifiHotspot.getSsid());
//					DataUtils.savePreference(mContext, Data_Key_PWD, wifiHotspot.getPassword());
//					DataUtils.savePreference(mContext, Data_Key_SECURITY, wifiHotspot.getSecurity());
//				}
//				
//				mTxtContainer.requestFocus();
//				mSettingsContainer.setVisibility(View.GONE);
//			}
//		});
//		
//		mBtnCancel.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		
//		mTxtContainer.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
////				mSSID.setEnabled(true);
////				mSecurity.setEnabled(true);
////				mPassword.setEnabled(true);
////				mShowPwd.setEnabled(true);
////				mBtnOk.setEnabled(true);
//				
//				mSettingsContainer.setVisibility(View.VISIBLE);
//			}
//		});
//	}
//	
//	private int getTypeOfSecurity(WifiHotspot wifiHotspot) {
//		int type = WifiAdmin.TYPE_WPA;
//		if (wifiHotspot != null) {
//			if (wifiHotspot.getSecurity().equals("Open")) {
//				type = WifiAdmin.TYPE_NO_PASSWD;
//			} else if (wifiHotspot.getSecurity().equals("WPA PSK")) {
//				type = WifiAdmin.TYPE_WEP;								
//			} else {				
//				type = WifiAdmin.TYPE_WPA;				
//			}
//		}
//		return type;
//	}
//
//	private void findViews(View view) {
////		txtopen = (TextView) view.findViewById(R.id.wifi_hotspot_content);
//		txtSettingsContent = (TextView) view.findViewById(R.id.wifi_hotspot_settings_content);
//		mOpenHotspot = (CheckBox) view.findViewById(R.id.wifi_hotspot_cb_select);
//		mTxtContainer = (LinearLayout) view.findViewById(R.id.wifi_hotspot_settings_container);
//		mSettingsContainer = (LinearLayout) view.findViewById(R.id.wifi_hotspot_settings_setContainer);
//		mSSID = (EditText) view.findViewById(R.id.wifi_hotspot_et_ssid);
//		mSecurity = (Spinner) view.findViewById(R.id.wifi_hotspot_security_spinner);
//		mPassword = (EditText) view.findViewById(R.id.wifi_hotspot_et_password);
//		mShowPwd = (CheckBox) view.findViewById(R.id.wifi_hotspot_cb_showPwd);
//		mBtnOk = (Button) view.findViewById(R.id.wifi_hotspot_btn_ok);
//		mBtnCancel = (Button) view.findViewById(R.id.wifi_hotspot_btn_cancel);
//	}
//	
//	private void wifiHotspotConnected() {
//		WifiApAdmin wifiAp = new WifiApAdmin(mContext);
////		wifiAp.startWifiAp("\"HotSpot\"", "hhhhhh123");
//		wifiAp.startWifiAp(wifiHotspot.getSsid(), wifiHotspot.getPassword());
//		
//		wifiAdmin = new WifiAdmin(mContext) {
//			
//			@Override
//			public void onNotifyWifiConnected() {
//				LogUtil.d("OTTSettingsActivity", "have connected success!");
//				LogUtil.d("OTTSettingsActivity", "###############################");
//				
//			}
//			
//			@Override
//			public void onNotifyWifiConnectFailed() {
//				LogUtil.d("OTTSettingsActivity", "have connected failed!");
//				LogUtil.d("OTTSettingsActivity", "###############################");
//				
//			}
//			
//			@Override
//			public Intent myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter) {
//				mContext.registerReceiver(receiver, filter);
//				return null;
//			}
//			
//			@Override
//			public void myUnregisterReceiver(BroadcastReceiver receiver) {
//				mContext.unregisterReceiver(receiver);
//			}					
//		};
//		
//		wifiAdmin.openWifi();
//		wifiAdmin.addNetwork(wifiHotspot.getSsid(), wifiHotspot.getPassword(), getTypeOfSecurity(wifiHotspot));
//	}
//
//	private class OnEditFocusChangeListner implements OnFocusChangeListener {
//
//		@Override
//		public void onFocusChange(View v, boolean hasFocus) {
//			if (hasFocus || !(v instanceof EditText)) {
//				return;
//			}
//			
//			// 保存输入框中的参数值设置。
//			saveEditTextValue((EditText) v);
//		}
//
//		private void saveEditTextValue(EditText txtInput) {
//			if (SettingUtils.hasEmpty(txtInput, txtInput.getText())) {
//				ToastUtils.showToast(mContext, R.string.page_wifi_hotspot_validation);
//				setFocus(txtInput);
//				return;
//			}
//			
//			String inputText = txtInput.getText().toString();
//			if (wifiHotspot == null) {
//				wifiHotspot = new WifiHotspot();
//			}
//			
//			switch (txtInput.getId()) {
//			case R.id.wifi_hotspot_et_ssid:
//				if (wifiHotspot.getSsid().equals(inputText)) {
//					return;
//				}
//				if (inputText.equals("") || inputText.equals("null")) {
//					ToastUtils.showToast(mContext, R.string.page_wifi_hotspot_validation);
//					setFocus(txtInput);
//					return;
//				}
//				wifiHotspot.setSsid(inputText);
//				break;
//			case R.id.wifi_hotspot_et_password:
//				if (wifiHotspot.getPassword().equals(inputText)) {
//					return;
//				}
//				if (inputText.length() < 8) {
//					ToastUtils.showToast(mContext, R.string.page_wifi_hotspot_pwd_validation);
//					setFocus(txtInput);
//					return;
//				}
//				wifiHotspot.setPassword(inputText);
//				break;
//
//			default:
//				break;
//			}
//			
//		}
//
//		private void setFocus(final EditText txtInput) {
//			if (txtInput != null) {
//				txtInput.post(new Runnable() {
//					
//					@Override
//					public void run() {
//						txtInput.requestFocus();
//					}
//				});
//			}
//		}
//		
//	}
}
