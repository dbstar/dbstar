package com.settings.components;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.settings.model.GDDataModel;
import com.settings.model.GDSystemConfigure;
import com.settings.ottsettings.R;
import com.settings.utils.LogUtil;
import com.settings.utils.SettingUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

public class AboutSettingsViewWrapper {
	private Context context;
	private TextView txtTerminalNum, txtHardwareVersion, txtSoftwareVersion, 
			txtLoaderVersion, txtMACAddress, txtTotalMemory, txtCanUseMemory;

	public AboutSettingsViewWrapper(Context context) {
		this.context = context;
	}

	public void initView(View view) {
		findViews(view);
		
		GDDataModel dataModel = new GDDataModel();
		GDSystemConfigure configure = new GDSystemConfigure();
		dataModel.initialize(configure);
		// 从数据库读取数据
		String hardwareType = dataModel.getHardwareVersion();
		String terminalNum = dataModel.getDeviceSearialNumber();
		String softwareVersion = dataModel.getSoftwareVersion();
		String loaderVersion = dataModel.getLoaderVersion();
		
		txtTerminalNum.setText(terminalNum);
		txtHardwareVersion.setText(hardwareType);
		txtSoftwareVersion.setText(softwareVersion);
		txtLoaderVersion.setText(loaderVersion);
		
		String macAddress = SettingUtils.getLocalMacAddress(true);
		txtMACAddress.setText(macAddress);

		txtCanUseMemory.setText(getCanUseMemory(context));
		txtTotalMemory.setText(getTotalMemory(context));
	}

	/**
	 * 获得可用内存，即当前内存剩余量
	 */
	private String getCanUseMemory(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo info = new MemoryInfo();
		manager.getMemoryInfo(info);

		// info.availMem为当前系统的可用内存
		// 将获取的内存大小格式化并返回
		return Formatter.formatFileSize(context, info.availMem);
	}

	private String getTotalMemory(Context context) {
		// 系统内存信息文件
		String filePath = "/proc/meminfo";
		String memoryStr;
		String[] arrayOfString;
		long memory = 0l;

		try {
			FileReader localFileReader = new FileReader(filePath);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);

			// 读取meminfo第一行，系统总内存大小
			memoryStr = localBufferedReader.readLine();

			arrayOfString = memoryStr.split("\\s+");

			// 获取系统总内存，单位是KB，乘以1024转换为Byte
			if (arrayOfString != null && arrayOfString.length != 0) {			
				for (String num : arrayOfString) {
					LogUtil.d("AboutSettingsViewWrapper::getTotalMemory", "num：：" + num + "\t");
				}
				memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
			}
			localBufferedReader.close();
			localFileReader.close();

		} catch (FileNotFoundException e) {
			LogUtil.d("AboutSettingsViewWrapper::getTotalMemory", "FileNotFoundException：：" + e);
			e.printStackTrace();
		} catch (IOException e) {
			LogUtil.d("AboutSettingsViewWrapper::getTotalMemory", "IOException：：" + e);
			e.printStackTrace();
		}

		// Byte转换为KB或者MB，内存大小规格化
		return Formatter.formatFileSize(context, memory);
	}

	private void findViews(View view) {
		txtTerminalNum = (TextView) view.findViewById(R.id.about_settings_terminal_number);
		txtHardwareVersion = (TextView) view.findViewById(R.id.about_settings_hardware_version);
		txtSoftwareVersion = (TextView) view.findViewById(R.id.about_settings_software_version);
		txtLoaderVersion = (TextView) view.findViewById(R.id.about_settings_loader_version);
		txtMACAddress = (TextView) view.findViewById(R.id.about_settings_mac_address);
		txtCanUseMemory = (TextView) view.findViewById(R.id.about_settings_memory_canuse);
		txtTotalMemory = (TextView) view.findViewById(R.id.about_settings_memory_total);
	}
}
