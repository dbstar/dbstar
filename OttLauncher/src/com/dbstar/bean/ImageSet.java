package com.dbstar.bean;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.BaseAdapter;

import com.dbstar.http.SimpleWorkPool.ConnectWork;

public class ImageSet {
	private List<Image> mList = new ArrayList<Image>();

	private static BaseAdapter mAdapter;

	public ImageSet(BaseAdapter adapter) {
		mAdapter = adapter;
	}

	public int getCount() {
		if (mList == null)
			return 0;
		return mList.size();
	}

	/*
	 * 直接获取bitmap
	 */
	public Bitmap getImage(int position) {
		if (mList == null || mList.size() <= 0 || mList.size() <= position) {
			return null;
		}
		return mList.get(position).getImage();
	}

	public void add(String uri, String localPath, Bitmap bm) {
		Image image = new Image(uri, localPath, bm);
		if (mList == null)
			mList = new ArrayList<ImageSet.Image>();
		mList.add(image);
	}

	public void recycle() {
		if (mList == null | mList.size() <= 0)
			return;
		for (Image i : mList) {
			i.recycle();
		}
		mList.clear();
		mAdapter = null;
	}

	public static class Image {

		private String mUri;
		/*
		 * 在SDCard中缓存图片的绝对路径。
		 * 
		 * 如果可以通过uri也可以得到在SDCard中缓存的图片，这个属性可以去掉，完全看你自己缓存是怎么实现的。
		 */
		private String mLocalPath;

		private SoftReference<Bitmap> mImage;

		/*
		 * 提供两种构造函数
		 */
		public Image() {
		}

		// Bitmap 可以为空，这样会在使用这张图片时才会联网或者从SDCard中加载
		public Image(String uri, String localPath, Bitmap bm) {
			setUri(uri);
			setLocalPath(localPath);
			setImage(bm);
		}

		/*
		 * get 和 set 方法
		 */
		public void setUri(String uri) {
			this.mUri = uri;
		}

		public void setLocalPath(String localPath) {
			this.mLocalPath = localPath;
		}

		public void setImage(Bitmap bm) {
			if (bm == null)
				return;
			this.mImage = new SoftReference<Bitmap>(bm);
		}

		public String getUri() {
			return mUri;
		}

		public String getLocalPath() {
			return mLocalPath;
		}

		public Bitmap getImage() {
			Bitmap bm = null;
			if (mImage != null) {
				Bitmap tmp = mImage.get();
				if (tmp != null) {
					return tmp;
				}
			}
			bm = getImageFromNetOrSDCard();
			if (bm != null)
				mImage = new SoftReference<Bitmap>(bm);
			return bm;
		}

		/*
		 * 这个方法是当软连接里的图片被回收掉后，从新从T卡的缓存中获得图片或者联网从新获得图片。
		 */
		private Bitmap getImageFromNetOrSDCard() {
			Bitmap bm = null;
			bm = getImageFromeSDCard();
			if (bm == null) {
				bm = getImageFromeNet();
			}
			return bm;
		}

		/*
		 * 从T卡缓存获得图片
		 */
		private Bitmap getImageFromeSDCard() {
			return null;
		}

		/*
		 * 联网获得图片
		 */
		private Bitmap getImageFromeNet() {
			return null;
		}

		/*
		 * 回收该实例
		 */
		public void recycle() {
			mUri = null;
			mLocalPath = null;
			Bitmap bm = mImage.get();
			if (bm != null)
				bm.recycle();
			mImage = null;
		}
	}

	static class ImageWork extends ConnectWork<Bitmap> {
		private Image mImage;

		public ImageWork(int type, String uri, List<NameValuePair> params, Image image) {
			super(type, uri, params);
			mImage = image;
		}
		
		@Override
		public Bitmap processResult(HttpEntity entity) {
        	// 处理得到ｂｉｔｍａｐ
        	// TODO Auto-generated method stub
        	Bitmap bitmap = null;
        	if (entity != null) {        		
        		try {
        			InputStream inputStream = entity.getContent();
        			bitmap = BitmapFactory.decodeStream(inputStream);
        		} catch (IllegalStateException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        	return bitmap;
        }

		@Override
		public void connectComplete(Bitmap result) {
			mImage.setImage(result);
			mAdapter.notifyDataSetChanged();
		}
	}
}
