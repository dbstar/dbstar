package com.settings.components;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.settings.bean.UpgradeInfo;
import com.settings.bean.Vapks;
import com.settings.http.HttpConnect;
import com.settings.http.HttpConnect.HttpConnectInstance;
import com.settings.http.SimpleWorkPool;
import com.settings.http.SimpleWorkPool.ConnectWork;
import com.settings.http.SimpleWorkPool.SimpleWorkPoolInstance;
import com.settings.model.GDDataModel;
import com.settings.model.GDSystemConfigure;
import com.settings.ottsettings.R;
import com.settings.utils.Constants;
import com.settings.utils.LogUtil;
import com.settings.utils.MD5;
import com.settings.utils.SettingUtils;

public class SysUpgradeSettingsViewWrapper {

	private Context context;
	private Button btnLocal;
	private Button btnOnline;
	private TextView txtContent;

	public SysUpgradeSettingsViewWrapper(Context context) {
		this.context = context;
	}

	public void initView(View view) {
		btnLocal = (Button) view.findViewById(R.id.sysUpgrade_settings_btn_local_upgrade);
		btnOnline = (Button) view.findViewById(R.id.sysUpgrade_settings_btn_online_upgrade);
		txtContent = (TextView) view.findViewById(R.id.sysUpgrade_settings_text);
		
//		UpgradeTask task = new UpgradeTask();
//		task.execute();
		
		btnLocal.requestFocus();
		
		btnLocal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
		
		GDDataModel dataModel = new GDDataModel();
		GDSystemConfigure configure = new GDSystemConfigure();
		dataModel.initialize(configure);
		String productSN = dataModel.getDeviceSearialNumber();
		String  deviceModel = dataModel.getHardwareType();
		String mac = SettingUtils.getLocalMacAddress(true);
		// md5加密
		String md5String = MD5.getMD5("OEM$" + deviceModel + "$" + productSN + "$" + mac);
		
		// 先将参数放入List,再对参数进行URL编码
		List<NameValuePair> paramsList = new LinkedList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("TERMINALUNIQUE", "OEM$" + deviceModel + "$" + productSN));
		paramsList.add(new BasicNameValuePair("TERMINALPROFILE", "1.0.0.0"));
		paramsList.add(new BasicNameValuePair("AUTHENTICATOR", md5String));
		paramsList.add(new BasicNameValuePair("VAPK", "system"));
		paramsList.add(new BasicNameValuePair("CURVERSION", "1."));
		// 对参数进行编码
		String param = URLEncodedUtils.format(paramsList, "UTF-8");
		String url = Constants.Server_Url_Upgrade + param;
		
		ConnectWork<UpgradeInfo> work = new ConnectWork<UpgradeInfo>(HttpConnect.POST, url, paramsList) {
			
			@Override
			public UpgradeInfo processResult(HttpEntity entity) {
				UpgradeInfo upgradeInfo = parseUpgradeEntity(entity);
				return upgradeInfo;
			}
			
			@Override
			public void connectComplete(final UpgradeInfo upgradeInfo) {
				if (upgradeInfo == null) {
					btnOnline.setClickable(false);
					btnOnline.setFocusable(false);
					return;
				}
				
				if (upgradeInfo.getRc() == 0) {
					txtContent.setText(context.getString(R.string.page_sysUpgrade_need_upgrade));				
					btnOnline.setFocusable(true);
					btnOnline.setClickable(true);
					LogUtil.d("UpgradeTask", "成功");
				} else if (upgradeInfo.getRc() == -9001) {
					txtContent.setText(context.getString(R.string.page_sysUpgrade_neednot_upgrade));
					btnOnline.setFocusable(false);
					btnOnline.setClickable(false);
					LogUtil.d("UpgradeTask", "无升级包");
				} else if (upgradeInfo.getRc() == -2101) {
					LogUtil.d("UpgradeTask", "终端未登记");
				} else if (upgradeInfo.getRc() == -2113) {
					LogUtil.d("UpgradeTask", "MAC地址不匹配");				
				}
				
				btnOnline.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						List<Vapks> list = upgradeInfo.getVapksList();
						if (list != null && !list.isEmpty()) {
							for (Vapks vapks : list) {
								final String upgradeUrl = vapks.getProfileUrl();
								ConnectWork<Void> work = new ConnectWork<Void>(HttpConnect.GET, upgradeUrl, null) {
									
									@Override
									public Void processResult(HttpEntity entity) {
										try {
											InputStream is = entity.getContent();
											long contentLength = entity.getContentLength();
											// 将zip文件保存到data/dbstar
											SettingUtils.SaveFile(is, contentLength);									
										} catch (IllegalStateException e) {
											LogUtil.d("SysUpgradeSettingsViewWrapper", "=-=-=-btnOnline=-=-=-=" + e);
										} catch (IOException e) {
											LogUtil.d("SysUpgradeSettingsViewWrapper", "=-=-=-btnOnline=-=-=-=" + e);
										}
										return null;
									}
									
									@Override
									public void connectComplete(Void result) {
									}
								};
								SimpleWorkPoolInstance.instance().execute(work);
							}
						}
					}
				});
			}
		};
		
		SimpleWorkPoolInstance.instance().execute(work);
	}

	private class UpgradeTask extends AsyncTask<Void, Void, UpgradeInfo> {

		@Override
		protected UpgradeInfo doInBackground(Void... params) {
			GDDataModel dataModel = new GDDataModel();
			GDSystemConfigure configure = new GDSystemConfigure();
			dataModel.initialize(configure);
			String productSN = dataModel.getDeviceSearialNumber();
			String  deviceModel = dataModel.getHardwareType();
			String mac = SettingUtils.getLocalMacAddress(true);
			// md5加密
			String md5String = MD5.getMD5("OEM$" + deviceModel + "$" + productSN + "$" + mac);
			
			
			// 先将参数放入List,再对参数进行URL编码
			List<NameValuePair> paramsList = new LinkedList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("TERMINALUNIQUE", "OEM$" + deviceModel + "$" + productSN));
			paramsList.add(new BasicNameValuePair("TERMINALPROFILE", "1.0.0.0"));
			paramsList.add(new BasicNameValuePair("AUTHENTICATOR", md5String));
			paramsList.add(new BasicNameValuePair("VAPK", "system"));
			paramsList.add(new BasicNameValuePair("CURVERSION", "1."));
			// 对参数进行编码
			String param = URLEncodedUtils.format(paramsList, "UTF-8");
			String url = Constants.Server_Url_Upgrade + param;
//			String url = "http://www.baidu.com/";
			
			HttpEntity entity = HttpConnectInstance.instance().openConnect(HttpConnect.POST, url, paramsList);
			UpgradeInfo upgradeInfo = parseUpgradeEntity(entity);
			
			return upgradeInfo;
		}


		@Override
		protected void onPostExecute(final UpgradeInfo upgradeInfo) {
			super.onPostExecute(upgradeInfo);
			
			if (upgradeInfo == null) {
				btnOnline.setClickable(false);
				btnOnline.setFocusable(false);
				return;
			}
			
			if (upgradeInfo.getRc() == 0) {
				txtContent.setText(context.getString(R.string.page_sysUpgrade_need_upgrade));				
				btnOnline.setFocusable(true);
				btnOnline.setClickable(true);
				LogUtil.d("UpgradeTask", "成功");
			} else if (upgradeInfo.getRc() == -9001) {
				txtContent.setText(context.getString(R.string.page_sysUpgrade_neednot_upgrade));
				btnOnline.setFocusable(false);
				btnOnline.setClickable(false);
				LogUtil.d("UpgradeTask", "无升级包");
			} else if (upgradeInfo.getRc() == -2101) {
				LogUtil.d("UpgradeTask", "终端未登记");
			} else if (upgradeInfo.getRc() == -2113) {
				LogUtil.d("UpgradeTask", "MAC地址不匹配");				
			}
			
			btnOnline.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					List<Vapks> list = upgradeInfo.getVapksList();
					if (list != null && !list.isEmpty()) {
						for (Vapks vapks : list) {
							final String upgradeUrl = vapks.getProfileUrl();
							// 将zip文件保存到data/dbstar
							// TODO: 下载包的方法应该在一个异步线程里面进行，否则，容易造成主线程阻塞。
//							SettingUtils.downLoadAndSaveFile(upgradeUrl);									
//							new Thread(){
//								public void run() {
//									
//								};
//							}.start();
						}
					}
				}
			});
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
}
