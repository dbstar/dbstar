package com.settings.components;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.settings.bean.UpgradeInfo;
import com.settings.bean.Vapks;
import com.settings.http.HttpConnect;
import com.settings.http.SimpleWorkPool.ConnectWork;
import com.settings.http.SimpleWorkPool.SimpleWorkPoolInstance;
import com.settings.ottsettings.R;
import com.settings.utils.Constants;
import com.settings.utils.DataUtils;
import com.settings.utils.LogUtil;
import com.settings.utils.MD5;
import com.settings.utils.SettingUtils;
import com.settings.utils.ToastUtils;

public class SysUpgradeSettingsViewWrapper {

	private Context context;
	private Button btnLocal;
	private Button btnOnline;
	private TextView txtPercent;
	
	private String localUpgradeFilePath;
	private UpgradeInfo mUpgradeInfo = null;
	private String productSN = null, deviceModel = null, softVersion = null;
	public static boolean isNeedSysUpgrade = true;
	private boolean isUpgrading;
	
	private static float downloadSize = 0;
	private static long downloadFileSize = 0;
	private static long fileTotalSize;
	
	public SysUpgradeSettingsViewWrapper(Context context) {
		this.context = context;
	}

	public void initView(View view) {
		btnLocal = (Button) view.findViewById(R.id.sysUpgrade_settings_btn_local_upgrade);
		btnOnline = (Button) view.findViewById(R.id.sysUpgrade_settings_btn_online_upgrade);
		txtPercent = (TextView) view.findViewById(R.id.sysUpgrade_settings_download_percent);
		
		btnLocal.setEnabled(false);
		btnOnline.setEnabled(false);
		txtPercent.setText(context.getString(R.string.page_sysUpgrade_isChecking));
		
		String content = DataUtils.getCacheContent();
		if (content != null && content.length() > 0) {
			String[] splits = content.split("\n");
			if (splits != null && splits.length >= 2) {
				productSN = splits[0];
				softVersion = splits[1];
				deviceModel = splits[2];
			}
		}
		
		if (softVersion == null || softVersion.equals("")) {
			softVersion = "2.0.3.1";
		}
		
		if (productSN == null || productSN.equals("")) {
			// TODO:现在的串号不标准
			productSN = "0000000000000066";
		}
		
		if (deviceModel == null || deviceModel.equals("")) {
			deviceModel = "02";
		}
		
		resume();
		populateData();
		setEventListener();
	}

	BroadcastReceiver onReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
//			LogUtil.d("SysUpgradeSettingsViewWrapper", "-------------------downloadSize = " + downloadSize);
			if (action.equals(SettingUtils.Sys_Upgrade_Settings_Progress)) {
				Message msg = handler.obtainMessage(1);
				msg.obj = intent.getLongExtra("has_recv", 0);
				handler.sendMessage(msg);
			} else if (action.equals(SettingUtils.Sys_Auto_Upgrade_Settings_Progress)) {
				Message msg = handler.obtainMessage(2);
				Bundle bundle = new Bundle();
				bundle.putLong("fileTotalSize", intent.getLongExtra("fileTotalSize", fileTotalSize));
				bundle.putLong("has_recv", intent.getLongExtra("has_recv", 0));
				msg.setData(bundle);
				handler.sendMessage(msg);
			} else if (action.equals(SettingUtils.Sys_Upgrade_Settings_Upgrade)) {
				Message message = handler.obtainMessage(3);
				Bundle bundle = new Bundle();
				bundle.putBoolean("isUpgrading", intent.getBooleanExtra("isUpgrading", false));
				bundle.putLong("fileTotalSize", intent.getLongExtra("fileTotalSize", 0));
				handler.sendMessage(message);
			} else if (action.equals(SettingUtils.Sys_Upgrade_Settings_Upgrade_Failed)) {
				handler.sendEmptyMessage(4);
			}
		}
	};
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			final NumberFormat percentFormat = NumberFormat.getPercentInstance();
			percentFormat.setMaximumFractionDigits(1); // 最大小数位数
			percentFormat.setMaximumIntegerDigits(3); // 最大整数位数
			percentFormat.setMinimumFractionDigits(0); // 最小小数位数
			percentFormat.setMinimumIntegerDigits(1); // 最小整数位数
			
			boolean isAvaliable = SettingUtils.isNetworkAvailable(context);
			
			switch (msg.what) {
			case 1:
				if (isAvaliable) {
					if (fileTotalSize > 0) {
						downloadFileSize = (Long) msg.obj;
						downloadSize = ((Long) msg.obj).floatValue() / fileTotalSize;
						String percentNum = percentFormat.format(downloadSize);
						txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_percent, percentNum));				
						btnOnline.setEnabled(false);
					} else {
						LogUtil.d("SysUpgradeSettingsViewWrapper", "--------1-------------------------fileTotalSize = " + fileTotalSize);
						txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_failed));
						btnOnline.setEnabled(true);
					}					
				} else { // 当网络断开的时候，下载失败
					fileTotalSize = 0;
					SettingUtils.save0ToFile();
					btnOnline.setEnabled(false);
					txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_check_upgrade_failed_byNetwork));										
//					ToastUtils.showToast(context, "请检查网络！");					
				}
				break;
			case 2:
				Bundle bundle = msg.getData();
				downloadFileSize = bundle.getLong("has_recv");
				fileTotalSize = bundle.getLong("fileTotalSize");
				
				if (isAvaliable) {					
					if (fileTotalSize > 0) {
						downloadSize = ((Long) downloadFileSize).floatValue() / fileTotalSize;
						String percentNum = percentFormat.format(downloadSize);
						txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_percent, percentNum));										
						btnOnline.setEnabled(false);
					} else {
						LogUtil.d("SysUpgradeSettingsViewWrapper", "-------2--------------------------fileTotalSize = " + fileTotalSize);
						txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_failed));						
						btnOnline.setEnabled(true);
					}
				} else {
					fileTotalSize = 0;
					SettingUtils.save0ToFile();
					btnOnline.setEnabled(false);
					txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_check_upgrade_failed_byNetwork));										
					ToastUtils.showToast(context, "请检查网络！");					
				}
				break;
			case 3:
				Bundle bundle2 = msg.getData();
				if (bundle2 != null) {
					isUpgrading = bundle2.getBoolean("isUpgrading");
					fileTotalSize = bundle2.getLong("fileTotalSize");					
				}
				
				if (isUpgrading) {
					btnLocal.setEnabled(false);
					btnOnline.setEnabled(false);
				}
				
				// 当网络断开的时候，下载失败
				if (!isAvaliable) {
					fileTotalSize = 0;
					SettingUtils.save0ToFile();
					btnOnline.setEnabled(false);
					txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_check_upgrade_failed_byNetwork));										
//					ToastUtils.showToast(context, "请检查网络！");
				}
				break;
			case 4:
				LogUtil.d("SysUpgradeSettingsViewWrapper", "-------4--------------------------fileTotalSize = " + fileTotalSize);
				if (isAvaliable) {
					btnOnline.setEnabled(true);
					txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_failed));					
				} else {
					SettingUtils.save0ToFile();
					btnOnline.setEnabled(false);
					LogUtil.d("SysUpgradeSettingsViewWrapper", "-------4--------------------------isAvaliable = " + isAvaliable);
					txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_check_upgrade_failed_byNetwork));					
				}
				break;
			default:
				break;
			}
		}
	};
	
	public void resume() {
		IntentFilter filter = new IntentFilter();		
		filter.addAction(SettingUtils.Sys_Upgrade_Settings_Upgrade);
		filter.addAction(SettingUtils.Sys_Upgrade_Settings_Upgrade_Failed);
		filter.addAction(SettingUtils.Sys_Upgrade_Settings_Progress);
		filter.addAction(SettingUtils.Sys_Auto_Upgrade_Settings_Progress);
		context.registerReceiver(onReceiver, filter);
	}
	
	public void pause() {
		context.unregisterReceiver(onReceiver);
	}

	private void populateData() {
		final NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1); // 最大小数位数
		percentFormat.setMaximumIntegerDigits(3); // 最大整数位数
		percentFormat.setMinimumFractionDigits(0); // 最小小数位数
		percentFormat.setMinimumIntegerDigits(1); // 最小整数位数
		
		// 先检测硬盘，看看是否有升级文件，如果有，本地升级按钮变为可以点击的，在线升级仍然不可点击。
		// 如果没有，则再检测在线升级是否有新的版本
		
		boolean isUpgrading = SettingUtils.readIsUpgrade();
		LogUtil.d("SysUpgradeSettingsViewWrapper", "SettingUtils.readUpgradeFile() = " + isUpgrading);
		
		boolean isNetworkAvailable = SettingUtils.isNetworkAvailable(context);
		LogUtil.d("SysUpgradeSettingsViewWrapper", "isNetworkAvailable = " + isNetworkAvailable);
		
		
		if (isNetworkAvailable) {
			if (!isUpgrading) {
				LocalUpgradeTask task = new LocalUpgradeTask();
				task.execute();				
			} else { // downloading
				if (fileTotalSize == 0) {
					txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_percent, ""));					
				} else {					
					LogUtil.d("SysUpgradeSettingsViewWrapper", "downloadSize = " + downloadSize);
					String percentNum = percentFormat.format(downloadSize);
					txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_percent, percentNum));
				}
			}
		} else {
			// 当网络断开的时候，肯定检测不到新的版本，把两个按钮都变灰色
			fileTotalSize = 0;
			SettingUtils.save0ToFile();
			btnOnline.setEnabled(false);
			txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_check_upgrade_failed_byNetwork));										
//			ToastUtils.showToast(context, "请检查网络！");
		}
		
//		if (fileTotalSize == 0) {			
//			if (isNetworkAvailable) {
//				txtPercent.setText("");	
//				if (!isUpgrading) {
//					LocalUpgradeTask task = new LocalUpgradeTask();
//					task.execute();									
//				} 
//			} else {				
//				// 当网络断开的时候，肯定检测不到新的版本，把两个按钮都变灰色
//				fileTotalSize = 0;
//				btnOnline.setEnabled(false);
//				txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_check_upgrade_failed));										
//				ToastUtils.showToast(context, "请检查网络！");
//			}
//		} else {
//			if (isNetworkAvailable) {
//				LogUtil.d("SysUpgradeSettingsViewWrapper", "downloadSize = " + downloadSize);
//				String percentNum = percentFormat.format(downloadSize);
//				txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_percent, percentNum));					
//			} else {
//				fileTotalSize = 0;
//				SettingUtils.save0ToFile();
//				btnOnline.setEnabled(false);
//				txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_failed));										
//				ToastUtils.showToast(context, "请检查网络！");
//			}
//				
//		}
	}

	private void setEventListener() {
		btnLocal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 检测是否有升级包，如果有则将文件显示出来，否则弹出一个提示“未检测到升级包”
				// 打开文件，判断sda1、sdb1、sdb2、sdcard1是否存在，
				// 如果存在就检测看看dbstar-upgrade.zip是否存在
				
				if (localUpgradeFilePath == null || localUpgradeFilePath.equals("")) {
					return;
				}
				
				// 存在升级文件，则开始强制升级
				Intent intent = new Intent();
		        intent.setClassName("com.dbstar", "com.dbstar.app.alert.GDForceUpgradeActivity");
		        intent.putExtra("packge_file", localUpgradeFilePath);
		        context.startActivity(intent);
			}
		});
		
		btnOnline.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				upgrade();
			}

		});
	}
	
	private void upgrade() {
		if (mUpgradeInfo == null) {
			return;
		}
		
		btnOnline.setEnabled(false);
		txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_percent, ""));				
		
		List<Vapks> list = mUpgradeInfo.getVapksList();
		if (list != null && !list.isEmpty()) {
			for (final Vapks vapks : list) {
				final String upgradeUrl = vapks.getProfileUrl();
				ConnectWork<Boolean> work = new ConnectWork<Boolean>(HttpConnect.GET, upgradeUrl, null) {
					
					@Override
					public Boolean processResult(HttpEntity entity) {
						
						if (entity == null) {
							return false;
						}
						
						boolean success = false;
						try {
							InputStream is = entity.getContent();
							fileTotalSize = entity.getContentLength();
							// 将zip文件保存到
							success = SettingUtils.SaveFile(context, is, fileTotalSize, vapks.getVersion(), true);									
						} catch (IOException e) {
							LogUtil.d("SysUpgradeSettingsViewWrapper", " download upgrade file failed!" + e);
							fileTotalSize = 0;
						}
						return success;
					}
					
					@Override
					public void connectComplete(Boolean success) {
						if (success) {
							int mode = vapks.getUpgradeMode();
							// 强制升级
							Intent intent = new Intent();
							if (mode == 1) {
								intent.setClassName("com.dbstar", "com.dbstar.app.alert.GDForceUpgradeActivity");
							} else {
								intent.setClassName("com.dbstar", "com.dbstar.app.alert.GDUpgradeActivity");
							}
							intent.putExtra("packge_file", "/cache/upgrade.zip");
							context.startActivity(intent);
						} else {
							LogUtil.d("SysUpgradeSettingsViewWrapper", " save file failed!");
							if (SettingUtils.isNetworkAvailable(context)) {
								btnOnline.setEnabled(true);
								txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_failed));				
							} else {
								btnOnline.setEnabled(false);
								txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_check_upgrade_failed_byNetwork));				
							}
							fileTotalSize = 0;																
						} 
					}
				};
				SimpleWorkPoolInstance.instance().execute(work);
			}
		}
	}
	
	private class LocalUpgradeTask extends AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			return CheckLocalUpgradeFile();
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> arrayList) {
			super.onPostExecute(arrayList);
			
			if (arrayList != null && arrayList.size() > 0) {
//				LogUtil.d("SysUpgradeSettingsViewWrapper", " accept()----arrayList.size() = " + arrayList.size());																					
				if (!arrayList.contains("m6_cytc_update.zip")) {
					btnLocal.setEnabled(false);
					btnOnline.setEnabled(false);
					checkOnlineUpgradeFile(deviceModel, productSN, softVersion);
					return;
				} else {
					txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_need_upgrade));
					btnLocal.setEnabled(true);
					btnOnline.setEnabled(false);
					btnLocal.requestFocus();
					btnLocal.setNextFocusRightId(R.id.sysUpgrade_settings_btn_local_upgrade);
					btnLocal.setNextFocusLeftId(R.id.settings_sysUpgrade);
				}
			} else {				
				btnLocal.setEnabled(false);
				btnOnline.setEnabled(false);
				checkOnlineUpgradeFile(deviceModel, productSN, softVersion);
			}
		}
	}

	private ArrayList<String> CheckLocalUpgradeFile() {
		final ArrayList<String> arrayList = new ArrayList<String>();
		// 检测是否有升级包，如果有则将文件显示出来，否则弹出一个提示“未检测到升级包”
		// 打开文件，判断sda1、sdb1、sdb2、sdcard1是否存在，
		// 如果存在就检测看看dbstar-upgrade.zip是否存在
		// TODO:文件名是写死的，千万不能写错
		File file = new File("/storage/external_storage/");
		LogUtil.d("SysUpgradeSettingsViewWrapper", " /storage/external_storage/ is exists = " + file.exists());
		
		if (file.exists()) {
			File[] files = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String fileNmae = pathname.getName();
					String filePath = pathname.getPath();
					if (fileNmae.startsWith("sd")) {

						File[] sdFiles = pathname.listFiles(new FileFilter() {

							@Override
							public boolean accept(File pathname) {
								String sdFileName = pathname.getName();
								if (sdFileName.equals("m6_cytc_update.zip")) {
									arrayList.add(sdFileName);
									localUpgradeFilePath = pathname.getPath();
									
									LogUtil.d("SysUpgradeSettingsViewWrapper", "accept()----arrayList contains m6_cytc_update.zip! ");											
									return true;
								}
								return false;
							}
						});
						return true;
					} else {
						return false;
					}
				}
			});
			
		}
		return arrayList;
	}
	
	private void checkOnlineUpgradeFile(String deviceModel,String productSN, final String softVersion) {
		
		String mac = SettingUtils.getLocalMacAddress(true);
		
		String string = "OEM$" + deviceModel + "$" + productSN + "$" + mac;
		
		LogUtil.d("SysUpgradeSettingsViewWrapper", "before encrpt = " + string);
		// md5加密
		String md5String = MD5.getMD5(string);
		
		// 先将参数放入List,再对参数进行URL编码
		List<NameValuePair> paramsList = new LinkedList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("TERMINALUNIQUE", "OEM$" + deviceModel + "$" + productSN));
		paramsList.add(new BasicNameValuePair("TERMINALPROFILE", "1.0.0.0"));
		paramsList.add(new BasicNameValuePair("AUTHENTICATOR", md5String));
		paramsList.add(new BasicNameValuePair("VAPK", "system"));
		paramsList.add(new BasicNameValuePair("CURVERSION", softVersion));
		// 对参数进行编码
		String param = URLEncodedUtils.format(paramsList, "UTF-8");
		String url = Constants.Server_Url_Upgrade + param;
		
		LogUtil.d("SysUpgradeSettingsViewWrapper", "url = " + url);
		
		ConnectWork<UpgradeInfo> work = new ConnectWork<UpgradeInfo>(HttpConnect.POST, url, paramsList) {
			
			@Override
			public UpgradeInfo processResult(HttpEntity entity) {
				return SettingUtils.parseUpgradeEntity(entity);
			}
			
			@Override
			public void connectComplete(final UpgradeInfo upgradeInfo) {
				if (upgradeInfo == null || upgradeInfo.getVapksList() == null || upgradeInfo.getVapksList().size() <= 0) {
					btnOnline.setEnabled(false);
					txtPercent.setText(context.getString(R.string.page_sysUpgrade_check_upgrade_failed));
					return;
				}
				
				boolean isNeedUpgrade = false;
				
				if (upgradeInfo.getRc() == 0) {
					
					mUpgradeInfo = upgradeInfo;
					
					LogUtil.d("UpgradeTask", "成功");
					
					LogUtil.d("SysUpgradeSettingsViewWrapper", "softVersion = " + softVersion);
					
					isNeedUpgrade = SettingUtils.compareSoftVersionAndIsOrNotUpgrade(softVersion, upgradeInfo, isNeedUpgrade);
					
					LogUtil.d("SysUpgradeSettingsViewWrapper", "isNeedUpgrade = " + isNeedUpgrade);											
					if (!isNeedUpgrade) {
						isNeedSysUpgrade = false;
						btnOnline.setEnabled(false);
						txtPercent.setText(context.getString(R.string.page_sysUpgrade_neednot_upgrade));
					} else {
						isNeedSysUpgrade = true;
						txtPercent.setText(context.getString(R.string.page_sysUpgrade_need_upgrade));				
						btnOnline.setEnabled(true);
						btnOnline.requestFocus();
						btnOnline.setNextFocusLeftId(R.id.settings_sysUpgrade);
					}
				} else if (upgradeInfo.getRc() == -9001) {
					btnOnline.setEnabled(false);
					txtPercent.setText(context.getString(R.string.page_sysUpgrade_check_upgrade_failed));
					btnLocal.setEnabled(false);
					btnOnline.setEnabled(false);
					LogUtil.d("UpgradeTask", "无升级包");
				} else if (upgradeInfo.getRc() == -2101) {
					txtPercent.setText(context.getString(R.string.page_sysUpgrade_check_upgrade_failed));
					btnLocal.setEnabled(false);
					btnOnline.setEnabled(false);
					ToastUtils.showToast(context, "终端未登记");
					LogUtil.d("UpgradeTask", "终端未登记");
				} else if (upgradeInfo.getRc() == -2113) {
					txtPercent.setText(context.getString(R.string.page_sysUpgrade_check_upgrade_failed));
					btnLocal.setEnabled(false);
					btnOnline.setEnabled(false);
					ToastUtils.showToast(context, "MAC地址不匹配");
					LogUtil.d("UpgradeTask", "MAC地址不匹配");				
				}
			}
		};
		
		SimpleWorkPoolInstance.instance().execute(work);
	}

}
