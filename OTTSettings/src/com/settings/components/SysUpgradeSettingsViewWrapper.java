package com.settings.components;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
	private TextView txtContent;
	
	private String localUpgradeFilePath;

	UpgradeInfo mUpgradeInfo = null;
	
	public SysUpgradeSettingsViewWrapper(Context context) {
		this.context = context;
	}

	public void initView(View view) {
		btnLocal = (Button) view.findViewById(R.id.sysUpgrade_settings_btn_local_upgrade);
		btnOnline = (Button) view.findViewById(R.id.sysUpgrade_settings_btn_online_upgrade);
		txtContent = (TextView) view.findViewById(R.id.sysUpgrade_settings_text);
		
		// 先检测硬盘，看看是否有升级文件，如果有，本地升级按钮变为可以点击的，在线升级仍然不可点击。
		// 如果没有，则再检测在线升级是否有新的版本
		
		btnLocal.setEnabled(false);
		btnOnline.setEnabled(false);
		
		LocalUpgradeTask task = new LocalUpgradeTask();
		task.execute();
		
		btnLocal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 检测是否有升级包，如果有则将文件显示出来，否则弹出一个提示“未检测到升级包”
				// 打开文件，判断sda1、sdb1、sdb2、sdcard1是否存在，
				// 如果存在就检测看看dbstar-upgrade.zip是否存在
				
				if (localUpgradeFilePath == null || localUpgradeFilePath.equals("")) {
					ToastUtils.showToast(context, "未检测到升级包");
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
									long contentLength = entity.getContentLength();
									// 将zip文件保存到
									success = SettingUtils.SaveFile(is, contentLength, vapks.getVersion());									
								} catch (IllegalStateException e) {
									LogUtil.d("SysUpgradeSettingsViewWrapper", "=-=-=-btnOnline=-=-=-=" + e);
								} catch (IOException e) {
									LogUtil.d("SysUpgradeSettingsViewWrapper", "=-=-=-btnOnline=-=-=-=" + e);
								}
								return success;
							}
							
							@Override
							public void connectComplete(Boolean success) {
								if (success) {
									Toast.makeText(context, "正在下载升级文件...", Toast.LENGTH_LONG).show();
									
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
									Toast.makeText(context, "升级文件下载失败！", Toast.LENGTH_LONG).show();											
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
			GDDataModel dataModel = new GDDataModel();
			GDSystemConfigure configure = new GDSystemConfigure();
			dataModel.initialize(configure);
			String productSN = dataModel.getDeviceSearialNumber();
			String  deviceModel = dataModel.getHardwareType();
			String  softVersion = dataModel.getSoftwareVersion();
			String mac = SettingUtils.getLocalMacAddress(true);
			
			String string = "OEM$" + deviceModel + "$" + productSN + "$" + mac;
			
			LogUtil.d("SysUpgradeSettingsViewWrapper", "----string = " + string);
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
			
			if (arrayList != null && arrayList.size() > 0) {
				LogUtil.d("SysUpgradeSettingsViewWrapper", "-----accept()----arrayList.size() = " + arrayList.size());																					
				if (!arrayList.contains("upgrade.zip")) {
//					txtContent.setText(context.getResources().get	String(R.string.page_sysUpgrade_neednot_upgrade));
					ToastUtils.showToast(context, "未检测到升级包");
					btnLocal.setEnabled(false);
					btnOnline.setEnabled(false);
					checkOnlineUpgradeFile(url, paramsList, softVersion);
					return;
				} else {
					txtContent.setText(context.getResources().getString(R.string.page_sysUpgrade_need_upgrade));
					btnLocal.setEnabled(true);
					btnOnline.setEnabled(false);
					btnLocal.requestFocus();
					btnLocal.setNextFocusRightId(R.id.sysUpgrade_settings_btn_local_upgrade);
					btnLocal.setNextFocusLeftId(R.id.settings_sysUpgrade);
				}
			} else {				
				btnLocal.setEnabled(false);
				btnOnline.setEnabled(false);
				checkOnlineUpgradeFile(url,paramsList, softVersion);
//				txtContent.setText(context.getResources().getString(R.string.page_sysUpgrade_neednot_upgrade));
				ToastUtils.showToast(context, "未检测到升级包");
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
		LogUtil.d("SysUpgradeSettingsViewWrapper", "---------file.exists() = " + file.exists());
		
		if (file.exists()) {
			File[] files = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String fileNmae = pathname.getName();
					String filePath = pathname.getPath();
					if (fileNmae.startsWith("sd")) {
						LogUtil.d("SysUpgradeSettingsViewWrapper", "-----accept()----filePath = " + filePath);
						LogUtil.d("SysUpgradeSettingsViewWrapper", "-----accept()----fileNmae = " + fileNmae);

						File[] sdFiles = pathname.listFiles(new FileFilter() {

							@Override
							public boolean accept(File pathname) {
								String sdFileName = pathname.getName();
								LogUtil.d("SysUpgradeSettingsViewWrapper", "-----accept()----sdFileName = " + sdFileName);
								if (sdFileName.equals("m6_cytc_update.zip")) {
									arrayList.add(sdFileName);
									localUpgradeFilePath = pathname.getPath();
									
									LogUtil.d("SysUpgradeSettingsViewWrapper", "-----accept()----arrayList contains m6_cytc_update.zip! ");											
									return true;
								}
								return false;
							}
						});
						LogUtil.d("SysUpgradeSettingsViewWrapper", "-----accept()----sdFiles = " + sdFiles);
						return true;
					} else {
						ToastUtils.showToast(context, "未检测到升级包");
						return false;
					}
				}
			});
			
		}
		return arrayList;
	}
	
	private void checkOnlineUpgradeFile(String url, List<NameValuePair> paramsList, final String softVersion) {
		
		LogUtil.d("SysUpgradeSettingsViewWrapper", "----url = " + url);
		
		ConnectWork<UpgradeInfo> work = new ConnectWork<UpgradeInfo>(HttpConnect.POST, url, paramsList) {
			
			@Override
			public UpgradeInfo processResult(HttpEntity entity) {
				return parseUpgradeEntity(entity);
			}
			
			@Override
			public void connectComplete(final UpgradeInfo upgradeInfo) {
				if (upgradeInfo == null) {
					btnOnline.setEnabled(false);
					return;
				}
				
				boolean isNeedUpgrade = false;
				
				if (upgradeInfo.getRc() == 0) {
					
					mUpgradeInfo = upgradeInfo;
					
					LogUtil.d("UpgradeTask", "成功");
					
					LogUtil.d("SysUpgradeSettingsViewWrapper", "------softVersion = " + softVersion);
					
					// 判断版本号，如果取出的版本号大于本地的，就升级
					if (softVersion == null || softVersion.equals("")) {
						isNeedUpgrade = true;
					} else {
						String[] localSoft = softVersion.split("\\.");
						LogUtil.d("SysUpgradeSettingsViewWrapper", "------localSoft.length = " + localSoft.length);
						List<Vapks> vapksList = upgradeInfo.getVapksList();
						if (vapksList != null && vapksList.size() > 0) {						
							// 只处理一个，这样写只是因为数据结果定义成了多个，其实只有一个升级包
							for (Vapks vapks : vapksList) {
								String newVersion = vapks.getVersion();
								LogUtil.d("SysUpgradeSettingsViewWrapper", "------newVersion = " + newVersion);
								if (newVersion != null && !newVersion.equals("")) {
									String[] newSoft = newVersion.split("\\.");							
									LogUtil.d("SysUpgradeSettingsViewWrapper", "------newSoft.length = " + newSoft.length);
									 Pattern pattern = Pattern.compile("[0-9]*"); 
									for (int i = 0; i < newSoft.length; i++) {
										boolean isNum = pattern.matcher(localSoft[i]).matches();
										if (isNum) {
											LogUtil.d("SysUpgradeSettingsViewWrapper", "------Integer.parseInt(localSoft[i] = " + Integer.parseInt(localSoft[i]));											
											LogUtil.d("SysUpgradeSettingsViewWrapper", "------Integer.parseInt(newSoft[i] = " + Integer.parseInt(newSoft[i]));											
											if (Integer.parseInt(localSoft[i]) < Integer.parseInt(newSoft[i])) {
												// 有两种升级模式：
												isNeedUpgrade = true;
												LogUtil.d("SysUpgradeSettingsViewWrapper", "------need upgrade!");											
												break;
											} 											
										} else {
											isNeedUpgrade = true;
											LogUtil.d("SysUpgradeSettingsViewWrapper", "--version is not a num----need upgrade!");											
											break;
										}
									}
								} else {
									isNeedUpgrade = false;
									LogUtil.d("SysUpgradeSettingsViewWrapper", "------need not upgrade!");											
								}
							}
						}						
					}
					
					LogUtil.d("SysUpgradeSettingsViewWrapper", "------isNeedUpgrade = " + isNeedUpgrade);											
					if (!isNeedUpgrade) {
						btnOnline.setEnabled(false);
						txtContent.setText(context.getString(R.string.page_sysUpgrade_neednot_upgrade));
						ToastUtils.showToast(context, context.getResources().getString(R.string.page_sysUpgrade_neednot_upgrade));
					} else {
						txtContent.setText(context.getString(R.string.page_sysUpgrade_need_upgrade));				
						btnOnline.setEnabled(true);
						btnOnline.requestFocus();
						btnOnline.setNextFocusLeftId(R.id.settings_sysUpgrade);
					}
				} else if (upgradeInfo.getRc() == -9001) {
					btnOnline.setEnabled(false);
					LogUtil.d("UpgradeTask", "无升级包");
				} else if (upgradeInfo.getRc() == -2101) {
					ToastUtils.showToast(context, "终端未登记");
					LogUtil.d("UpgradeTask", "终端未登记");
				} else if (upgradeInfo.getRc() == -2113) {
					ToastUtils.showToast(context, "MAC地址不匹配");
					LogUtil.d("UpgradeTask", "MAC地址不匹配");				
				}
			}
		};
		
		SimpleWorkPoolInstance.instance().execute(work);
	}
}
