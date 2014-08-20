package com.settings.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.settings.ottsettings.R;
import com.settings.utils.LogUtil;
import com.settings.utils.ToastUtils;

public class CheckedWifiListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<ScanResult> list;

	public CheckedWifiListAdapter(Context context, List<ScanResult> list) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.list = list;
	}

	public void refresh(List<ScanResult> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		if (list == null || list.size() == 0)
			return 0;
		else
			return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.lt_common_wifi_item, null);

		if (list == null) {
			return null;
		}

		final ScanResult scanResult = list.get(position);
		TextView textView = (TextView) view.findViewById(R.id.wifi_settings_item_wifiName);
		textView.setText(scanResult.SSID);
		// 强度
		TextView txtStrenth = (TextView) view.findViewById(R.id.wifi_settings_item_strenth);
		txtStrenth.setText(String.valueOf(Math.abs(scanResult.level)));

		ImageView imgView = (ImageView) view.findViewById(R.id.wifi_settings_item_img);
		// 判断信号强度，显示对应的只是图标
		int level = Math.abs(scanResult.level);
		if (level > 100) {
			imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.wifi_level_05));
		} else if (level > 70 && level <= 100) {
			imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.wifi_level_04));
		} else if (level > 50 && level <= 70) {
			imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.wifi_level_03));
		} else if (level > 30 && level <= 50) {
			imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.wifi_level_02));
		} else {
			imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.wifi_level_01));
		}

		RelativeLayout container = (RelativeLayout) view.findViewById(R.id.wifi_settings_item_container);

		container.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LogUtil.d("CheckedWifiListAdapter", "wifiContainer-----===click container should show dialog");
				LayoutInflater inflater = LayoutInflater.from(context);
				final View view = inflater.inflate(R.layout.lt_common_wifi_dialog, null);
				TextView titleView = (TextView) view.findViewById(R.id.dialog_wifi_name);
				final EditText etPwd = (EditText) view.findViewById(R.id.dialog_wifi_et_password);
				final CheckBox cbShowPwd = (CheckBox) view.findViewById(R.id.dialog_wifi_cb_show_password);
				
				titleView.setText(scanResult.SSID);
			
				cbShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// 是否显示密码
						if (isChecked) {
							etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
							LogUtil.d("CheckedWifiListAdapter", "cbShowPwd-----" + isChecked + "===pwd is show");
						} else {						
							etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());					
							LogUtil.d("CheckedWifiListAdapter", "cbShowPwd-----" + isChecked + "===pwd is hidden");
						}
						etPwd.postInvalidate();
					}
				});
				
				AlertDialog dialog = new AlertDialog.Builder(context)
						.setTitle(scanResult.SSID).setView(view)
						.setPositiveButton(context.getString(R.string.dialog_txt_ok), new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int which) {
//										if (etPwd.getText() == null || etPwd.getText().equals("")) {
//											ToastUtils.showToast(context, R.string.dialog_password_cannot_null);
//										} else {
//											//TODO:点击确定之后要连接wifi，怎么连呢？
//											
////											dialog.dismiss();
//										}
										
										// TODO:现在即使不输入密码，点击确定按钮dialog还是会消失
									}
								})
						.setNegativeButton(context.getString(R.string.dialog_txt_cancel), null).create();
				dialog.show();
				
				// 不知道为什么在这个地方添加的监听器会不好使
				TextWatcherWrapper watcher = new TextWatcherWrapper(etPwd, cbShowPwd, dialog);
				etPwd.addTextChangedListener(watcher);
				
//				 //切换后将EditText光标置于末尾
//                CharSequence charSequence = etPwd.getText();
//                if (charSequence instanceof Spannable) {
//                    Spannable spanText = (Spannable) charSequence;
//                    Selection.setSelection(spanText, charSequence.length());
//                }
			}
		});

		return view;
	}

	class TextWatcherWrapper implements TextWatcher {
		
		private EditText etPwd;
		private AlertDialog dialog;
		
		public TextWatcherWrapper(EditText etPwd, CheckBox cbShowPwd, AlertDialog dialog) {
			this.etPwd = etPwd;
			this.dialog = dialog;
		}

		@Override
		public void afterTextChanged(Editable editable) {
			if ((editable.toString()).length() == 0 || (editable.toString()).equals("")) {
				// 没有监听到
				ToastUtils.showToast(context, R.string.dialog_password_cannot_null);
				dialog.isShowing();
			} else {
				//TODO:点击确定之后要连接wifi，怎么连呢？
				
//				dialog.dismiss();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}
}
