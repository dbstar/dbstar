package com.fb.FileBrower;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fb.FileBrower.FileBrowerDatabase.FileMarkCursor;
import com.fb.FileBrower.FileOp.FileOpReturn;
import com.fb.FileBrower.FileOp.FileOpTodo;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.GridView;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
    /** Called when the activity is first created. */
public class ThumbnailView extends Activity{
	public static final String TAG = "ThumbnailView";
	private static final String ROOT_PATH = "/stroage";
	public static String cur_path = ROOT_PATH;
	protected static final int SORT_DIALOG_ID = 0;
	protected static final int EDIT_DIALOG_ID = 1;
	private static final int HELP_DIALOG_ID = 3;
	protected static  String cur_sort_type = null;
	private AlertDialog sort_dialog;	
	private AlertDialog edit_dialog;
	private AlertDialog help_dialog;
	private ListView sort_lv;
	private ListView edit_lv;
	private ListView help_lv;
	public static  Handler mProgressHandler;
	public static FileBrowerDatabase db;
	public static FileMarkCursor myCursor;
	private static List<String> filelist = new ArrayList<String>();
	private boolean local_mode;
	GridView ThumbnailView;		
	int request_code = 1550;
	DecodePhotosTask decodePhotosTask=null;	
	ThumbnailAdapter myThumbnailAdapter;	
	class  DecodePhotosTask  extends  AsyncTask<Void, Photo, Void>  implements    
	PhotoDecodeListener { 		
	protected  Void doInBackground(Void... params) {   
		
		
		DecodePhoto.getInstance().decodeImage(this,filelist);
		return null;   
		   
	}    
	
	public void PreOnExecute( Context c,List<String> flist){
		Bitmap bm = null;
		String filename = null;
		Photo photo;
		myThumbnailAdapter.clear();
		for(int i=0;i<flist.size();i++){
			File f = new File(flist.get(i));
			filename = f.getName();				
			bm = BitmapFactory.decodeResource(c.getResources(),R.drawable.item_preview_photo);
			photo = new Photo(bm,flist.get(i));	
			myThumbnailAdapter.addPhoto(photo);			
		}
		myThumbnailAdapter.notifyDataSetChanged();
		
	}
	public   void  onPhotoDecodeListener(Photo photo) {   
		if  (!isCancelled()) {             
			publishProgress(photo);   
		}   
	}     
	public   void  onProgressUpdate(Photo...photos) {   
		for(Photo photo : photos) {   
			myThumbnailAdapter.refreshPhoto(photo);  
			//myThumbnailAdapter.addPhoto(photo);  
			
		}      
		myThumbnailAdapter.notifyDataSetChanged();
		
		}
	public void OnCancelled(){
		//Runtime.getRuntime().gc();
		System.gc();
	}
	
	}	
		
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);         
        setContentView(R.layout.thumbnail);       
        ThumbnailView = (GridView)findViewById(R.id.mygridview);   
        /*get cur path form listview*/
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();  
        cur_path = bundle.getString("cur_path");
        /* setup database */
        FileOp.SetMode(false);
        db = new FileBrowerDatabase(this);             
        local_mode = false;
        GetCurrentFilelist(cur_path,cur_sort_type);
        myThumbnailAdapter = new ThumbnailAdapter(this,filelist);
        if(cur_path.equals(ROOT_PATH)){
        		DeviceScan();
        	
        }
        else{
        	   
        		//ThumbnailView.setAdapter(getThumbnailAdapter(cur_path,cur_sort_type));
        		waitMediaScan(cur_sort_type);
        	
        }
         
        
        /* btn_mode default checked */
          
        ToggleButton btn_mode = (ToggleButton) findViewById(R.id.btn_thumbmode); 
        btn_mode.setChecked(true);
        
        ThumbnailView.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {				
				String file_path = (String)filelist.get((int)arg3);
				File file = new File(file_path);
				if(!file.exists()){					
					return;
				}
				
				if (file.isDirectory()) {						
					cur_path = file_path;
					if(decodePhotosTask !=null&&(decodePhotosTask.getStatus() == AsyncTask.Status.RUNNING)){						
						return;
					}
					waitMediaScan(cur_sort_type);
					
					
				}
				else{
					ToggleButton btn_mode = (ToggleButton) findViewById(R.id.btn_thumbmode); 
					if (!btn_mode.isChecked()){
						openFile(cur_path);
						
					}
					else {						
						    if(FileOp.isFileSelected(file_path, "thumbnail")){
						    	
						    	FileOp.updateFileStatus(file_path, 0,"thumbnail");						
						}
						    else{						    							
							FileOp.updateFileStatus(file_path, 1,"thumbnail");							
						}		
						    
						myThumbnailAdapter.notifyDataSetChanged();	
					}
				}
				
			}			
            	
        });
        
        /* lv OnItemLongClickListener */
      /*  ThumbnailView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			public boolean onItemLongClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);
				
				String file_path = (String) item.get("file_path");
				File file = new File(file_path);
				
				if (file.isFile()) {	
					showDialog(EDIT_DIALOG_ID);					
				}
				return false;
			}
		});*/
        
        //button click listener
        /*home button*/
        Button btn_thumbhome = (Button) findViewById(R.id.btn_thumbhome); 
        btn_thumbhome.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {  
    			if(decodePhotosTask !=null&&(decodePhotosTask.getStatus() == AsyncTask.Status.RUNNING)){
					decodePhotosTask.cancel(true);
					//return;
				}
    			DeviceScan();
    		}
    		   			       		
        });  
        
        
        /*updir button*/
        Button btn_thumbparent = (Button) findViewById(R.id.btn_thumbparent); 
        btn_thumbparent.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {  
    			if(decodePhotosTask !=null&&(decodePhotosTask.getStatus() == AsyncTask.Status.RUNNING)){
					decodePhotosTask.cancel(true);
					//return;
				}
    			if (!cur_path.equals(ROOT_PATH)) {
					File file = new File(cur_path);
					String parent_path = file.getParent();
					
					cur_path = parent_path;
					if(parent_path.equals(ROOT_PATH)){
						cur_path = parent_path;
						DeviceScan();
					}
					else{
						 cur_path = parent_path;
						 waitMediaScan(cur_sort_type);
					
					}
				}
    		}
    		   			       		
        });         
        /*edit button*/
        Button btn_thumbsort = (Button) findViewById(R.id.btn_thumbsort); 
        btn_thumbsort.setOnClickListener(new OnClickListener() {
   		public void onClick(View v) {   
   			if(decodePhotosTask !=null&&(decodePhotosTask.getStatus() == AsyncTask.Status.RUNNING)){
				
				return;
			}
			if (!cur_path.equals(ROOT_PATH))
				showDialog(SORT_DIALOG_ID);
			else {
    			Toast.makeText(ThumbnailView.this,
    					getText(R.string.Toast_msg_sort_noopen),
    					Toast.LENGTH_SHORT).show();  					
			}
   		}
   		   			       		
       }); 
        /*edit button*/
         Button btn_thumbedit = (Button) findViewById(R.id.btn_thumbedit); 
         btn_thumbedit.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {   
    			if(decodePhotosTask !=null&&(decodePhotosTask.getStatus() == AsyncTask.Status.RUNNING)){
					
					return;
				}
				if (!cur_path.equals(ROOT_PATH))
					showDialog(EDIT_DIALOG_ID);
				else {
        			Toast.makeText(ThumbnailView.this,
        					getText(R.string.Toast_msg_edit_noopen),
        					Toast.LENGTH_SHORT).show();  	
        		}	
    		}
    		   			       		
        }); 
         /* btn_help_listener */
         Button btn_thumbhelp = (Button) findViewById(R.id.btn_thumbhelp);  
         btn_thumbhelp.setOnClickListener(new OnClickListener() {
 			public void onClick(View v) {
 				showDialog(HELP_DIALOG_ID);
 			}
         });          
        /*switch_button*/
        Button btn_thumbswitch = (Button) findViewById(R.id.btn_thumbswitch); 
        btn_thumbswitch.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {  
    			if(decodePhotosTask !=null&&(decodePhotosTask.getStatus() == AsyncTask.Status.RUNNING)){
					decodePhotosTask.cancel(true);
					//return;
				}
    			FileOp.SetMode(true);
    			Intent intent = new Intent();
    			intent.setClass(ThumbnailView.this, FileBrower.class);
    			/* Activity */
    			Bundle mybundle = new Bundle();   			
    			mybundle.putString("cur_path", cur_path);
    			intent.putExtras(mybundle);   	  			
    			startActivityForResult(intent,request_code); 
    			//setResult(RESULT_OK, intent);
    			/* Activity */
    			local_mode = true;
    			ThumbnailView.this.finish();   	
    		}
    		   			       		
        }); 

        
        
        /** edit process bar handler
         *  mProgressHandler.sendMessage(Message.obtain(mProgressHandler, msg.what, msg.arg1, msg.arg2));            
         */
        mProgressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                
                ProgressBar pb = null;
                if (edit_dialog != null)
                	pb = (ProgressBar) edit_dialog.findViewById(R.id.edit_progress_bar);
                switch(msg.what) {
                case 0: 	//set invisible
                    if ((edit_dialog != null) && (pb != null)) {                    	
                	pb.setVisibility(View.INVISIBLE);
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
                	if ((edit_dialog != null) && (pb != null)) {  
	                	pb.setProgress(0);
	                	pb.setSecondaryProgress(0);    
	                	pb.setVisibility(View.VISIBLE);
                	}
                	break;
                case 4:		//file paste ok
        			db.deleteAllFileMark();
        			waitMediaScan(cur_sort_type);
        			Toast.makeText(ThumbnailView.this,
        					getText(R.string.Toast_msg_paste_ok),
        					Toast.LENGTH_SHORT).show();       
        			FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                    if (edit_dialog != null)
                    	edit_dialog.dismiss();                    	
                	
                	break;
                case 5:		//file paste err
        			Toast.makeText(ThumbnailView.this,
        					getText(R.string.Toast_msg_paste_nofile),
        					Toast.LENGTH_SHORT).show();   
        			FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                    if (edit_dialog != null)
                    	edit_dialog.dismiss();   
                	break;
                case 6:
                	//decodePhotosTask = (DecodePhotosTask)new DecodePhotosTask().execute();
                	break;
                }
                
            }
        };
        
    }
    
    
    
	private void waitMediaScan(String sort_type) {
		// TODO Auto-generated method stub
		 GetCurrentFilelist(cur_path,sort_type);
		 
		 myThumbnailAdapter.clear();
		 System.gc();
		 myThumbnailAdapter = new ThumbnailAdapter(getBaseContext(),filelist);
		 ThumbnailView.setAdapter(myThumbnailAdapter);
		 if(decodePhotosTask !=null&&(decodePhotosTask.getStatus() == AsyncTask.Status.RUNNING)){
				decodePhotosTask.cancel(true);
				//return;
			}
		 decodePhotosTask = (DecodePhotosTask)new DecodePhotosTask();
		 decodePhotosTask.PreOnExecute(this, filelist);
		 decodePhotosTask.execute();
		
	}



	private void GetCurrentFilelist(String file_path, String sort_type) {
		// TODO Auto-generated method stub
    	File path = new File(file_path);
		filelist.clear();
		if(!file_path.equals(ROOT_PATH)){
			if(path.listFiles() != null){				
				for(File files: path.listFiles()){					
					String file_abs_path = files.getAbsolutePath();
					filelist.add(file_abs_path);
					if(!filelist.isEmpty()){						
						
						if(sort_type != null){
							if(sort_type.equals("by_name")){							
								Collections.sort(filelist, new Comparator<String>(){
									public int compare(String object1,
											String object2) {	
										return ((String) object1).compareTo((String) object2);													
									}																	
								}); 								
							}
							else if(sort_type.equals("by_date")){							
									Collections.sort(filelist, new Comparator<String>(){
										public int compare(String object1,
												String object2) {	
											File file1 =new File(object1);
											File file2 =new File(object2);
											long file_date1 = file1.lastModified(); 
											long file_date2 = file2.lastModified(); 
											return ((Long) file_date1).compareTo((Long) file_date2);					
										}																	
								}); 
							}
							else if(sort_type.equals("by_size")){							
								Collections.sort(filelist, new Comparator<String>(){
									public int compare(String object1,
											String object2) {
										File file1 =new File(object1);
										File file2 =new File(object2);
										long file_size1 = file1.length(); 
										long file_size2 = file2.length(); 										
										return ((Long) file_size1).compareTo((Long) file_size2);			
									}																	
								}); 
							}
						}
					}
				}
			}
		}		
		updatePathShow(cur_path);
	}


    public void onDestroy() {
    	super.onDestroy(); 
    	if(decodePhotosTask !=null&&(decodePhotosTask.getStatus() == AsyncTask.Status.RUNNING)){
			decodePhotosTask.cancel(true);
    	}
    	if(!local_mode){
    		db.deleteAllFileMark();   		
    	}    	
    	db.close();
    }
    protected void DeviceScan() {
        // TODO Auto-generated method stub
        List<String> dev_list = new ArrayList<String>();
        filelist.clear();
        String internal = getString(R.string.memory_device_str);
        String sdcard = getString(R.string.sdcard_device_str);
        String usb = getString(R.string.usb_device_str);
        String cdrom = getString(R.string.cdrom_device_str);

        String DeviceArray[]={internal,sdcard,usb,cdrom};   	
    	for(int i=0;i<DeviceArray.length;i++){
    		if(FileOp.deviceExist(DeviceArray[i])){
    			dev_list.add(DeviceArray[i]);
    			String dev_path = FileOp.convertDeviceName(this,DeviceArray[i]);
    			filelist.add(dev_path);
    		}
    	} 
    	cur_path = ROOT_PATH;    	
		 myThumbnailAdapter.clear();
		 myThumbnailAdapter = new ThumbnailAdapter(getBaseContext(),filelist);
		 ThumbnailView.setAdapter(myThumbnailAdapter);
		 if(decodePhotosTask !=null&&(decodePhotosTask.getStatus() == AsyncTask.Status.RUNNING)){
				decodePhotosTask.cancel(true);				
			}
		decodePhotosTask = (DecodePhotosTask)new DecodePhotosTask();
		decodePhotosTask.PreOnExecute(this, filelist);
		String device = getString(R.string.rootDevice);
    	updatePathShow(device);
    	
	}	
    
 

	protected void onActivityResult(int requestCode, int resultCode,Intent data) {
    	// TODO Auto-generated method stub
    		 super.onActivityResult(requestCode, resultCode, data);
    		 switch (resultCode) {
    		 case RESULT_OK:
    			 /* */    			
    			 Bundle bundle = data.getExtras();
    			 cur_path = bundle.getString("cur_path");   			 
    			 break;
    		 default:
    			 break;
    		 }
}
   /* private SimpleAdapter getThumbnailAdapter(String path,String sort_type) {
		// TODO Auto-generated method stub
		return new SimpleAdapter(ThumbnailView.this,
        		getThumbData(path,sort_type),
        		R.layout.gridview_item,        		
                new String[]{
        	"item_image",
        	"item_name", 
        	"item_mark"
        	},        		
                new int[]{
        	R.id.itemImage,
        	R.id.itemText, 
        	R.id.itemMark,
		});  		
	}
	private List<? extends Map<String, ?>> getThumbData(String path,String sort_type) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		File  file_path = new File(path);		
		if(path.equals(ROOT_PATH)){
			for(int i=0;i<filelist.size();i++){
				String filename = filelist.get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				cur_path = FileOp.convertDeviceName(filelist.get(i));
				map.put("item_image", FileOp.getThumbImage(filename));
				map.put("item_name", filename.toString());
				map.put("item_mark",R.drawable.item_img_nosel);
				map.put("file_path",cur_path);
				list.add(map); 					
			}			
		}
		else{
			
			if(file_path != null && file_path.exists()){
				if(file_path.listFiles() != null){				
					for(File files: file_path.listFiles()){
						Map<String, Object> map = new HashMap<String, Object>();
						String file_abs_path = files.getAbsolutePath();
						if(files.isDirectory()){
							map.put("item_mark",R.drawable.item_img_nosel);
							long file_date = files.lastModified();       	        		  	        		
        	        		map.put("file_date", file_date);	//use for sorting
        	        		
        	        		long file_size = files.length();
        	        		map.put("file_size", file_size);	//use for sorting      	        		          	        
						}
						else{
							if (FileOp.isFileSelected(file_abs_path,"thumbnail"))
        	        			map.put("item_mark", R.drawable.item_img_sel); 
        	        		else
        	        			map.put("item_mark", R.drawable.item_img_unsel); 
							
							long file_date = files.lastModified();        	        		      	        		
        	        		map.put("file_date", file_date);	//use for sorting      	        		
        	        		long file_size = files.length();
        	        		map.put("file_size", file_size);	//use for sorting
						}						
						
						map.put("item_image", FileOp.getThumbImage(files.getName()));
						map.put("item_name", files.getName());
						map.put("file_path",files.getAbsolutePath());
						list.add(map); 					
					}
				}
			}
		}
		if(!list.isEmpty()){
			if(sort_type != null){
				if(sort_type.equals("by_name")){
					Collections.sort(list, new Comparator<Map<String, Object>>() {
						
						public int compare(Map<String, Object> object1,
								Map<String, Object> object2) {	
							return ((String) object1.get("item_name")).compareTo((String) object2.get("item_name"));					
						}    			
    				});      
					
				}
				else if(sort_type.equals("by_date")){
					Collections.sort(list, new Comparator<Map<String, Object>>() {
						
						public int compare(Map<String, Object> object1,
								Map<String, Object> object2) {	
							return ((Long) object1.get("file_date")).compareTo((Long) object2.get("file_date"));					
						}    			
    				});     
				}
				else if(sort_type.equals("by_size")){
					Collections.sort(list, new Comparator<Map<String, Object>>() {
						
						public int compare(Map<String, Object> object1,
								Map<String, Object> object2) {	
							return ((Long) object1.get("file_size")).compareTo((Long) object2.get("file_size"));					
						}    			
    				}); 
					
				}
			}
		}
		updatePathShow(cur_path);
		return list;
	}
	*/
/*private byte[] Bitmap2Bytes(Bitmap bm){

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

		return baos.toByteArray();

}

	
	public static int BytetoInteger(byte[] bitmap_byte)
	{
		int s = 0;
		for (int i = 0; i < 3; i++)
		{
			if (bitmap_byte[i] > 0)
				s = s + bitmap_byte[i];
			else
				s = s + 256 + bitmap_byte[i];
			s = s * 256;
		}

		if (bitmap_byte[3] > 0)
			s = s + bitmap_byte[3];
		else
			s = s + 256 + bitmap_byte[3];

		return s;
}

*/

	private void updatePathShow(String device) {
		// TODO Auto-generated method stub		 	
		TextView tv = (TextView) findViewById(R.id.thumb_path); 
		tv.setText(device); 		
	}

	private void openFile(String file_path) {
		// TODO Auto-generated method stub
		File file = new File(file_path);
		Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = "*/*";        
        type = FileOp.CheckMediaType(file);
        intent.setDataAndType(Uri.fromFile(file),type);
        startActivity(intent); 
		
	}
	
	protected Dialog onCreateDialog(int id){
		LayoutInflater inflater = (LayoutInflater) ThumbnailView.this
		.getSystemService(LAYOUT_INFLATER_SERVICE);
		
		switch (id) {
        case SORT_DIALOG_ID:
        	View layout_sort = inflater.inflate(R.layout.sort_dialog_layout,
        		(ViewGroup) findViewById(R.id.layout_root_sort));
        	
            sort_dialog =  new AlertDialog.Builder(ThumbnailView.this)   
        	.setView(layout_sort)
            .create(); 
            return sort_dialog;

        case EDIT_DIALOG_ID:
	    	View layout_edit = inflater.inflate(R.layout.edit_dialog_layout,
	    		(ViewGroup) findViewById(R.id.layout_root_edit));
	    	
	    	edit_dialog = new AlertDialog.Builder(ThumbnailView.this)   
	    	.setView(layout_edit)
	        .create();             
	    	return edit_dialog;
        	
        case HELP_DIALOG_ID:
	    	View layout_help = inflater.inflate(R.layout.help_dialog_layout,
		    		(ViewGroup) findViewById(R.id.layout_root_help));
		    	
		    	help_dialog = new AlertDialog.Builder(ThumbnailView.this)   
		    	.setView(layout_help)
		        .create();
		    return help_dialog;	       
        }
        
		return null;    	
    }
	
	
	
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
            				waitMediaScan("by_name");
            				cur_sort_type = "by_name";
            			}
            			else if (pos == 1){
            				waitMediaScan("by_date");
            				cur_sort_type = "by_date";
            				
            			}
            				
            			else if (pos == 2){
            				waitMediaScan("by_size");
            				cur_sort_type = "by_size";
            			}
            		}
            		sort_dialog.dismiss();
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
            
            edit_lv.setOnItemClickListener(new OnItemClickListener() {
            	public void onItemClick(AdapterView<?> parent, View view, int pos,
    					long id) {
            		if (!cur_path.equals(ROOT_PATH)) {
            			if (pos == 0) {
            				//Log.i(TAG, "DO cut...");            				
            		        try {        	
            		        	myCursor = db.getFileMark();   
            			        if (myCursor.getCount() > 0) {
                					Toast.makeText(ThumbnailView.this,
                							getText(R.string.Toast_msg_cut_todo),
                							Toast.LENGTH_SHORT).show();  
                					FileOp.file_op_todo = FileOpTodo.TODO_CUT;
            			        } else {
                					Toast.makeText(ThumbnailView.this,
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
                					Toast.makeText(ThumbnailView.this,
                							getText(R.string.Toast_msg_cpy_todo),
                							Toast.LENGTH_SHORT).show();  
                					FileOp.file_op_todo = FileOpTodo.TODO_CPY;
            			        } else {
                					Toast.makeText(ThumbnailView.this,
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
            				
            				new Thread () {
            					public void run () {
            						try {
            							FileOp.pasteSelectedFile("thumbnail");
            						} catch(Exception e) {
            							Log.e("Exception when paste file", e.toString());
            						}
            					}
            				}.start();
            				            				
            			}
            			else if (pos == 3) {
            				FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
            				//Log.i(TAG, "DO delete...");   
            				if (FileOpReturn.SUCCESS == FileOp.deleteSelectedFile("thumbnail")) {
            					db.deleteAllFileMark();
            					waitMediaScan(cur_sort_type);
                				Toast.makeText(ThumbnailView.this,
                						getText(R.string.Toast_msg_del_ok),
                						Toast.LENGTH_SHORT).show();
            				} else {
            					Toast.makeText(ThumbnailView.this,
            							getText(R.string.Toast_msg_del_nofile),
            							Toast.LENGTH_SHORT).show();
            				}         				          				
            				edit_dialog.dismiss();
            			}
            		} else {
    					Toast.makeText(ThumbnailView.this,
    							getText(R.string.Toast_msg_paste_wrongpath),
    							Toast.LENGTH_SHORT).show();
    					edit_dialog.dismiss();
            		}            		
				
    			}            	
            });            

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
    	}
    }

	 /** getDialogListAdapter */
    private SimpleAdapter getDialogListAdapter(int id) {
        return new SimpleAdapter(ThumbnailView.this,
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
    		map = new HashMap<String, Object>();    		
        	map.put("item_type", R.drawable.dialog_help_item_list);  
        	map.put("item_name", getText(R.string.dialog_help_item_list_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);   
        	list.add(map);   
    		map = new HashMap<String, Object>();    		
        	map.put("item_type", R.drawable.dialog_help_item_close);  
        	map.put("item_name", getText(R.string.dialog_help_item_close_str));            	        	
        	map.put("item_sel", R.drawable.dialog_item_img_unsel);   
        	list.add(map);          	
    		break;    		
    	}
    	return list;
    }    
    //option menu    
    public boolean onCreateOptionsMenu(Menu menu)
    {
   	 String ver_str = null;
   	 try {
			ver_str = getPackageManager().getPackageInfo("com.fb.FileBrower", 0).versionName;			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        menu.add(0, 0, 0, getText(R.string.app_name) + " v" + ver_str);
        return true;
    }
}


