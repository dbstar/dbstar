package com.dbstar.app.adapter;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

import com.dbstar.R;
import com.dbstar.bean.ImageSet;

public class GalleryAdapter extends BaseAdapter {
    private ImageSet mImageSet;
    private Context mContext;
    private Integer[] mImages;

    public GalleryAdapter(Context context, Integer[] images) {
        mImageSet = new ImageSet(this);
        mContext = context;
        mImages = images;
    }

    /*
     * 提供一个对外初始化数据源的入口
     */
    public void add(String uri,String localPath,Bitmap bm){
        mImageSet.add(uri, localPath, bm);
        notifyDataSetChanged();
    }
    //　提供一个对外的释放数据源的入口
    public void recycle(){
        mImageSet.recycle();
    }
    
    @Override
    public int getCount() {
    	return Integer.MAX_VALUE;
    }

    @Override
    public Bitmap getItem(int position) {
    	if (mImageSet != null && mImageSet.getCount() > 0)
    		return mImageSet.getImage(position);  		
    	else {
    		InputStream is = mContext.getResources().openRawResource(mImages[position]);  
    		return BitmapFactory.decodeStream(is);
    	}
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 在这里，获取ｂｉｔｍａｐ时可能为空，注意对ｂｉｔｍａｐ进行ｎｕｌｌ判断。
    	ImageView view = new ImageView(mContext);
    	
    	if (mImageSet == null || mImageSet.getCount() <= 0) {
    		view.setImageResource(mImages[position % mImages.length]);	
    	} else {
    		Bitmap bitmap = mImageSet.getImage(position % mImageSet.getCount());
    		if (bitmap != null) {
    			view.setImageBitmap(bitmap);
    			view.setScaleType(ImageView.ScaleType.FIT_XY);
    			view.setAdjustViewBounds(true);
    			view.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		} 	
    	}
        return view;
    }

    
}
