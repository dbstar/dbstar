package com.settings.components;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.settings.model.GDDataModel;
import com.settings.model.GDSystemConfigure;
import com.settings.ottsettings.R;
import com.settings.utils.Constants;
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
	
	public static Handler handler;
	public static boolean flag = true;
	public static float downloadSize = 0;
	public static long downloadFileSize = 0;
	public static long fileTotalSize;
	
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
		populateData();
		setEventListener();
	}

	private void populateData() {
		final NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1); // 最大小数位数
		percentFormat.setMaximumIntegerDigits(3); // 最大整数位数
		percentFormat.setMinimumFractionDigits(0); // 最小小数位数
		percentFormat.setMinimumIntegerDigits(1); // 最小整数位数
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					if (fileTotalSize > 0) {
						downloadFileSize = (Integer) msg.obj;
						downloadSize = ((Integer) msg.obj).floatValue() / fileTotalSize;
						String percentNum = percentFormat.format(downloadSize);
						txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_percent, percentNum));				
					} else {
						txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_failed));										
					}
					
					// 当网络断开的时候，下载失败
					if (!SettingUtils.isNetworkAvailable(context)) {
						fileTotalSize = 0;
						btnOnline.setEnabled(true);
						txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_failed));										
						ToastUtils.showToast(context, "请检查网络！");
					}
					break;

				default:
					break;
				}
			}
		};
		
		// 先检测硬盘，看看是否有升级文件，如果有，本地升级按钮变为可以点击的，在线升级仍然不可点击。
		// 如果没有，则再检测在线升级是否有新的版本
		boolean isNetworkAvailable = SettingUtils.isNetworkAvailable(context);
		LogUtil.d("SysUpgradeSettingsViewWrapper", "isNetworkAvailable = " + isNetworkAvailable);
		
		if (fileTotalSize == 0) {			
			if (isNetworkAvailable) {
				txtPercent.setText("");	
				LocalUpgradeTask task = new LocalUpgradeTask();
				task.execute();				
			} else {				
				// 当网络断开的时候，肯定检测不到新的版本，把两个按钮都变灰色
				fileTotalSize = 0;
				btnOnline.setEnabled(false);
				txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_check_upgrade_failed));										
				ToastUtils.showToast(context, "请检查网络！");
			}
		} else {
			LogUtil.d("SysUpgradeSettingsViewWrapper", "downloadSize = " + downloadSize);
			String percentNum = percentFormat.format(downloadSize);
			txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_percent, percentNum));	
		}
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
								boolean success = false;
								try {
									InputStream is = entity.getContent();
									fileTotalSize = entity.getContentLength();
									// 将zip文件保存到
									success = SettingUtils.SaveFile(is, fileTotalSize, vapks.getVersion());									
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
									btnOnline.setEnabled(true);
									txtPercent.setText(context.getResources().getString(R.string.page_sysUpgrade_download_failed));				
									fileTotalSize = 0;
								} 
							}
						};
						SimpleWorkPoolInstance.instance().execute(work);
					}
				}
			}
		});
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
				LogUtil.d("SysUpgradeSettingsViewWrapper", " accept()----arrayList.size() = " + arrayList.size());																					
				if (!arrayList.contains("m6_cytc_update.zip")) {
					btnLocal.setEnabled(false);
					btnOnline.setEnabled(false);
					checkOnlineUpgradeFile();
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
				checkOnlineUpgradeFile();
			}
		}
	}

	private UpgradeInfo parseUpgradeEntity(HttpEntity entity) {
		UpgradeInfo upgradeInfo = new UpgradeInfo();
		
		if (entity == null) {
			return null;
		}
		
		try {
			String entiryString = EntityUtils.toString(entity, "UTF-8");
			JSONObject jsonObject = new JSONObject(entiryString);
			JSONObject json = jsonObject.getJSONObject("Response");
			JSONObject jsonObj = json.getJSONObject("Body");
			JSONArray array = jsonObj.getJSONArray("Vapks");
			JSONObject object = json.getJSONObject("Header");
			
			int rc = object.getInt("RC");
			upgradeInfo.setRc(rc);
			String rm = object.getString("RM");
			upgradeInfo.setRm(rm);
			
			List<Vapks> vapksList = new ArrayList<Vapks>();
			if (rc == 0) {
				for (int i = 0; i < array.length(); i++) {
					Vapks vapks = new Vapks();
					JSONObject obj = array.getJSONObject(i);
					int compressType = obj.getInt("COMPRESSTYPE");
					vapks.setCompressType(compressType);
					String profileUrl = obj.getString("PROFILEURL");
					vapks.setProfileUrl(profileUrl);
					String version = obj.getString("VERSION");
					vapks.setVersion(version);
					String remake = obj.getString("REMARK");
					vapks.setRemake(remake);
					double profileSize = obj.getDouble("PROFILESIZE");
					vapks.setProfileSize(profileSize);
					String vapk = obj.getString("VAPK");
					vapks.setVapk(vapk);
					int upgradeMode = obj.getInt("UPGRADEMODE");
					vapks.setUpgradeMode(upgradeMode);
					String apkProfileName = obj.getString("APKPROFILENAME");
					vapks.setApkProfileName(apkProfileName);
					vapksList.add(vapks);
				}
			}
			upgradeInfo.setVapksList(vapksList);
		} catch (ParseException e) {
			LogUtil.d("parseUpgradeEntity", "升级：：解析异常" + e);
		} catch (IOException e) {
			LogUtil.d("parseUpgradeEntity", "升级：：解析异常" + e);
		} catch (JSONException e) {
			LogUtil.d("parseUpgradeEntity", "升级：：解析异常" + e);
		}
		
		return upgradeInfo;
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
	
	private void checkOnlineUpgradeFile() {
		
		GDDataModel dataModel = new GDDataModel();
		GDSystemConfigure configure = new GDSystemConfigure();
		dataModel.initialize(configure);
		String productSN = dataModel.getDeviceSearialNumber();
		String  deviceModel = dataModel.getHardwareType();
		final String  softVersion = dataModel.getSoftwareVersion();
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
				return parseUpgradeEntity(entity);
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
					
					// 判断版本号，如果取出的版本号大于本地的，就升级
					if (softVersion == null || softVersion.equals("")) {
						isNeedUpgrade = true;
					} else {
						String[] localSoft = softVersion.split("\\.");
						LogUtil.d("SysUpgradeSettingsViewWrapper", "localSoft.length = " + localSoft.length);
						List<Vapks> vapksList = upgradeInfo.getVapksList();
						if (vapksList != null && vapksList.size() > 0) {						
							// 只处理一个，这样写只是因为数据结果定义成了多个，其实只有一个升级包
							for (Vapks vapks : vapksList) {
								String newVersion = vapks.getVersion();
								LogUtil.d("SysUpgradeSettingsViewWrapper", "newVersion = " + newVersion);
								if (newVersion != null && !newVersion.equals("")) {
									String[] newSoft = newVersion.split("\\.");							
									 Pattern pattern = Pattern.compile("[0-9]*"); 
									for (int i = 0; i < newSoft.length; i++) {
										boolean isNum = pattern.matcher(localSoft[i]).matches();
										if (isNum) {
											if (Integer.parseInt(localSoft[i]) < Integer.parseInt(newSoft[i])) {
												isNeedUpgrade = true;
												LogUtil.d("SysUpgradeSettingsViewWrapper", "Integer.parseInt(localSoft[" + i + "]) = " 
														+ Integer.parseInt(localSoft[i]) + " < Integer.parseInt(newSoft[" + i + "]) = " 
														+ Integer.parseInt(newSoft[i]) + " (need upgrade)");											
												break;
											} else if(Integer.parseInt(localSoft[i]) > Integer.parseInt(newSoft[i])) {
												isNeedUpgrade = false;
												LogUtil.d("SysUpgradeSettingsViewWrapper", "Integer.parseInt(localSoft[" + i + "]) = " 
														+ Integer.parseInt(localSoft[i]) + " > Integer.parseInt(newSoft[" + i + "]) = " 
														+ Integer.parseInt(newSoft[i]) + " (no need upgrade)");											
												break;
											}
										} else {
											isNeedUpgrade = true;
											LogUtil.d("SysUpgradeSettingsViewWrapper", "--version is invalid----need upgrade!");											
											break;
										}
									}
								} else {
									isNeedUpgrade = false;
									LogUtil.d("SysUpgradeSettingsViewWrapper", "need not upgrade!");											
								}
							}
						}						
					}
					
					LogUtil.d("SysUpgradeSettingsViewWrapper", "isNeedUpgrade = " + isNeedUpgrade);											
					if (!isNeedUpgrade) {
						btnOnline.setEnabled(false);
						txtPercent.setText(context.getString(R.string.page_sysUpgrade_neednot_upgrade));
					} else {
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
