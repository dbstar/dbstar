package com.dbstar.openuser.activity;

import java.util.Random;

import android.content.Context;
import android.content.Intent;

import com.dbstar.util.DbstarUtil;
import com.dbstar.util.LogUtil;
import com.dbstar.util.ToastUtils;

public class CheckNetworkThread extends Thread {

	private volatile boolean isRun = true;
	private Context context;
	
	private final String Check_NetWork = "checkNetwork";

	public CheckNetworkThread(Context context) {
		this.context = context;
	}

	@Override
	public void run() {

		while (isRun) {
			boolean isNetworkAvailable = DbstarUtil.isNetworkAvailable(context);

			if (!isNetworkAvailable) {
				ToastUtils.showToast(context, "没有网络，请检查网络连接！");
			}

			try {
				// 每隔15-30分钟就发一次广播
				Random random = new Random();
				int time = random.nextInt(15) + 15; 
				Thread.sleep(time * 60 * 1000);
				
				Intent intent = new Intent();
				intent.setAction(Check_NetWork);
				context.sendBroadcast(intent);
				
				LogUtil.i("CheckNetworkThread", "定时检查网络连接");
			} catch (InterruptedException e) {
				e.printStackTrace();
				LogUtil.i("CheckNetworkThread", "定时检查网络连接的线程被打断！" + e);
			}
		}

	}

	/**
	 * 启动线程
	 */
	public void startThred() {
		super.start();
	}

	/**
	 * 结束线程
	 */
	public void shutdown() {
		isRun = false;
		try {
			this.interrupt();
		} catch (Exception e) {
			LogUtil.i("CheckNetworkThread", "结束线程出错：：shutdown()");
		}
	}

}
