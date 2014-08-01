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
	 * ֱ�ӻ�ȡbitmap
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
		 * ��SDCard�л���ͼƬ�ľ���·����
		 * 
		 * �������ͨ��uriҲ���Եõ���SDCard�л����ͼƬ��������Կ���ȥ������ȫ�����Լ���������ôʵ�ֵġ�
		 */
		private String mLocalPath;

		private SoftReference<Bitmap> mImage;

		/*
		 * �ṩ���ֹ��캯��
		 */
		public Image() {
		}

		// Bitmap ����Ϊ�գ���������ʹ������ͼƬʱ�Ż��������ߴ�SDCard�м���
		public Image(String uri, String localPath, Bitmap bm) {
			setUri(uri);
			setLocalPath(localPath);
			setImage(bm);
		}

		/*
		 * get �� set ����
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
		 * ��������ǵ����������ͼƬ�����յ��󣬴��´�T���Ļ����л��ͼƬ�����������»��ͼƬ��
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
		 * ��T��������ͼƬ
		 */
		private Bitmap getImageFromeSDCard() {
			return null;
		}

		/*
		 * �������ͼƬ
		 */
		private Bitmap getImageFromeNet() {
			return null;
		}

		/*
		 * ���ո�ʵ��
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
        	// ����õ���������
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
