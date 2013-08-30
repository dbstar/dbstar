package com.fb.FileBrower;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/* android.widget.BaseAdapter */
public class ThumbnailAdapter extends BaseAdapter{
  
  private LayoutInflater mInflater;
  //private Bitmap mIcon_folder;
  //private Bitmap mIcon_file;
  //private Bitmap mIcon_audio;
  //private Bitmap mIcon_video;
  //private Bitmap mIcon_apk;
 // private Bitmap mIcon_image;
  private List<String> items;
  private String file_path;
  private static  List<Photo> images =  new  ArrayList<Photo>();  
  Context c;
  Bitmap bitMap = null;
  /* MyAdapter  */  
  public ThumbnailAdapter(Context context,List<String> it){
   
    mInflater = LayoutInflater.from(context);  
    items = it;  
    //thumbs = thumb;
    c = context;
    images.clear(); 
    //mIcon_folder = BitmapFactory.decodeResource(context.getResources(),R.drawable.item_preview_dir);      //
    //mIcon_file = BitmapFactory.decodeResource(context.getResources(),R.drawable.txt_default);          //
  //mIcon_image = BitmapFactory.decodeResource(context.getResources(),R.drawable.item_preview_photo);        //
    //mIcon_audio = BitmapFactory.decodeResource(context.getResources(),R.drawable.item_preview_music);        //
    //mIcon_video = BitmapFactory.decodeResource(context.getResources(),R.drawable.item_preview_video);        //
   // mIcon_apk = BitmapFactory.decodeResource(context.getResources(),R.drawable.txt_default);            //apk
  }  
  public int getCount(){
    return images.size();
  }
  public Object getItem(int position){
    return images.get(position);
  }
  public long getItemId(int position){
    return position;
  }
  
  public   void  addPhoto(Photo photo) { 	  
		images.add(photo); 		  	         
  }  
  public   void  refreshPhoto(Photo photo) { 
	  String default_file,decode_file;
	  for(int i=0;i<images.size();i++){
		  default_file = images.get(i).getFilename();
		  decode_file = photo.getFilename(); 
		  if(default_file.equals(decode_file) ){
			  images.set(i, photo);			  
		  }
	  }         
  }  
  public   void  clear() {   
      images.clear();   
  }  
  public   void  notifyDataSetChanged(){   
      super .notifyDataSetChanged();   
  }    
  public View getView(int position,View convertView,ViewGroup par){
    
    ViewHolder holder = null;
      if(convertView == null){
        /* list_itemsLayout */
        convertView = mInflater.inflate(R.layout.gridview_item, null);
        /* holdertexticon */
        holder = new ViewHolder();
        holder.f_title = ((TextView) convertView.findViewById(R.id.itemText));       
        holder.f_icon = ((ImageView) convertView.findViewById(R.id.itemImage));
        holder.f_mark = ((ImageView) convertView.findViewById(R.id.itemMark)) ;
        convertView.setTag(holder);
      }else{
        holder = (ViewHolder) convertView.getTag();
      }      
      /* icon */      
      file_path = images.get(position).getFilename();
      final File f = new File(file_path);
      String file_name = f.getName();
      if(f.getParent().equals("/mnt")){
    	  holder.f_title.setText(FileOp.getDeviceName(c, file_path));
      }
      else{
    	  String tmp_file_name = FileOp.getShortName(file_path);
    	  holder.f_title.setText(tmp_file_name);
      }
      
      
      if(FileOp.isFileSelected(file_path,"thumbnail")){
    	  
    	  holder.f_mark.setImageResource(R.drawable.item_img_sel);
      }
      else{
    	  holder.f_mark.setImageResource(R.drawable.item_img_nosel);
      }
      bitMap = images.get(position).getBm();
      if(f.isDirectory()){
    	  if(f.getParent().equals("/mnt")){
    		  int icon = FileOp.getThumbDeviceIcon(c,file_name);   		 
    		  holder.f_icon.setImageResource(icon);
    		  
    	  }
    	  else{
    		  holder.f_icon.setImageResource(R.drawable.item_preview_dir);
    		  
    	  }
    	               
      }else{    
    	 if(FileOp.isVideo(file_name)){    		
    		 holder.f_icon.setImageResource(R.drawable.item_preview_video);
    	 }
    	 else if(FileOp.isMusic(file_name)){
    		 holder.f_icon.setImageResource(R.drawable.item_preview_music);    		 
    	 }
    	 else if(FileOp.isPhoto(file_name)){    		
    		 if(bitMap ==null){
    		 
    			holder.f_icon.setImageResource(R.drawable.item_preview_photo);    
    		 }
    		 
    		 else{
    			 holder.f_icon.setImageBitmap(bitMap);    			
    		 }   		 
    	 }
    	 else{
			  int icon = FileOp.getThumbDeviceIcon(c,file_name);   		 
    		  holder.f_icon.setImageResource(icon);
    	 }       
      }
     
    return convertView;
  }
  /**
   * class ViewHolder 
   * */
  private class ViewHolder{
    TextView f_title;   
    ImageView f_icon;
    ImageView f_mark;
  } 
}  