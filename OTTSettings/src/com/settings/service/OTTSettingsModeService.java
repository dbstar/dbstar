package com.settings.service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;

import com.settings.bean.UpgradeInfo;
import com.settings.bean.Vapks;
import com.settings.http.HttpConnect;
import com.settings.http.SimpleWorkPool.ConnectWork;
import com.settings.http.SimpleWorkPool.SimpleWorkPoolInstance;
import com.settings.utils.Constants;
import com.settings.utils.DataUtils;
import com.settings.utils.DisplaySettings;
import com.settings.utils.LogUtil;
import com.settings.utils.MD5;
import com.settings.utils.SettingUtils;
import com.settings.utils.SettingsCommon;

public class OTTSettingsModeService extends Service{

	private int mCheckCount = 0;
	private String productSN = null, deviceModel = null, softVersion = null;
	protected UpgradeInfo mUpgradeInfo = null;
	public static long fileTotalSize;
	private boolean upgradeIsSuccess = false;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private Handler handler;
	
	@Override
	public void onCreate() {
		super.onCreate();
		String mDefaultFrequency = DataUtils.getPreference(this, "modeFrequecy", "");
		String videoMode = DataUtils.getPreference(this, "modeValue", DisplaySettings.getOutpuMode());
		Log.d("OTTSettingsModeService", "videoMode = " + videoMode + " mDefaultFrequency = " + mDefaultFrequency);
		Intent saveIntent = new Intent(SettingsCommon.ACTION_OUTPUTMODE_CHANGE);
		saveIntent.putExtra(SettingsCommon.OUTPUT_MODE, videoMode);
		sendBroadcast(saveIntent);
		
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
			// TODO: this sn is not standard
			productSN = "0000000000000066";
		}

		if (deviceModel == null || deviceModel.equals("")) {
			deviceModel = "02";
		}

		File file = new File(SettingUtils.IsUpgrading_File);
		if (file.exists()) {
			file.delete();
		}
		
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 4:
					Log.d("OTTSettingsModeService", " ------------in handler----------");
					
					// check /cache/command0, /cache/command1 is exist
					CheckUpgradeFileTask task = new CheckUpgradeFileTask();
					task.execute();
					break;
				default:
					break;
				}
			};
		};
		
		final Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				if (mCheckCount < 2) {
					Log.d("OTTSettingsModeService", " ------------in timerTask and mCheckCount = " + mCheckCount);
					Log.d("OTTSettingsModeService", " ------------in timerTask and upgradeIsSuccess = " + upgradeIsSuccess);
					if (!upgradeIsSuccess) {						
						Message message = new Message();
						message.what = 4;
						handler.sendMessage(message);
						mCheckCount ++;
					}
				} else {
					timer.cancel();
				}
			}

		};
		
		if (timer != null) {
			timer.schedule(timerTask, 60 * 1000);
		}
		
	}
	
	private void checkSysUpgradeFile() {
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
			// TODO: this sn is not standard
			productSN = "0000000000000066";
		}

		if (deviceModel == null || deviceModel.equals("")) {
			deviceModel = "02";
		}

		String mac = SettingUtils.getLocalMacAddress(true);

		String string = "OEM$" + deviceModel + "$" + productSN + "$" + mac;

		LogUtil.d("OTTSettingsModeService", "before encrpt = " + string);
		// md5 calculate
		String md5String = MD5.getMD5(string);

		// put parameters into List, then encode as URL
		List<NameValuePair> paramsList = new LinkedList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("TERMINALUNIQUE", "OEM$" + deviceModel + "$" + productSN));
		paramsList.add(new BasicNameValuePair("TERMINALPROFILE", "1.0.0.0"));
		paramsList.add(new BasicNameValuePair("AUTHENTICATOR", md5String));
		paramsList.add(new BasicNameValuePair("VAPK", "system"));
		paramsList.add(new BasicNameValuePair("CURVERSION", softVersion));
		// encode
		String param = URLEncodedUtils.format(paramsList, "UTF-8");
		String url = Constants.Server_Url_Upgrade + param;

		LogUtil.d("OTTSettingsModeService", "url = " + url);

		ConnectWork<UpgradeInfo> work = new ConnectWork<UpgradeInfo>(HttpConnect.POST, url, paramsList) {

			@Override
			public UpgradeInfo processResult(HttpEntity entity) {
				return SettingUtils.parseUpgradeEntity(entity);
			}

			@Override
			public void connectComplete(final UpgradeInfo upgradeInfo) {
				if (upgradeInfo == null || upgradeInfo.getVapksList() == null || upgradeInfo.getVapksList().size() <= 0) {
					return;
				}

				boolean isNeedUpgrade = false;

				if (upgradeInfo.getRc() == 0) {

					mUpgradeInfo = upgradeInfo;

					LogUtil.d("UpgradeTask", "success");

					LogUtil.d("OTTSettingsModeService", "softVersion = " + softVersion);

					isNeedUpgrade = SettingUtils.compareSoftVersionAndIsOrNotUpgrade(softVersion, upgradeInfo, isNeedUpgrade);

					LogUtil.d("OTTSettingsModeService", "isNeedUpgrade = " + isNeedUpgrade);
					
					if (isNeedUpgrade) {
						upgradeIsSuccess = true;
						upgrade();
					} else // if no need upgrade, write '0' into "/data/dbstar/isupgrade.upgrade"
						SettingUtils.save0ToFile();
					
				} else if (upgradeInfo.getRc() == -9001) {
					LogUtil.d("UpgradeTask", "no upgrade package");
				} else if (upgradeInfo.getRc() == -2101) {
					LogUtil.d("UpgradeTask", "no regist");
				} else if (upgradeInfo.getRc() == -2113) {
					LogUtil.d("UpgradeTask", "MAC is invalid");
				}
			}
		};

		SimpleWorkPoolInstance.instance().execute(work);
	}

	private void upgrade() {
		if (mUpgradeInfo == null) {
			return;
		}

		List<Vapks> list = mUpgradeInfo.getVapksList();
		if (list != null && !list.isEmpty()) {
			for (final Vapks vapks : list) {
				final String upgradeUrl = vapks.getProfileUrl();
				LogUtil.d("OTTSettingsModeService", " upgradeUrl = " + upgradeUrl);
				ConnectWork<Boolean> work = new ConnectWork<Boolean>(HttpConnect.GET, upgradeUrl, null) {

					@Override
					public Boolean processResult(HttpEntity entity) {
						boolean success = false;
						
						if (entity == null)
							return false;
						
						try {
							InputStream is = entity.getContent();
							fileTotalSize = entity.getContentLength();
							// save zip file
							success = SettingUtils.SaveFile(getApplicationContext(), is, fileTotalSize, vapks.getVersion(), false);
						} catch (IOException e) {
							LogUtil.d("OTTSettingsModeService", " download upgrade file failed!" + e);
							SettingUtils.save0ToFile();
							fileTotalSize = 0;
						}
						return success;
					}

					@Override
					public void connectComplete(Boolean success) {
						if (success) {
							int mode = vapks.getUpgradeMode();
							// upgrade forced
							Intent intent = new Intent();
							if (mode == 1) {
								intent.setClassName("com.dbstar", "com.dbstar.app.alert.GDForceUpgradeActivity");
							} else {
								intent.setClassName("com.dbstar", "com.dbstar.app.alert.GDUpgradeActivity");
							}
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packge_file", "/cache/upgrade.zip");
							startActivity(intent);
							upgradeIsSuccess = true;
						} else {
							LogUtil.d("OTTSettingsModeService", " save file failed!");
							fileTotalSize = 0;
							upgradeIsSuccess = false;
							
							Intent intent = new Intent(SettingUtils.Sys_Upgrade_Settings_Upgrade_Failed);
							sendBroadcastAsUser(intent, UserHandle.ALL);
						}
					}
				};
				SimpleWorkPoolInstance.instance().execute(work);
			}
		}
		;
	}
	
	private class CheckUpgradeFileTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean existsCommandFile = CheckUpgradeFile();
			if (existsCommandFile)
				LogUtil.d("OTTSettingsModeService", "-----command file exists! ");
			else {
				LogUtil.d("OTTSettingsModeService", "-----command file is not exists! ");						
			}
				
			return existsCommandFile;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {				
				Intent intent = new Intent();
				intent.putExtra("packge_file", "/cache/upgrade.zip");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClassName("com.dbstar", "com.dbstar.app.alert.GDUpgradeActivity");
				startActivity(intent);
				LogUtil.d("OTTSettingsModeService", "-----to  GDUpgradeActivity!");
			} else {
				boolean isNetworkAvailable = SettingUtils.isNetworkAvailable(getApplicationContext());
				boolean isUpgrading = SettingUtils.readIsUpgrade();
				LogUtil.d("OTTSettingsModeService", "SettingUtils.readUpgradeFile() = " + isUpgrading);
				
				if (isNetworkAvailable) {
					if (!isUpgrading) {
						checkSysUpgradeFile();
						fileTotalSize = 1024000;						
						LogUtil.d("OTTSettingsModeService", "-----isUpgrading = " + isNetworkAvailable + ", fileTotalSize" + fileTotalSize);
						
						Intent intent = new Intent(SettingUtils.Sys_Upgrade_Settings_Upgrade);
						intent.putExtra("isUpgrading", isUpgrading);
						intent.putExtra("fileTotalSize", fileTotalSize);
						sendBroadcastAsUser(intent, UserHandle.ALL);
					}
				} else
					SettingUtils.save0ToFile();
				
			}
		}
	}
	
	private boolean CheckUpgradeFile() {
		// check upgrade package is exist, if not, show a pop message: detect no upgrade package
		// open file, check sda1, sdb1, sdb2, sdcard1 is exist
		// if exist, check dbstar-upgrade.zip is exist
		// TODO: file name is static
		File file = new File("/cache/");
		LogUtil.d("OTTSettingsModeService", "---------file.exists() = " + file.exists());
		
		if (file.exists()) {
			File[] files = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String fileNmae = pathname.getName();
					String filePath = pathname.getPath();
					if (fileNmae.startsWith("command")) {
//						LogUtil.d("OTTSettingsModeService", "-----accept()----filePath = " + filePath);
//						LogUtil.d("OTTSettingsModeService", "-----accept()----fileNmae = " + fileNmae);
//						LogUtil.d("OTTSettingsModeService", "-----command file exists! ");

						// if /cache/command0 or /cache/command1 exist, return true
						File file = new File(filePath);
						if (file.exists()) {
							if (file.length() > 0) {
								LogUtil.d("OTTSettingsModeService", "upgrade command exists!");						
								return true;
							} else {
								LogUtil.d("OTTSettingsModeService", "upgrade command exists but length=0");		
								return false;
							}
						} else
							return false;
					} else {
						LogUtil.d("OTTSettingsModeService", "upgrade command is not exists!");						
						return false;
					}
				}
			});	
			
			if (files != null && files.length > 0)
				return true;				
			else 
				return false;
		} else {			
			LogUtil.d("OTTSettingsModeService", "-----do not exists command file! ");
			return false;
		}
	}
	
}
