package com.media.reader.model;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;

/**
 * 动态异步加载图片
 */

public class ImageManager {
	private static final String TAG = "ImageManager";
	private  HashMap<String,SoftReference<Bitmap>> bitmapCache;
	
	private static class ImageManagerHolder{
		private static ImageManager instance = new ImageManager();
	}
    private ImageManager() {
    	bitmapCache = new HashMap<String,SoftReference<Bitmap>>();
    }
    public static ImageManager getInstance(){
    	return ImageManagerHolder.instance;
    }
    
    public Bitmap loadBitmapFromCache(String key){
    	Bitmap bitmap=null;
    	if(bitmapCache.containsKey(key)){
    		SoftReference<Bitmap> softReference=bitmapCache.get(key);
    		bitmap=softReference.get();
    	}
    	return bitmap;
    }
    
    public void setCache(String key,Bitmap bitmap){
    	if(null!=bitmap&&!bitmap.isRecycled()){
    		if(bitmapCache.get(key)==null){
    			bitmapCache.put(key, new SoftReference<Bitmap>(bitmap));
    		}
    	}
    }
    
    public void clearBitmapCache(){
    	for (int i = 0; i < bitmapCache.size(); i++) {
			if (bitmapCache.get(i) != null) {
				bitmapCache.get(i).get().recycle();
				bitmapCache.get(i).clear();
			}
		}
    	bitmapCache.clear();
    }
    
    public synchronized void recycleById(int resourceId){
    	if(bitmapCache.containsKey(resourceId+"")){
    		SoftReference<Bitmap> softReference=bitmapCache.get(resourceId+"");
			if(softReference.get()!=null&&!softReference.get().isRecycled()){
				bitmapCache.get(resourceId+"").get().recycle();
				bitmapCache.get(resourceId+"").clear();
				bitmapCache.remove(resourceId+"");
			}
    	}
    }
    
    public synchronized Bitmap loadBitmapByResourceId(Context context,int resourceId){
    	Bitmap bitmap=null;
    	if(bitmapCache.containsKey(resourceId+"")){
    		SoftReference<Bitmap> softReference=bitmapCache.get(resourceId+"");
    		if(softReference.get()!=null){
    			if(!softReference.get().isRecycled()){
    				return softReference.get();
    			}else{
    				try{
    					bitmapCache.get(resourceId+"").get().recycle();
        				bitmapCache.get(resourceId+"").clear();
    					bitmapCache.remove(resourceId+"");
    	    			bitmap=decodeResource(context.getResources(),resourceId);
    	    			setCache(resourceId+"", bitmap);
    	    		}catch(OutOfMemoryError e){
    	            	e.printStackTrace();
    	            	clearBitmapCache();
    	            	System.gc();
    	            }
    			}
    		}else{
    			try{
        			bitmap=decodeResource(context.getResources(),resourceId);
        			setCache(resourceId+"", bitmap);
        		}catch(OutOfMemoryError e){
                	e.printStackTrace();
                	clearBitmapCache();
                	System.gc();
                }
    		}
    	}else{
    		try{
    			bitmap=decodeResource(context.getResources(),resourceId);
    			setCache(resourceId+"", bitmap);
    		}catch(OutOfMemoryError e){
            	e.printStackTrace();
            	clearBitmapCache();
            	System.gc();
            }
    	}
		return bitmap;
    }
    
    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }
}