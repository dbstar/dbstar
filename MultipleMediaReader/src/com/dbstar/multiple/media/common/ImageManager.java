package com.dbstar.multiple.media.common;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

public class ImageManager {
    
    private ThreadPoolExecutor executor;
    
    private static ImageManager mInstance;
    private Map<String , SoftReference<BitmapDrawable>> mCache;
    private Context mContext;
    
    private int corePoolSize=1;
    private int maximumPoolSize=5;
    private int keepAliveTime=2;
    
    private ImageManager (Context context){
        mCache = new HashMap<String, SoftReference<BitmapDrawable>>();
        //executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        this.mContext = context;
    }
    
    public synchronized static ImageManager getInstance(Context context){
        if(mInstance == null)
            mInstance = new ImageManager(context);
        return mInstance;
        
    }
    
    public void getBitmapDrawable(final String uri, final ImageCallback callback, final String viewKey) {
        synchronized (uri) {
            BitmapDrawable drawable = null;
            SoftReference<BitmapDrawable> sfb = mCache.get(uri);
            if (sfb != null) {
                if (callback == null)
                    return;
                drawable = sfb.get();
                if (drawable != null) {
                    callback.imageLoaded(drawable, viewKey);
                    return;
                }
            }

            new AsyncTask<Object, Integer, BitmapDrawable>() {
                String uri;
                ImageCallback callback;
                String viewKey;

                @Override
                protected BitmapDrawable doInBackground(Object... params) {
                    uri = (String) params[0];
                    callback = (ImageCallback) params[1];
                    viewKey = (String) params[2];
                    BitmapDrawable drawable = null;
                    SoftReference<BitmapDrawable> sfb = mCache.get(uri);
                    if (sfb != null) {
                        drawable = sfb.get();
                        if (drawable == null) {
                            sfb = new SoftReference<BitmapDrawable>(drawable = new BitmapDrawable(mContext.getResources(), uri));
                            mCache.put(uri, sfb);
                        }
                    } else {
                        sfb = new SoftReference<BitmapDrawable>(drawable = new BitmapDrawable(mContext.getResources(), uri));
                        mCache.put(uri, sfb);

                    }

                    return drawable;
                }

                protected void onPostExecute(BitmapDrawable result) {
                    if (callback != null)
                        callback.imageLoaded(result, viewKey);
                };
            }.execute(uri, callback, viewKey);
        }
    }
    public BitmapDrawable getBitmapDrawable(final String uri){
        synchronized (uri) {
            
        
        BitmapDrawable drawable = null;
        SoftReference<BitmapDrawable> sfb = mCache.get(uri);
        if(sfb != null){
            drawable = sfb.get();
            if(drawable != null){
                return drawable;
            }
        }
        
        if(drawable == null){
            sfb = new SoftReference<BitmapDrawable>(drawable = new BitmapDrawable(mContext.getResources(), uri));
            mCache.put(uri, sfb);
        }
         
        return drawable;
        }
    }
    public interface ImageCallback {
        public void imageLoaded(BitmapDrawable imageDrawable, String viewKey);
    }
    
    public void destroy(){
        if(mCache != null){
            Iterator<String> iterator = mCache.keySet().iterator();
            while (iterator.hasNext()) {
                SoftReference<BitmapDrawable> sf=  mCache.get(iterator.next());
                BitmapDrawable drawable = sf.get();
                if(drawable != null && drawable.getBitmap() != null){
                    drawable.getBitmap().recycle();
                }
                drawable = null;
                sf.clear();
            }
            mCache.clear();
            mCache = null;
        }
        mInstance = null;
    }
}
