package com.fb.FileBrower;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ThumbnailOpUtils {
	
	public static void stopThumbnailSanner(Context context) {
		context.stopService(new Intent(context, ThumbnailScannerService.class));
		//Log.w("stopThumbnailSanner", "..................");
	}
	
	public static void cleanThumbnails(Context context) {
		Bundle args = new Bundle();		
		args.putString("scan_type", "clean");
		context.startService(
    		new Intent(context, ThumbnailScannerService.class).putExtras(args));
		
	}
	
	public static void deleteAllThumbnails(Context context, FileBrowerDatabase db) {
		if (db != null)
			db.deleteAllThumbnail();
	}
	
	public static void updateThumbnailsForAllDev(Context context) {
		Bundle args = new Bundle();		
		args.putString("scan_type", "all");
		context.startService(
    		new Intent(context, ThumbnailScannerService.class).putExtras(args));
	}
	
	public static void updateThumbnailsForDev(Context context, String dev_path) {
		if (dev_path != null) {
			if (!dev_path.equals("/mnt/sdcard") &&
				!dev_path.equals("/mnt/flash") &&
				!dev_path.equals("/mnt/usb") &&
				!dev_path.startsWith("/mnt/sd")) 				
					return;			
			
			Bundle args = new Bundle();
			args.putString("dir_path", dev_path);
			args.putString("scan_type", "dev");
			context.startService(
        		new Intent(context, ThumbnailScannerService.class).putExtras(args));
		}
	}	
	
	public static void updateThumbnailsForDir(Context context, String dir_path) {
		if (dir_path != null) {
			if (!dir_path.startsWith("/storage/external_storage/sdcard") &&
				!dir_path.startsWith("/storage/external_storage/flash") &&
				!dir_path.startsWith("/storage/external_storage/usb") &&
				!dir_path.startsWith("/storage/external_storage/sd")) 				
				return;	
			
			Bundle args = new Bundle();
			args.putString("dir_path", dir_path);
			args.putString("scan_type", "dir");
			context.startService(
        		new Intent(context, ThumbnailScannerService.class).putExtras(args));
			
		}
	}		
}