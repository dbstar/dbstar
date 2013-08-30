package com.fb.FileBrower;

import android.os.storage.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.PowerManager;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.storage.StorageVolume;
import android.content.BroadcastReceiver;

import com.fb.FileBrower.FileBrowerDatabase.FileMarkCursor;
import com.fb.FileBrower.FileBrowerDatabase.ThumbnailCursor;
import com.fb.FileBrower.FileOp.FileOpReturn;
import com.fb.FileBrower.FileOp.FileOpTodo;

import android.bluetooth.BluetoothAdapter;
import java.lang.System;

public class FileBrower extends Activity {
	public static final String TAG = "FileBrower";
	
	private List<Map<String, Object>> mList;
	private boolean mListLoaded = false;
	private static final int LOAD_DIALOG_ID = 4;
	private ProgressDialog load_dialog;
	private boolean mLoadCancel = false;
	
	private PowerManager.WakeLock mWakeLock;
	private static final String ROOT_PATH = "/storage";
	private static final String SHEILD_EXT_STOR = "/storage/sdcard0/external_storage";
	private static final String NAND_PATH = "/storage/sdcard0";
	private static final String SD_PATH = "/storage/external_storage/sdcard1";
	private static final String USB_PATH ="/storage/external_storage";
	private static final String SATA_PATH ="/storage/external_storage/sata";
	
	public static String cur_path = ROOT_PATH;
	private static final int SORT_DIALOG_ID = 0;
	private static final int EDIT_DIALOG_ID = 1;
	private static final int CLICK_DIALOG_ID = 2;
	private static final int HELP_DIALOG_ID = 3;
	private static String exit_path = ROOT_PATH;
	private AlertDialog sort_dialog;	
	private AlertDialog edit_dialog;
	private AlertDialog click_dialog;
	private AlertDialog help_dialog;
	private ListView sort_lv;
	private ListView edit_lv;
	private ListView click_lv;
	private ListView help_lv;
	private boolean local_mode;
	public static FileBrowerDatabase db;
	public static FileMarkCursor myCursor;
	public static ThumbnailCursor myThumbCursor;
	public static  Handler mProgressHandler;
	private ListView lv;
	private TextView tv;
	private ToggleButton btn_mode;
	private List<String> devList = new ArrayList<String>();
	private int request_code = 1550;
	private String lv_sort_flag = "by_name"; 
	
	private int item_position_selected, item_position_first, item_position_last;
	private int fromtop_piexl;
	private boolean isInFileBrowserView=false;
	
	String open_mode[] = {"movie","music","photo","packageInstall"};

    private BroadcastReceiver mMountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Uri uri = intent.getData();
            String path = uri.getPath();                        
            
            if (action == null || path == null)
            	return;
          
            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
        		if (cur_path.startsWith(path)) {
        			cur_path = ROOT_PATH;
        			DeviceScan();
        		}
        		if (cur_path.equals(ROOT_PATH)) {
        			DeviceScan();
        		} 	
        		if (FileOp.IsBusy) {
                    if (isOperateInDirectory(path, FileOp.source_path) ||
                        isOperateInDirectory(path, FileOp.target_path)) {
                        FileOp.copy_cancel = true;
                    }
        		}			
            } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {          	
        		if (cur_path.equals(ROOT_PATH)) {
        			DeviceScan();
        		}
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
        		if (cur_path.startsWith(path)) {
        			cur_path = ROOT_PATH;
        			DeviceScan();
        		}
        		if (cur_path.equals(ROOT_PATH)) {
        			DeviceScan();
        		}
            } 
			else if(action.equals(Intent.ACTION_SCREEN_OFF))
			{
				if(sort_dialog != null)
					sort_dialog.dismiss();
				if(click_dialog != null)
					click_dialog.dismiss();
				if(help_dialog != null)
					help_dialog.dismiss();
			}
        }
    };

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
		//ignore orientation change
    }

    /** Called when the activity is first created or resumed. */
    @Override
    public void onResume() {
        super.onResume();
        //StorageManager m_storagemgr = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
		//m_storagemgr.registerListener(mListener);
		
        // install an intent filter to receive SD card related events.
        IntentFilter intentFilter =
                new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addDataScheme("file");
        registerReceiver(mMountReceiver, intentFilter);
        		
        if(cur_path.equals(ROOT_PATH)){       	
        	 DeviceScan();
        }
        else{
        	lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
        }		
        lv.setSelectionFromTop(item_position_selected, fromtop_piexl);

		isInFileBrowserView=true;
    }
    
    @Override
    public void onPause() {
        super.onPause();

        //StorageManager m_storagemgr = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        //m_storagemgr.unregisterListener(mListener);
        
        unregisterReceiver(mMountReceiver);
        
        mLoadCancel = true;
        
        //update sharedPref
    	SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE); 
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putString("cur_path", cur_path);
    	editor.putBoolean("isChecked", btn_mode.isChecked());
    	editor.commit();          

		if (load_dialog != null)
			load_dialog.dismiss();

		if(mListLoaded==true)
			mListLoaded = false;

		if(!local_mode){
    		db.deleteAllFileMark();  
    		
    	}  	  	
    	db.close();   
    }
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
       
        //Log.i(TAG, "category =" + getIntent().getCategories());
        try{
	        Bundle bundle = this.getIntent().getExtras();  
	        if(!bundle.getString("sort_flag").equals("")){
	        	lv_sort_flag=bundle.getString("sort_flag");
	        }
        }
        catch(Exception e){
     	   Log.e(TAG, "Do not set sort flag");
        }
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        //mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
                
        /* setup database */
        db = new FileBrowerDatabase(this); 
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);  

        /* btn_mode default checked */
        btn_mode = (ToggleButton) findViewById(R.id.btn_mode); 
        btn_mode.setChecked(settings.getBoolean("isChecked", false));
        
        /* setup file list */
        lv = (ListView) findViewById(R.id.listview);  
        local_mode = false;
        
    	cur_path = settings.getString("cur_path", ROOT_PATH);        	
    	if (cur_path != null) {
    		File file = new File(cur_path);
    		if (!file.exists())
    			cur_path = ROOT_PATH;
    	} else
    		cur_path = ROOT_PATH; 
        
        mList = new ArrayList<Map<String, Object>>();
        
        /** edit process bar handler
         *  mProgressHandler.sendMessage(Message.obtain(mProgressHandler, msg.what, msg.arg1, msg.arg2));            
         */
        mProgressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
				if(false==isInFileBrowserView)
					return;
                
                ProgressBar pb = null;
				TextView tvForPaste=null;
                if (edit_dialog != null)
                	{
                		pb = (ProgressBar) edit_dialog.findViewById(R.id.edit_progress_bar);
						tvForPaste=(TextView)edit_dialog.findViewById(R.id.text_view_paste);
                	}

				//Log.i(TAG,"msg.what:"+msg.what);
              	
                switch(msg.what) {
                case 0: 	//set invisible
                    if ((edit_dialog != null) && (pb != null)&&(tvForPaste!=null)) { 
                	pb.setVisibility(View.INVISIBLE);
					tvForPaste.setVisibility(View.GONE);
                    }
                	break;                
                case 1:		//set progress_bar1 
                	if ((edit_dialog != null) && (pb != null)) {  
                		pb.setProgress(msg.arg1);
                 	}
                	break;
                case 2:		//set progress_bar2
                	if ((edit_dialog != null) && (pb != null)) {  
                		pb.setSecondaryProgress(msg.arg1);  
                	}
                	break;
                case 3:		//set visible
                	if ((edit_dialog != null) && (pb != null)&&(tvForPaste!=null)) {  
	                	pb.setProgress(0);
	                	pb.setSecondaryProgress(0);    
	                	pb.setVisibility(View.VISIBLE);
						
						tvForPaste.setVisibility(View.VISIBLE);
						tvForPaste.setText(getText(R.string.edit_dialog_paste_file)+"\n"+FileOp.getMarkFileName("list"));
                	}
                	break;
                case 4:		//file paste ok
					sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" 
	    				+ ROOT_PATH)));                       
        			db.deleteAllFileMark();
        			lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
        			ThumbnailOpUtils.updateThumbnailsForDir(getBaseContext(), cur_path);
        			Toast.makeText(FileBrower.this,
        					getText(R.string.Toast_msg_paste_ok),
        					Toast.LENGTH_SHORT).show();       
        			FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                    if (edit_dialog != null)
                    	edit_dialog.dismiss();  
    				if (mWakeLock.isHeld())
    					mWakeLock.release(); 

					if(tvForPaste!=null)
					{
						tvForPaste.setText("");
						tvForPaste.setVisibility(View.GONE);
					}
                	
                	break;
                case 5:		//file paste err
        			Toast.makeText(FileBrower.this,
        					getText(R.string.Toast_msg_paste_nofile),
        					Toast.LENGTH_SHORT).show();   
        			FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                    if (edit_dialog != null)
                    	edit_dialog.dismiss();   
    				if (mWakeLock.isHeld())
    					mWakeLock.release(); 

					if(tvForPaste!=null)
					{
						tvForPaste.setText("");
						tvForPaste.setVisibility(View.GONE);
					}
                	break;
                case 7:		//dir cannot write
        			Toast.makeText(FileBrower.this,
        					getText(R.string.Toast_msg_paste_writeable),
        					Toast.LENGTH_SHORT).show();  
        			//FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                    if (edit_dialog != null)
                    	edit_dialog.dismiss();  
    				if (mWakeLock.isHeld())
    					mWakeLock.release();   

					if(tvForPaste!=null)
					{
						tvForPaste.setText("");
						tvForPaste.setVisibility(View.GONE);
					}
                	break;
                case 8:		//no free space
                	db.deleteAllFileMark();
        			lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
        			ThumbnailOpUtils.updateThumbnailsForDir(getBaseContext(), cur_path);
        			Toast.makeText(FileBrower.this,
        					getText(R.string.Toast_msg_paste_nospace),
        					Toast.LENGTH_SHORT).show();   
        			FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                    if (edit_dialog != null)
                    	edit_dialog.dismiss(); 
    				if (mWakeLock.isHeld())
    					mWakeLock.release();  

					if(tvForPaste!=null)
					{
						tvForPaste.setText("");
						tvForPaste.setVisibility(View.GONE);
					}
                	break;
                case 9:		//file copy cancel                	
                	if((FileOp.copying_file!=null)&&(FileOp.copying_file.exists()))
            		{
            			try
        				{
							if(FileOp.copying_file.isDirectory())
						        FileUtils.deleteDirectory(FileOp.copying_file);
							else
				        		FileOp.copying_file.delete();
        				}
						catch (Exception e) {
	        				Log.e("Exception when delete",e.toString());
	        			}
            		}
					
    				Toast.makeText(FileBrower.this,
							getText(R.string.Toast_copy_fail),
							Toast.LENGTH_SHORT).show();
    				FileOp.copy_cancel = false;
    				FileOp.copying_file = null;
    				db.deleteAllFileMark();
    				lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
    				FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                    if (edit_dialog != null)
                    	edit_dialog.dismiss();   
    				if (mWakeLock.isHeld())
    					mWakeLock.release(); 

					sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" 
	    				+ ROOT_PATH))); 

					if(tvForPaste!=null)
					{
						tvForPaste.setText("");
						tvForPaste.setVisibility(View.GONE);
					}
                	break;
                case 10:    //update list                                       
                    //((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                    lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));                    
                    mListLoaded = false;
                    if (load_dialog != null)
                        load_dialog.dismiss();
                    break; 
				case 11:	//destination dir is sub folder of src dir
					Toast.makeText(FileBrower.this,
        					getText(R.string.Toast_msg_paste_sub_folder),
        					Toast.LENGTH_SHORT).show();  
        			//FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                    if (edit_dialog != null)
                    	edit_dialog.dismiss();  
    				if (mWakeLock.isHeld())
    					mWakeLock.release();   

					if(tvForPaste!=null)
					{
						tvForPaste.setText("");
						tvForPaste.setVisibility(View.GONE);
					}
					break;
                }
                
            }
        };        
        
        if(cur_path.equals(ROOT_PATH)){       	
        	 DeviceScan();
        }
        else{
        	lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
        }
        
        /* lv OnItemClickListener */
        lv.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);
			
				String file_path = (String) item.get("file_path");
				File file = new File(file_path);
				if(!file.exists()){
					//finish();
					return;
				}

				ToggleButton btn_mode = (ToggleButton) findViewById(R.id.btn_mode); 
				if (!btn_mode.isChecked()){
					if (file.isDirectory()) {	
						cur_path = file_path;
						lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
					}
					else
					{
						openFile(file);
						//showDialog(CLICK_DIALOG_ID);
					}
					
				}
				else {
					if (!cur_path.equals(ROOT_PATH))
					{
						if (item.get("item_sel").equals(R.drawable.item_img_unsel)) {
						FileOp.updateFileStatus(file_path, 1,"list");
						item.put("item_sel", R.drawable.item_img_sel);
						}
						else if (item.get("item_sel").equals(R.drawable.item_img_sel)) {
							FileOp.updateFileStatus(file_path, 0,"list");
							item.put("item_sel", R.drawable.item_img_unsel);
						}
					
						((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();	
					}
					else 
					{
						cur_path = file_path;
						lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));	
	        		}
				}
				
				item_position_selected = lv.getSelectedItemPosition();
				item_position_first = lv.getFirstVisiblePosition();
				item_position_last = lv.getLastVisiblePosition();
				View cv = lv.getChildAt(item_position_selected - item_position_first);
			    if (cv != null) {
			        fromtop_piexl = cv.getTop();
			    }
			}        	
        });      
        
        /* btn_parent listener */
        Button btn_parent = (Button) findViewById(R.id.btn_parent);  
        btn_parent.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!cur_path.equals(ROOT_PATH)) {
					File file = new File(cur_path);
					String parent_path = file.getParent();
					if(cur_path.equals(NAND_PATH)||cur_path.equals(SD_PATH)||parent_path.equals(USB_PATH)) {
						cur_path = ROOT_PATH;
						DeviceScan();
					}
					else {
						cur_path = parent_path;
						lv.setAdapter(getFileListAdapterSorted(parent_path, lv_sort_flag));
					}
				}
			}        	
        });
        
        /* btn_home listener */
        Button btn_home = (Button) findViewById(R.id.btn_home);  
        btn_home.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
				cur_path = ROOT_PATH;
				DeviceScan();
			}        	
        });       
        
  
        
        /* btn_edit_listener */
        Button btn_edit = (Button) findViewById(R.id.btn_edit);  
        btn_edit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!cur_path.equals(ROOT_PATH))
					showDialog(EDIT_DIALOG_ID);
				else {
        			Toast.makeText(FileBrower.this,
        					getText(R.string.Toast_msg_edit_noopen),
        					Toast.LENGTH_SHORT).show();  	
        		}	
			}        	
        });         
        
        /* btn_sort_listener */
        Button btn_sort = (Button) findViewById(R.id.btn_sort);  
        btn_sort.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!cur_path.equals(ROOT_PATH))
					showDialog(SORT_DIALOG_ID);
				else {
        			Toast.makeText(FileBrower.this,
        					getText(R.string.Toast_msg_sort_noopen),
        					Toast.LENGTH_SHORT).show();  					
				}
			}        	
        });   
        
        /* btn_help_listener */
        Button btn_help = (Button) findViewById(R.id.btn_help);  
        btn_help.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(HELP_DIALOG_ID);
			}
        });  
		
        /* btn_istswitch_listener */
        Button btn_listswitch = (Button) findViewById(R.id.btn_listswitch);  
        btn_listswitch.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			FileOp.SetMode(true);
    			Intent intent = new Intent();
    			intent.setClass(FileBrower.this, ThumbnailView1.class);	
    			Bundle bundle = new Bundle();  
                bundle.putString("sort_flag", lv_sort_flag);  
                intent.putExtras(bundle);
    			local_mode = true;
    			FileBrower.this.finish();   
    			startActivity(intent);
    		}
    		   			       		
        }); 
        


    }
    /** onDestory() */
    @Override
    public void onDestroy() {
    	super.onDestroy();
		isInFileBrowserView=false;
    	if(!local_mode){
    		db.deleteAllFileMark();  
    		
    	}  	  	
    	db.close();    	    	   	
    }
	
protected void openFile(File f) {
		// TODO Auto-generated method stub	
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = "*/*";        
        type = FileOp.CheckMediaType(f);
        intent.setDataAndType(Uri.fromFile(f),type);
        startActivity(intent);      		
	}
protected void onActivityResult(int requestCode, int resultCode,Intent data) {
// TODO Auto-generated method stub
	 super.onActivityResult(requestCode, resultCode, data);
	 switch (resultCode) {
	 case RESULT_OK:
		 /* */
		 //Intent intent = this.getIntent();
		 Bundle bundle = data.getExtras();
		 cur_path = bundle.getString("cur_path");		
		 break;
	 default:
		 break;
	 }
}   
    
    private void DeviceScan() {
        // TODO Auto-generated method stub
        devList.clear();
        String internal = getString(R.string.memory_device_str);
        String sdcard = getString(R.string.sdcard_device_str);
        String usb = getString(R.string.usb_device_str);
        String cdrom = getString(R.string.cdrom_device_str);
        String sdcardExt = getString(R.string.ext_sdcard_device_str);
        String DeviceArray[]={internal,sdcard,usb,cdrom,sdcardExt};

		int length=0;
		length=DeviceArray.length;
		
    	for(int i=0;i<length;i++){
    		if(FileOp.deviceExist(DeviceArray[i])){
    			devList.add(DeviceArray[i]);
    		}
    	} 
    	lv.setAdapter(getDeviceListAdapter());
		
	}

	private ListAdapter getDeviceListAdapter() {
		// TODO Auto-generated method stub
		return new SimpleAdapter(FileBrower.this,
        		getDeviceListData(),
        		R.layout.device_item,        		
                new String[]{
        	"item_type",
        	"item_name",        	        	
        	"item_rw",
        	"item_size"
        	},        		
                new int[]{
        	R.id.device_type,
        	R.id.device_name,        	
        	R.id.device_rw,       	
        	R.id.device_size
        	});  		
	}
	private List<Map<String, Object>> getDeviceListData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); 
		Map<String, Object> map;
		//int dev_count=0;
        //File dir = new File(ROOT_PATH);
		/*if (dir.exists() && dir.isDirectory()) {
			if (dir.listFiles() != null) {
				if (dir.listFiles().length > 0) {
					for (File file : dir.listFiles()) {
						if (file.isDirectory()) {
							String path = file.getAbsolutePath();            								
							if (path.equals("/mnt/flash")) {
								map = new HashMap<String, Object>();
								map.put("item_name", getText(R.string.memory_device_str));
								map.put("file_path", "/mnt/flash");
								map.put("item_type", R.drawable.memory_icon);
								map.put("file_date", 0);
								map.put("file_size", 0);	//for sort
					    		map.put("item_size", null);
					    		map.put("item_rw", null);
								list.add(map);								
							} else if (path.equals("/mnt/sata")) {
								map = new HashMap<String, Object>();
								map.put("item_name", getText(R.string.sata_device_str));
								map.put("file_path", "/mnt/sata");
								map.put("item_type", R.drawable.sata_icon);
								map.put("file_date", 0);
								map.put("file_size", 1);	//for sort
					    			map.put("item_size", null);
						    		map.put("item_rw", null);
								list.add(map);								
							} else if (path.equals("/mnt/sdcard")) {
								map = new HashMap<String, Object>();
								
								if(true==isRealSD)
									map.put("item_name", getText(R.string.ext_sdcard_device_str));
								else
									map.put("item_name", getText(R.string.sdcard_device_str));
								
								map.put("file_path", "/mnt/sdcard");
								map.put("item_type", R.drawable.sd_card_icon);
								map.put("file_date", 0);
								map.put("file_size", 1);	//for sort
					    		map.put("item_size", null);
					    		map.put("item_rw", null);
								list.add(map);
							}else if (path.equals("/mnt/usb")) {
								map = new HashMap<String, Object>();
								map.put("item_name", getText(R.string.usb_device_str) + 
										" " + file.getName());
								map.put("file_path", "/mnt/usb");
								map.put("item_type", R.drawable.usb_card_icon);
								map.put("file_date", 0);
								map.put("file_size", 2);	//for sort
					    		map.put("item_size", null);
					    		map.put("item_rw", null);
								list.add(map);									
							} else if (path.startsWith("/mnt/sd")) {
								map = new HashMap<String, Object>();
								if (!path.equals("/mnt/sdcard")){
									dev_count++;
								}
								String devname = null;
								char data = (char) ('A' +dev_count-1);
								devname =  getText(R.string.usb_device_str) +"(" +data + ":)" ;
								map.put("item_name", devname);
								map.put("file_path", path);
								map.put("item_type", R.drawable.usb_card_icon);
								map.put("file_date", 0);
								map.put("file_size", 3);	//for sort
					    		map.put("item_size", null);
					    		map.put("item_rw", null);
								list.add(map);	
							}
						}
					}
				}
			}
		}*/
		File dir = new File(NAND_PATH);
		if (dir.exists() && dir.isDirectory()) {
			map = new HashMap<String, Object>();
			map.put("item_name", getText(R.string.sdcard_device_str));
			map.put("file_path", NAND_PATH);
			map.put("item_type", R.drawable.sd_card_icon);
			map.put("file_date", 0);
			map.put("file_size", 1);	//for sort
			map.put("item_size", null);
			map.put("item_rw", null);
			list.add(map);
		}

		dir = new File(SD_PATH);
		if (dir.exists() && dir.isDirectory()) { 
			map = new HashMap<String, Object>();
			map.put("item_name", getText(R.string.ext_sdcard_device_str));
			map.put("file_path", SD_PATH);
			map.put("item_type", R.drawable.sd_card_icon);
			map.put("file_date", 0);
			map.put("file_size", 1);	//for sort
			map.put("item_size", null);
			map.put("item_rw", null);
			list.add(map);
		}

		dir = new File(USB_PATH);
		if (dir.exists() && dir.isDirectory()) { 
			if (dir.listFiles() != null) {
				int dev_count=0;
				for (File file : dir.listFiles()) {
					if (file.isDirectory()) {
						String devname = null;
						String path = file.getAbsolutePath();
						if (path.startsWith(USB_PATH+"/sd")&&!path.equals(SD_PATH)) {
							map = new HashMap<String, Object>();
							dev_count++;
							char data = (char) ('A' +dev_count-1);
							devname =  getText(R.string.usb_device_str) +"(" +data + ":)" ;
							map.put("item_name", devname);
							map.put("file_path", path);
							map.put("item_type", R.drawable.usb_card_icon);
							map.put("file_date", 0);
							map.put("file_size", 3);	//for sort
				    		map.put("item_size", null);
				    		map.put("item_rw", null);
							list.add(map);	
						}
					}
				}
			}
		}

        dir = new File(USB_PATH);
        if (dir.exists() && dir.isDirectory()) { 
            if (dir.listFiles() != null) {
                int dev_count=0;
                for (File file : dir.listFiles()) {
                    if (file.isDirectory()) {
                        String devname = null;
                        String path = file.getAbsolutePath();
                        if (path.startsWith(USB_PATH+"/sr")&&!path.equals(SD_PATH)) {
                            map = new HashMap<String, Object>();
                            dev_count++;
                            char data = (char) ('A' +dev_count-1);
                            devname =  getText(R.string.cdrom_device_str) +"(" +data + ":)" ;
                            map.put("item_name", devname);
                            map.put("file_path", path);
                            map.put("item_type", R.drawable.cd_rom_icon);
                            map.put("file_date", 0);
                            map.put("file_size", 3);	//for sort
                            map.put("item_size", null);
                            map.put("item_rw", null);
                            list.add(map);	
                        }
                    }
                }
            }
        }

		dir = new File(SATA_PATH);
		if (dir.exists() && dir.isDirectory()) { 
			map = new HashMap<String, Object>();
			map.put("item_name", getText(R.string.sata_device_str));
			map.put("file_path", SATA_PATH);
			map.put("item_type", R.drawable.sata_icon);
			map.put("file_date", 0);
			map.put("file_size", 1);	//for sort
			map.put("item_size", null);
			map.put("item_rw", null);
			list.add(map);	
		}
		
		updatePathShow(ROOT_PATH);
    	if (!list.isEmpty()) { 
    		Collections.sort(list, new Comparator<Map<String, Object>>() {
				
				public int compare(Map<String, Object> object1,
						Map<String, Object> object2) {	
					return ((Integer) object1.get("file_size")).compareTo((Integer) object2.get("file_size"));					
				}    			
    		}); 
    }
		return list;
	}	
    /** Dialog */
    @Override
    protected Dialog onCreateDialog(int id) {
    	LayoutInflater inflater = (LayoutInflater) FileBrower.this
		.getSystemService(LAYOUT_INFLATER_SERVICE);
    	
        switch (id) {
        case SORT_DIALOG_ID:
        	View layout_sort = inflater.inflate(R.layout.sort_dialog_layout,
        		(ViewGroup) findViewById(R.id.layout_root_sort));
        	
            sort_dialog =  new AlertDialog.Builder(FileBrower.this)   
        	.setView(layout_sort)
        	.setTitle(R.string.btn_sort_str)  
            .create(); 
            return sort_dialog;

        case EDIT_DIALOG_ID:
	    	View layout_edit = inflater.inflate(R.layout.edit_dialog_layout,
	    		(ViewGroup) findViewById(R.id.layout_root_edit));
	    	
	    	edit_dialog = new AlertDialog.Builder(FileBrower.this)   
	    	.setView(layout_edit)
	    	.setTitle(R.string.btn_edit_str)  
	        .create();             
	    	return edit_dialog;
        	
        case CLICK_DIALOG_ID:
	    	View layout_click = inflater.inflate(R.layout.click_dialog_layout,
	    		(ViewGroup) findViewById(R.id.layout_root_click));
	    	
	    	click_dialog = new AlertDialog.Builder(FileBrower.this)   
	    	.setView(layout_click)
	        .create();
	    	return click_dialog;	    	
        case HELP_DIALOG_ID:
	    	View layout_help = inflater.inflate(R.layout.help_dialog_layout,
		    		(ViewGroup) findViewById(R.id.layout_root_help));
		    	
	    	help_dialog = new AlertDialog.Builder(FileBrower.this)   
	    	.setView(layout_help)
	    	.setTitle(R.string.btn_help_str)  
	        .create();
		    return help_dialog;	
		case LOAD_DIALOG_ID:
			if(load_dialog==null)
			{
			    load_dialog = new ProgressDialog(this);
			    load_dialog.setMessage(getText(R.string.load_dialog_msg_str));
			    load_dialog.setIndeterminate(true);
			    load_dialog.setCancelable(true);
	    	}
		    return load_dialog;
        }
        
		return null;    	
    }
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        LayoutParams lp = dialog.getWindow().getAttributes();    	
    	switch (id) {
    	case SORT_DIALOG_ID:
            if (display.getHeight() > display.getWidth()) {            	
            	lp.width = (int) (display.getWidth() * 1.0);       	
        	} else {        		
        		lp.width = (int) (display.getWidth() * 0.5);            	
        	}
            dialog.getWindow().setAttributes(lp);   
            
            sort_lv = (ListView) sort_dialog.getWindow().findViewById(R.id.sort_listview);  
            sort_lv.setAdapter(getDialogListAdapter(SORT_DIALOG_ID));	
            
            sort_lv.setOnItemClickListener(new OnItemClickListener() {
            	public void onItemClick(AdapterView<?> parent, View view, int pos,
    					long id) {    				
    				
            		if (!cur_path.equals(ROOT_PATH)) {
            			if (pos == 0){
            				lv_sort_flag = "by_name";
            				lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
            			}
            			else if (pos == 1){
            				lv_sort_flag = "by_date";
            				lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
            			}
            			else if (pos == 2){
            				lv_sort_flag = "by_size";
            				lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
            			}
            		}
            		sort_dialog.dismiss();
            		
    				/*//todo
    				Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);
					if (item.get("item_sel").equals(R.drawable.dialog_item_img_unsel))
						item.put("item_sel", R.drawable.dialog_item_img_sel);
					else if (item.get("item_sel").equals(R.drawable.dialog_item_img_sel))
						item.put("item_sel", R.drawable.dialog_item_img_unsel);
					
					((BaseAdapter) sort_lv.getAdapter()).notifyDataSetChanged();	  
					*/  				
    			}
            	
            });
		
            break;
    	case EDIT_DIALOG_ID:    		
            if (display.getHeight() > display.getWidth()) {            	
            	lp.width = (int) (display.getWidth() * 1.0);       	
        	} else {        		
        		lp.width = (int) (display.getWidth() * 0.5);            	
        	}
            dialog.getWindow().setAttributes(lp);  

            mProgressHandler.sendMessage(Message.obtain(mProgressHandler, 0));
            edit_lv = (ListView) edit_dialog.getWindow().findViewById(R.id.edit_listview);  
            edit_lv.setAdapter(getDialogListAdapter(EDIT_DIALOG_ID));
			//edit_dialog.setCanceledOnTouchOutside(false);
			edit_dialog.setOnDismissListener(new OnDismissListener(){
				public void onDismiss(DialogInterface dialog) {
					FileOp.copy_cancel = true;
				}
			});
            
            edit_lv.setOnItemClickListener(new OnItemClickListener() {
            	public void onItemClick(AdapterView<?> parent, View view, int pos,
    					long id) {
            		if (!cur_path.equals(ROOT_PATH)) {
            			if(FileOp.IsBusy){
            				return;
            			}
            			if (pos == 0) {
            				//Log.i(TAG, "DO cut...");            				
            		        try {        	
            		        	myCursor = db.getFileMark();   
            			        if (myCursor.getCount() > 0) {
                					Toast.makeText(FileBrower.this,
                							getText(R.string.Toast_msg_cut_todo),
                							Toast.LENGTH_SHORT).show();  
                					FileOp.file_op_todo = FileOpTodo.TODO_CUT;
            			        } else {
                					Toast.makeText(FileBrower.this,
                							getText(R.string.Toast_msg_cut_nofile),
                							Toast.LENGTH_SHORT).show();    
                					FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
            			        }
            		        } finally {        	
            		        	myCursor.close();        	
            		        }  
      
        					edit_dialog.dismiss();
            			}
            			else if (pos == 1) {
            				//Log.i(TAG, "DO copy...");            				
            		        try {        	
            		        	myCursor = db.getFileMark();   
            			        if (myCursor.getCount() > 0) {
                					Toast.makeText(FileBrower.this,
                							getText(R.string.Toast_msg_cpy_todo),
                							Toast.LENGTH_SHORT).show();  
                					FileOp.file_op_todo = FileOpTodo.TODO_CPY;
            			        } else {
                					Toast.makeText(FileBrower.this,
                							getText(R.string.Toast_msg_cpy_nofile),
                							Toast.LENGTH_SHORT).show();     
                					FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
            			        }
            		        } finally {        	
            		        	myCursor.close();        	
            		        }       
        					edit_dialog.dismiss();
            			}
            			else if (pos == 2) {
            				//Log.i(TAG, "DO paste...");     
					    	if (!mWakeLock.isHeld())
						    	mWakeLock.acquire(); 

							if(cur_path.startsWith(SD_PATH))
							{
								if(Environment.getExternalStorage2State().equals(Environment.MEDIA_MOUNTED))
								{
									new Thread () {
			        					public void run () {
			        						try {   
			        							FileOp.pasteSelectedFile("list");
			        						} catch(Exception e) {
			        							Log.e("Exception when paste file", e.toString());
			        						}
			        					}
			        				}.start();
								}
								else
								{
									Toast.makeText(FileBrower.this,
	        							getText(R.string.Toast_no_sdcard),
	        							Toast.LENGTH_SHORT).show();
								}
							}
							else
							{
								new Thread () {
		        					public void run () {
		        						try {   
		        							FileOp.pasteSelectedFile("list");
		        						} catch(Exception e) {
		        							Log.e("Exception when paste file", e.toString());
		        						}
		        					}
		        				}.start();
							}
            			}
            			else if (pos == 3) {
            				FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
            				//Log.i(TAG, "DO delete...");   
            				if (FileOpReturn.SUCCESS == FileOp.deleteSelectedFile("list")) {
            					sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" 
				    				+ ROOT_PATH)));
            					db.deleteAllFileMark(); 
                				lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                				Toast.makeText(FileBrower.this,
                						getText(R.string.Toast_msg_del_ok),
                						Toast.LENGTH_SHORT).show();
            				} else {
            					Toast.makeText(FileBrower.this,
            							getText(R.string.Toast_msg_del_nofile),
            							Toast.LENGTH_SHORT).show();
            				}         				          				
            				edit_dialog.dismiss();
            			}
						else if(pos == 4)
						{
							FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
							//Log.i(TAG, "DO rename...");
							myCursor = db.getFileMark(); 
        			        if (myCursor.getCount() > 0) 
							{
								if(myCursor.getCount() > 1)
								{
									String fullPath=FileOp.getMarkFilePath("list");
		        					Toast.makeText(FileBrower.this,
		        							getText(R.string.Toast_msg_rename_morefile)+"\n"+fullPath,
		        							Toast.LENGTH_LONG).show(); 
								}
								else
								{
									String fullPath=FileOp.getSingleMarkFilePath("list");
									if(null!=fullPath)
									{
										String dirPath=fullPath.substring(0, fullPath.lastIndexOf('/'));
										if(cur_path.equals(dirPath))
										{
											if(true!=fileRename())
											{
												Toast.makeText(FileBrower.this,
				        							getText(R.string.Toast_msg_rename_error),
				        							Toast.LENGTH_SHORT).show();
											}
										}
										else
										{
											Toast.makeText(FileBrower.this,
			        							getText(R.string.Toast_msg_rename_diffpath)+"\n"+dirPath,
			        							Toast.LENGTH_LONG).show();
										}
									}
									else if(true!=fileRename())
									{
										Toast.makeText(FileBrower.this,
		        							getText(R.string.Toast_msg_rename_error),
		        							Toast.LENGTH_SHORT).show();
									}
								}
        			        } 
							else 
							{
            					Toast.makeText(FileBrower.this,
            							getText(R.string.Toast_msg_rename_nofile),
            							Toast.LENGTH_SHORT).show();     
        			        }
							edit_dialog.dismiss();
						}
						else if (pos == 5) {
							FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
							//Log.i(TAG, "DO share...");
							myCursor = db.getFileMark(); 
        			        if (myCursor.getCount() > 0) {
								int ret = shareFile();
								if(ret <= 0) {
									Toast.makeText(FileBrower.this,
	        							getText(R.string.Toast_msg_share_nofile),
	        							Toast.LENGTH_SHORT).show(); 
								}	
							}
							else {
								Toast.makeText(FileBrower.this,
        							getText(R.string.Toast_msg_share_nofile),
        							Toast.LENGTH_SHORT).show(); 
							}
							edit_dialog.dismiss();
						}
            		} else {
    					Toast.makeText(FileBrower.this,
    							getText(R.string.Toast_msg_paste_wrongpath),
    							Toast.LENGTH_SHORT).show();
    					edit_dialog.dismiss();
            		}            		
				
    			}            	
            });            

    		break;
    	case CLICK_DIALOG_ID:
            if (display.getHeight() > display.getWidth()) {            	
            	lp.width = (int) (display.getWidth() * 1.0);       	
        	} else {        		
        		lp.width = (int) (display.getWidth() * 0.5);            	
        	}
            dialog.getWindow().setAttributes(lp);  
 
            click_lv = (ListView) click_dialog.getWindow().findViewById(R.id.click_listview);  
            click_lv.setAdapter(getDialogListAdapter(CLICK_DIALOG_ID));	
            
			
    		break;
    	case HELP_DIALOG_ID:
            if (display.getHeight() > display.getWidth()) {            	
            	lp.width = (int) (display.getWidth() * 1.0);       	
        	} else {        		
        		lp.width = (int) (display.getWidth() * 0.5);            	
        	}
            dialog.getWindow().setAttributes(lp);   
            
            help_lv = (ListView) help_dialog.getWindow().findViewById(R.id.help_listview);  
            help_lv.setAdapter(getDialogListAdapter(HELP_DIALOG_ID));	
            
            help_lv.setOnItemClickListener(new OnItemClickListener() {
            	public void onItemClick(AdapterView<?> parent, View view, int pos,
    					long id) { 
            		help_dialog.dismiss();				
    			}
            	
            });
    		
    		break;
        case LOAD_DIALOG_ID:
            if (display.getHeight() > display.getWidth()) {            	
                lp.width = (int) (display.getWidth() * 1.0);       	
            } else {        		
                lp.width = (int) (display.getWidth() * 0.5);            	
            }
            dialog.getWindow().setAttributes(lp);   
            
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {					
                public void onCancel(DialogInterface dialog) {
                    mLoadCancel = true;
                }
            });

            mLoadCancel = false;
			           
            break;
    	}
    } 

	private Dialog mRenameDialog;
	private String name=null;
	private String path=null;
	private boolean fileRename()
    {
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
    	View v = inflater.inflate(R.layout.file_rename, null); 

		path=FileOp.getSingleMarkFilePath("list");
		if(null!=path)
		{
			int index=-1;
			index=path.lastIndexOf("/");
			if(index>=0)
			{
				name=path.substring(index+1);
				if(null==name)
				{
					Log.e(TAG,"[fileRename] file name null!!");
					return false;
				}
			}
			else
			{
				Log.e(TAG,"[fileRename] index error!!");
				return false;
			}
		}
		else
		{
			Log.e(TAG,"[fileRename] file path null!!");
			return false;
		}
    	
    	final EditText mRenameEdit = (EditText) v.findViewById(R.id.editTextRename); 
    	final File mRenameFile = new File(path);  
    	mRenameEdit.setText(name); 
    	
    	Button buttonOK = (Button) v.findViewById(R.id.buttonOK);  
    	Button buttonCancel = (Button) v.findViewById(R.id.buttonCancel);  
    	
    	buttonOK.setOnClickListener(new OnClickListener()
		{
    		public void onClick(View v) 
    		{
    			if( null != mRenameDialog )
    			{
    				mRenameDialog.dismiss();
    				mRenameDialog = null;
    			}
    			
    			String newFileName = String.valueOf(mRenameEdit.getText());
    			if(!name.equals(newFileName))
    			{
	    			newFileName = path.substring(0, path.lastIndexOf('/') + 1) + newFileName;

	    			if(mRenameFile.renameTo(new File(newFileName)))
	    			{
	    				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" 
				    				+ ROOT_PATH)));
						db.deleteAllFileMark(); 
	    				lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
	    			}
	    			else
	    			{
	    				Toast.makeText(FileBrower.this,
							getText(R.string.Toast_msg_rename_error),
							Toast.LENGTH_SHORT).show();
	    			}
    			}
    		}
		});
    	
    	buttonCancel.setOnClickListener(new OnClickListener()
		{
    		public void onClick(View v) 
    		{
    			if( null != mRenameDialog )
    			{
    				mRenameDialog.dismiss();
    				mRenameDialog = null;
    			}
    		}
		});
    	
    	mRenameDialog = new AlertDialog.Builder(FileBrower.this)
				        .setView(v)
				        .show();
		return true;
    }

	private int shareFile() {
		ArrayList<Uri> uris = new ArrayList<Uri>();
		Intent intent = new Intent();
		String type = "*/*";  

		uris = FileOp.getMarkFilePathUri("list");
		final int size = uris.size();

		BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
		if(ba == null) {
			Toast.makeText(FileBrower.this,
						getText(R.string.Toast_msg_share_nodev),
						Toast.LENGTH_SHORT).show(); 
			return 0xff;
		}

		if(size > 0) {
			if (size > 1) {
                intent.setAction(Intent.ACTION_SEND_MULTIPLE).setType(type);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            } else {
                intent.setAction(Intent.ACTION_SEND).setType(type);
                intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
            }
            intent.setType(type);
			startActivity(intent);   
		} 

		return size;
	}
	
    
    /** getFileListAdapter */
    private SimpleAdapter getFileListAdapter(String path) {
        return new SimpleAdapter(FileBrower.this,
        		getFileListData(path),
        		R.layout.filelist_item,        		
                new String[]{
        	"item_type",
        	"item_name",
        	"item_sel",
        	"item_size",
        	"item_date",
        	"item_rw"},        		
                new int[]{
        	R.id.item_type,
        	R.id.item_name,
        	R.id.item_sel,
        	R.id.item_size,
        	R.id.item_date,
        	R.id.item_rw});  
    }
    
    /** getFileListData */
    private List<Map<String, Object>> getFileListData(String path) {    	
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();   	
    	try {
    		File file_path = new File(path); 
        	if (file_path != null && file_path.exists()) { 
        		if (file_path.listFiles() != null) {
            		if (file_path.listFiles().length > 0) {
            			for (File file : file_path.listFiles()) {    					
            	        	Map<String, Object> map = new HashMap<String, Object>();    
            	        	String file_abs_path = file.getAbsolutePath();
							
							//shield external_sdcard and usbdrive under /storage/sdcard0/
							if ((file_abs_path.equals(SD_PATH)) ||(file_abs_path.equals(USB_PATH)) || (file_abs_path.equals(SHEILD_EXT_STOR)))
								continue;

							map.put("item_name", file.getName()); 
            	        	map.put("file_path", file_abs_path);
            	        	
            	        	if (file.isDirectory()) {
            	        		//map.put("item_sel", R.drawable.item_img_nosel);
            	        		if (FileOp.isFileSelected(file_abs_path,"list"))
            	        			map.put("item_sel", R.drawable.item_img_sel); 
            	        		else
            	        			map.put("item_sel", R.drawable.item_img_unsel); 
								
								map.put("item_sel", R.drawable.item_img_nosel);
            	        		map.put("item_type", R.drawable.item_type_dir);
            	        		
            	        		String rw = "d";
            	        		if (file.canRead()) rw += "r"; else rw += "-";
            	        		if (file.canWrite()) rw += "w"; else rw += "-";  
            	        		map.put("item_rw", rw);       
            	        		
            	        		long file_date = file.lastModified();
            	        		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm")
            	        			.format(new Date(file_date));
            	        		map.put("item_date", date + " | ");
            	        		map.put("file_date", file_date);	//use for sorting
            	        		
            	        		long file_size = file.length();
            	        		map.put("file_size", file_size);	//use for sorting
            	        		map.put("item_size", " | ");            	        		
            	        	} else {
            	        		if (FileOp.isFileSelected(file_abs_path,"list"))
            	        			map.put("item_sel", R.drawable.item_img_sel); 
            	        		else
            	        			map.put("item_sel", R.drawable.item_img_unsel); 
            	        			
            	        		map.put("item_type", FileOp.getFileTypeImg(file.getName()));
            	        		
            	        		String rw = "-";
            	        		if (file.canRead()) rw += "r"; else rw += "-";
            	        		if (file.canWrite()) rw += "w"; else rw += "-";  
            	        		map.put("item_rw", rw);       
            	        		
            	        		long file_date = file.lastModified();
            	        		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm")
            	        			.format(new Date(file_date));
            	        		map.put("item_date", date + " | ");
            	        		map.put("file_date", file_date);	//use for sorting
            	        		
            	        		long file_size = file.length();
            	        		map.put("file_size", file_size);	//use for sorting
            	        		map.put("item_size", FileOp.getFileSizeStr(file_size) + " | ");
            	        		
            	        		
            	        	}
            	        	if(!file.isHidden()){
            	        		list.add(map);
            	        	}
            			}
            		}            		
        		}
        		updatePathShow(path);
        	}
    	} catch (Exception e) {
    		Log.e(TAG, "Exception when getFileListData(): ", e);
    		return list;
		}   
    	
		//Log.i(TAG, "list size = " + list.size());
    	return list;
 	}  
        
    /** updatePathShow */
    private void updatePathShow(String path) {      	
        tv = (TextView) findViewById(R.id.path); 
		if (path.equals(ROOT_PATH))
			tv.setText(getText(R.string.rootDevice));
		else
			tv.setText(path);   	
    }

    /** getFileListAdapterSorted */
    private SimpleAdapter getFileListAdapterSorted(String path, String sort_type) {
    	if(path.equals(ROOT_PATH)) 
    	{
			return new SimpleAdapter(FileBrower.this,
        		getDeviceListData(),
        		R.layout.device_item,        		
                new String[]{
        	"item_type",
        	"item_name",        	        	
        	"item_rw",
        	"item_size"
        	},        		
                new int[]{
        	R.id.device_type,
        	R.id.device_name,        	
        	R.id.device_rw,       	
        	R.id.device_size
        	});  
		}
		else
		{
		    return new SimpleAdapter(FileBrower.this,
		    		getFileListDataSorted(path, sort_type),
		    		R.layout.filelist_item,        		
		            new String[]{
		    	"item_type",
		    	"item_name",
		    	"item_sel",
		    	"item_size",
		    	"item_date",
		    	"item_rw"},        		
		            new int[]{
		    	R.id.item_type,
		    	R.id.item_name,
		    	R.id.item_sel,
		    	R.id.item_size,
		    	R.id.item_date,
		    	R.id.item_rw});
		}
    }
    
    private List<Map<String, Object>> getFileListDataSorted(String path, String sort_type) {
        updatePathShow(path);

        if (!mListLoaded) {
            mListLoaded = true; 
            showDialog(LOAD_DIALOG_ID);
            
            final String ppath = path;
            final String ssort_type = sort_type;
            new Thread("getFileListDataSortedAsync") {
                @Override
                public void run() {                                       
                    mList = getFileListDataSortedAsync(ppath, ssort_type);
                    mProgressHandler.sendMessage(Message.obtain(mProgressHandler, 10));                    
                }
    
            }.start();          
            
            return new ArrayList<Map<String, Object>>();
        } else {
            return mList;
        }             
        
    }    
    
    /** getFileListDataSorted */
    private List<Map<String, Object>> getFileListDataSortedAsync(String path, String sort_type) {    	
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	 	
    	try {
    		File file_path = new File(path); 
        	if (file_path != null && file_path.exists()) { 
        		if (file_path.listFiles() != null) {
            		if (file_path.listFiles().length > 0) {
            			for (File file : file_path.listFiles()) {    					
            	        	if (mLoadCancel)
            	        	    return list;
            			        					
            	        	Map<String, Object> map = new HashMap<String, Object>();   
            	        	String file_abs_path = file.getAbsolutePath();

							//shield external_sdcard and usbdrive under /storage/sdcard0/
							if ((file_abs_path.equals(SD_PATH)) || (file_abs_path.equals(USB_PATH)) || (file_abs_path.equals(SHEILD_EXT_STOR)))
								continue;
							
							map.put("item_name", file.getName()); 
            	        	map.put("file_path", file_abs_path);
            	        	
            	        	if (file.isDirectory()) {
            	        		//map.put("item_sel", R.drawable.item_img_nosel);
            	        		if (FileOp.isFileSelected(file_abs_path,"list"))
            	        			map.put("item_sel", R.drawable.item_img_sel); 
            	        		else
            	        			map.put("item_sel", R.drawable.item_img_unsel); 
            	        		map.put("item_type", R.drawable.item_type_dir);
            	        		
            	        		String rw = "d";
            	        		if (file.canRead()) rw += "r"; else rw += "-";
            	        		if (file.canWrite()) rw += "w"; else rw += "-";  
            	        		map.put("item_rw", rw);       
            	        		
            	        		long file_date = file.lastModified();
            	        		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm")
            	        			.format(new Date(file_date));
            	        		map.put("item_date", " | " + date + " | ");
            	        		map.put("file_date", file_date);	//use for sorting
            	        		
            	        		long file_size = file.length();
            	        		map.put("file_size", file_size);	//use for sorting
            	        		map.put("item_size", "");            	        		
            	        	} else {
            	        		if (FileOp.isFileSelected(file_abs_path,"list"))
            	        			map.put("item_sel", R.drawable.item_img_sel); 
            	        		else
            	        			map.put("item_sel", R.drawable.item_img_unsel); 
            	        		
            	        		map.put("item_type", FileOp.getFileTypeImg(file.getName()));
            	        		
            	        		String rw = "-";
            	        		if (file.canRead()) rw += "r"; else rw += "-";
            	        		if (file.canWrite()) rw += "w"; else rw += "-";  
            	        		map.put("item_rw", rw);       
            	        		
            	        		long file_date = file.lastModified();
            	        		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm")
            	        			.format(new Date(file_date));
            	        		map.put("item_date", " | " + date + " | ");
            	        		map.put("file_date", file_date);	//use for sorting
            	        		
            	        		long file_size = file.length();
            	        		map.put("file_size", file_size);	//use for sorting
            	        		map.put("item_size", FileOp.getFileSizeStr(file_size));
            	        		
            	        		
            	        	}
            	        	if(!file.isHidden()){
            	        		list.add(map);    
            	        	}
            			}
            		}            		
        		}
        		//updatePathShow(path);
        	}
    	} catch (Exception e) {
    		Log.e(TAG, "Exception when getFileListData(): ", e);
    		return list;
		}   
    	
		/* sorting */
    	if (!list.isEmpty()) {    	
        	if (sort_type.equals("by_name")) {
        		Collections.sort(list, new Comparator<Map<String, Object>>() {
    				public int compare(Map<String, Object> object1,
    						Map<String, Object> object2) {	
						File file1 = new File((String) object1.get("file_path"));
						File file2 = new File((String) object2.get("file_path"));
						
						if (file1.isFile() && file2.isFile() || file1.isDirectory() && file2.isDirectory()) {
							return ((String) object1.get("item_name")).toLowerCase()
									.compareTo(((String) object2.get("item_name")).toLowerCase());						
						} else {
							return file1.isFile() ? 1 : -1;
						}				
    				}    			
        		});           		
        		
        	} else if (sort_type.equals("by_date")) {
        		Collections.sort(list, new Comparator<Map<String, Object>>() {
    				
    				public int compare(Map<String, Object> object1,
    						Map<String, Object> object2) {	
    					return ((Long) object1.get("file_date")).compareTo((Long) object2.get("file_date"));					
    				}    			
        		});         		
        	} else if (sort_type.equals("by_size")) {
        		Collections.sort(list, new Comparator<Map<String, Object>>() {
    				
    				public int compare(Map<String, Object> object1,
    						Map<String, Object> object2) {	
    					return ((Long) object1.get("file_size")).compareTo((Long) object2.get("file_size"));					
    				}    			
        		});         		
        	}   		
 		
    	}
    	
    	return list;
 	}  
    
    /** getDialogListAdapter */
    private SimpleAdapter getDialogListAdapter(int id) {
        return new SimpleAdapter(FileBrower.this,
        		getDialogListData(id),
        		R.layout.dialog_item,        		
                new String[]{
        	"item_type",
        	"item_name",
        	"item_sel",
        	},        		
                new int[]{
        	R.id.dialog_item_type,
        	R.id.dialog_item_name,
        	R.id.dialog_item_sel,
        	});  
    }
    /** getFileListData */
    private List<Map<String, Object>> getDialogListData(int id) { 
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();  
    	Map<String, Object> map; 
    	
    	switch (id) {
    	case SORT_DIALOG_ID:  	
    		map = new HashMap<String, Object>();     		
        	map.put("item_type", R.drawable.dialog_item_type_name);  
        	map.put("item_name", getText(R.string.sort_dialog_name_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
        	list.add(map);
        	map = new HashMap<String, Object>();         	
        	map.put("item_type", R.drawable.dialog_item_type_date);  
        	map.put("item_name", getText(R.string.sort_dialog_date_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
        	list.add(map);    	
        	map = new HashMap<String, Object>();         	
        	map.put("item_type", R.drawable.dialog_item_type_size);  
        	map.put("item_name", getText(R.string.sort_dialog_size_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
        	list.add(map);       	
        	break; 
        	
    	case EDIT_DIALOG_ID: 
    		map = new HashMap<String, Object>();    		
        	map.put("item_type", R.drawable.dialog_item_type_cut);  
        	map.put("item_name", getText(R.string.edit_dialog_cut_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);   
        	list.add(map);
    		map = new HashMap<String, Object>();    		
        	map.put("item_type", R.drawable.dialog_item_type_copy);  
        	map.put("item_name", getText(R.string.edit_dialog_copy_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
        	list.add(map);
        	map = new HashMap<String, Object>();         	
        	map.put("item_type", R.drawable.dialog_item_type_paste);  
        	map.put("item_name", getText(R.string.edit_dialog_paste_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
        	list.add(map);    	
        	map = new HashMap<String, Object>();         	
        	map.put("item_type", R.drawable.dialog_item_type_delete);  
        	map.put("item_name", getText(R.string.edit_dialog_delete_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
        	list.add(map);  
			map = new HashMap<String, Object>();         	
        	map.put("item_type", R.drawable.dialog_item_type_rename);  
        	map.put("item_name", getText(R.string.edit_dialog_rename_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
        	list.add(map);  
			map = new HashMap<String, Object>();         	
        	map.put("item_type", R.drawable.dialog_item_type_size);  
        	map.put("item_name", getText(R.string.edit_dialog_share_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);  
        	list.add(map); 
    		break; 
    		
    	case CLICK_DIALOG_ID:
    		for(int i=0;i<open_mode.length;i++){
    			map = new HashMap<String, Object>();  
    			map.put("item_type", R.drawable.dialog_item_img_unsel);        	
    			map.put("item_name", open_mode[i]);       	      	            	        	
    			map.put("item_sel", R.drawable.dialog_item_img_unsel);   
    			list.add(map); 
    		}
    		break;
    	case HELP_DIALOG_ID:
    		map = new HashMap<String, Object>();    		
        	map.put("item_type", R.drawable.dialog_help_item_home);  
        	map.put("item_name", getText(R.string.dialog_help_item_home_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);   
        	list.add(map);    	
    		map = new HashMap<String, Object>();    		
        	map.put("item_type", R.drawable.dialog_help_item_mode);  
        	map.put("item_name", getText(R.string.dialog_help_item_mode_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);   
        	list.add(map);  
    		map = new HashMap<String, Object>();    		
        	map.put("item_type", R.drawable.dialog_help_item_edit);  
        	map.put("item_name", getText(R.string.dialog_help_item_edit_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);   
        	list.add(map);   
    		map = new HashMap<String, Object>();    		
        	map.put("item_type", R.drawable.dialog_help_item_sort);  
        	map.put("item_name", getText(R.string.dialog_help_item_sort_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);   
        	list.add(map);    
    		map = new HashMap<String, Object>();    		
        	map.put("item_type", R.drawable.dialog_help_item_parent);  
        	map.put("item_name", getText(R.string.dialog_help_item_parent_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);   
        	list.add(map);      
    		map = new HashMap<String, Object>();    		
        	map.put("item_type", R.drawable.dialog_help_item_thumb);  
        	map.put("item_name", getText(R.string.dialog_help_item_thumb_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);   
        	list.add(map); 
    		/*map = new HashMap<String, Object>();    		
        	map.put("item_type", R.drawable.dialog_help_item_list);  
        	map.put("item_name", getText(R.string.dialog_help_item_list_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);   
        	list.add(map); */  
        	/*  
    		map = new HashMap<String, Object>();    		
        	map.put("item_type", R.drawable.dialog_help_item_close);  
        	String ver_str = " ";
          	try {
          		ver_str += getPackageManager().getPackageInfo("com.fb.FileBrower", 0).versionName;			
     		} catch (NameNotFoundException e) {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     		}      
        	map.put("item_name", getText(R.string.dialog_help_item_close_str) + ver_str);           	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);   
        	list.add(map);   
        	*/       	
    		break;
    	}
    	return list;
    }    
  
    //option menu    
   // public boolean onCreateOptionsMenu(Menu menu)
    //{
   	// String ver_str = null;
   	// try {
	//		ver_str = getPackageManager().getPackageInfo("com.fb.FileBrower", 0).versionName;			
	//	} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
	//	}
       // menu.add(0, 0, 0, getText(R.string.app_name) + " v" + ver_str);
       // return true;
   // }   

    private boolean isOperateInDirectory(String umount_path, String path) 
    {
        String str = path.substring(0, umount_path.length());
        return str.equals(umount_path);
    }
     
}
