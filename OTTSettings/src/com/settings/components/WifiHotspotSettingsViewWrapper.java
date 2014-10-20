package com.settings.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.settings.bean.WifiHotspot;
import com.settings.bean.WifiHotspotConfig;
import com.settings.ottsettings.R;
import com.settings.utils.DataUtils;
import com.settings.utils.LogUtil;
import com.settings.utils.SettingUtils;
import com.settings.utils.ToastUtils;
import com.settings.wifihotspot.WifiAdmin;
import com.settings.wifihotspot.WifiApAdmin;

public class WifiHotspotSettingsViewWrapper {
	
	private static final String TAG = "WifiHotspotSettingsViewWrapper";
	
	private static final String Data_Key_SSID = "com.settings.ssid";
	private static final String Data_Key_SECURITY = "com.settings.security";
	private static final String Data_Key_PWD = "com.settings.password";
	
	private Button btnSetHotsopt;
	private EditText mSSID;
	private Spinner mSecurity;
	private EditText mPassword;
	private CheckBox mShowPwd;
	private Button mBtnOk, mBtnCancel;
	
	private Context mContext;
	private WifiManager mWifiManager;
	private ArrayAdapter<String> mSecurityAdapter;
	private String[] mTxtSecurity = new String[]{"Open", "WPA PSK", "WPA2 PSK"};
	private WifiHotspot wifiHotspot = null;
	
	private WifiAdmin wifiAdmin;

	private WifiApAdmin wifiAp;

	public WifiHotspotSettingsViewWrapper(Context context) {
		this.mContext = context;
		mWifiManager =  (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	}

	public void initView(View view) {
		findViews(view);
		
		btnSetHotsopt.requestFocus();
		unenableSetWifiHotspot();
		
		wifiHotspot = new WifiHotspot();
		String ssid = DataUtils.getPreference(mContext, Data_Key_SSID, "DbstarAP");
		String password = DataUtils.getPreference(mContext, Data_Key_PWD, "12345678");
		String security = DataUtils.getPreference(mContext, Data_Key_SECURITY, "WPA PSK");
		wifiHotspot.setSsid(ssid);
		wifiHotspot.setPassword(password);
		wifiHotspot.setSecurity(security);
		
		if (wifiHotspot != null) {
			mSSID.setText(wifiHotspot.getSsid());
			mPassword.setText(wifiHotspot.getPassword());			
		}
		
		mSecurityAdapter = new ArrayAdapter<String>(mContext, R.layout.lt_common_spinner_item, mTxtSecurity){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				if (view != null && view instanceof TextView) {
					TextView textView = (TextView) view;
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.gl_text_size_22sp));
				}
				return view;
			}
			
			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				TextView txtSecurity;
				if (convertView == null) {
					LayoutInflater inflater = LayoutInflater.from(mContext);
					convertView = inflater.inflate(R.layout.lt_common_spinner_item, null);
				}
				txtSecurity = (TextView) convertView.findViewById(R.id.txt_spinner);
				txtSecurity.setBackgroundResource(R.drawable.lt_common_rectangle_txt_selector);
				txtSecurity.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.gl_text_size_22sp));
				txtSecurity.setText(getItem(position));
				return convertView;
			}
		};
		
		mSecurity.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					wifiHotspot.setSecurity(mTxtSecurity[0]);
					// TODO:
					break;

				case 1:
					wifiHotspot.setSecurity(mTxtSecurity[1]);
					
					break;
				case 2:
					wifiHotspot.setSecurity(mTxtSecurity[2]);
					
					break;
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		mSecurity.setAdapter(mSecurityAdapter);
		// 安全性默认是WPA2 PSK
		mSecurity.setSelection(2);
		
		wifiHotspot.setSecurity((String) mSecurity.getSelectedItem());
		
//		DataUtils.savePreference(mContext, Data_Key_SSID, wifiHotspot.getSsid());
//		DataUtils.savePreference(mContext, Data_Key_PWD, wifiHotspot.getPassword());
//		DataUtils.savePreference(mContext, Data_Key_SECURITY, wifiHotspot.getSecurity());
		
		wifiHotspotConnect(wifiHotspot);
		
		setEventListener(view);
	}
	
	private void setEventListener(View view) {
		
		OnEditFocusChangeListner onEditFocusChangeListner = new OnEditFocusChangeListner();
		mSSID.setOnFocusChangeListener(onEditFocusChangeListner);
		mPassword.setOnFocusChangeListener(onEditFocusChangeListner);
		
		mShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					LogUtil.d(TAG, "mShowPwd-----" + isChecked + "===pwd is show");
				} else {
					mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
					LogUtil.d(TAG, "mShowPwd-----" + isChecked + "===pwd is hidden");
				}
				mPassword.postInvalidate();
			}
		});
		
		// TODO:
		mBtnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				wifiHotspotConnect(wifiHotspot);
				
				ToastUtils.showToast(mContext, R.string.page_wifi_hotspot_settingsOk);
				
				btnSetHotsopt.requestFocus();
				unenableSetWifiHotspot();
				
				if (WifiHotspotConfig.getInstance(mContext).shouldRestoreWifiHotspot()) {
//					DataUtils.savePreference(mContext, Data_Key_IsOpenWifiHotspot, true);
					DataUtils.savePreference(mContext, Data_Key_SSID, wifiHotspot.getSsid());
					DataUtils.savePreference(mContext, Data_Key_PWD, wifiHotspot.getPassword());
					DataUtils.savePreference(mContext, Data_Key_SECURITY, wifiHotspot.getSecurity());
				}
				
			}
		});
		
		mBtnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnSetHotsopt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				enableSetWifiHotspot();
				mSSID.requestFocus();
			}
		});
	}
	
	private int getTypeOfSecurity(WifiHotspot wifiHotspot) {
		int type = WifiAdmin.TYPE_WPA;
		if (wifiHotspot != null) {
			if (wifiHotspot.getSecurity().equals("Open")) {
				type = WifiAdmin.TYPE_NO_PASSWD;
			} else if (wifiHotspot.getSecurity().equals("WPA PSK")) {
				type = WifiAdmin.TYPE_WEP;								
			} else {				
				type = WifiAdmin.TYPE_WPA;				
			}
		}
		return type;
	}

	private void findViews(View view) {
		btnSetHotsopt = (Button) view.findViewById(R.id.wifi_hotspot_settings);
		mSSID = (EditText) view.findViewById(R.id.wifi_hotspot_et_ssid);
		mSecurity = (Spinner) view.findViewById(R.id.wifi_hotspot_security_spinner);
		mPassword = (EditText) view.findViewById(R.id.wifi_hotspot_et_password);
		mShowPwd = (CheckBox) view.findViewById(R.id.wifi_hotspot_cb_showPwd);
		mBtnOk = (Button) view.findViewById(R.id.wifi_hotspot_btn_ok);
		mBtnCancel = (Button) view.findViewById(R.id.wifi_hotspot_btn_cancel);
	}
	
	private void wifiHotspotConnect(WifiHotspot wifiHotspot) {
		wifiAp = new WifiApAdmin(mContext);
		wifiAp.startWifiAp(wifiHotspot.getSsid(), wifiHotspot.getPassword());
		
		wifiAdmin = new WifiAdmin(mContext) {
			
			@Override
			public void onNotifyWifiConnected() {
				LogUtil.d("OTTSettingsActivity", "have connected success!");
				LogUtil.d("OTTSettingsActivity", "###############################");
			}
			
			@Override
			public void onNotifyWifiConnectFailed() {
				LogUtil.d("OTTSettingsActivity", "have connected failed!");
				LogUtil.d("OTTSettingsActivity", "###############################");
			}
			
			@Override
			public Intent myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter) {
				mContext.registerReceiver(receiver, filter);
				return null;
			}
			
			@Override
			public void myUnregisterReceiver(BroadcastReceiver receiver) {
				mContext.unregisterReceiver(receiver);
			}					
		};
		
		wifiAdmin.openWifi();
		wifiAdmin.addNetwork(wifiHotspot.getSsid(), wifiHotspot.getPassword(), getTypeOfSecurity(wifiHotspot));
//				wifiAdmin.addNetwork(wifiHotspot.getSsid(), wifiHotspot.getPassword(),  WifiAdmin.TYPE_WPA);
	}

	private void unenableSetWifiHotspot() {
		mSSID.setEnabled(false);
		mSecurity.setEnabled(false);
		mPassword.setEnabled(false);
		mShowPwd.setEnabled(false);
		mBtnOk.setEnabled(false);
		btnSetHotsopt.requestFocus();
	}

	private void enableSetWifiHotspot() {
		mSSID.setEnabled(true);
		mSecurity.setEnabled(true);
		mPassword.setEnabled(true);
		mShowPwd.setEnabled(true);
		mBtnOk.setEnabled(true);
		
		btnSetHotsopt.setNextFocusUpId(R.id.wifi_hotspot_btn_ok);
		btnSetHotsopt.setNextFocusDownId(R.id.wifi_hotspot_et_ssid);
		mBtnOk.setNextFocusDownId(R.id.wifi_hotspot_settings);
	}

	private class OnEditFocusChangeListner implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus || !(v instanceof EditText)) {
				return;
			}
			
			// 保存输入框中的参数值设置。
			saveEditTextValue((EditText) v);
		}

		private void saveEditTextValue(EditText txtInput) {
			if (SettingUtils.hasEmpty(txtInput, txtInput.getText())) {
				ToastUtils.showToast(mContext, R.string.page_wifi_hotspot_validation);
				setFocus(txtInput);
				return;
			}
			
			String inputText = txtInput.getText().toString();
			if (wifiHotspot == null) {
				wifiHotspot = new WifiHotspot();
			}
			
			switch (txtInput.getId()) {
			case R.id.wifi_hotspot_et_ssid:
				if (wifiHotspot.getSsid().equals(inputText)) {
					return;
				}
				if (inputText.equals("") || inputText.equals("null")) {
					ToastUtils.showToast(mContext, R.string.page_wifi_hotspot_validation);
					setFocus(txtInput);
					return;
				}
				wifiHotspot.setSsid(inputText);
				break;
			case R.id.wifi_hotspot_et_password:
				if (wifiHotspot.getPassword().equals(inputText)) {
					return;
				}
				if (inputText.length() < 8) {
					ToastUtils.showToast(mContext, R.string.page_wifi_hotspot_pwd_validation);
					setFocus(txtInput);
					return;
				}
				wifiHotspot.setPassword(inputText);
				break;

			default:
				break;
			}
			
		}

		private void setFocus(final EditText txtInput) {
			if (txtInput != null) {
				txtInput.post(new Runnable() {
					
					@Override
					public void run() {
						txtInput.requestFocus();
					}
				});
			}
		}
		
	}
}
