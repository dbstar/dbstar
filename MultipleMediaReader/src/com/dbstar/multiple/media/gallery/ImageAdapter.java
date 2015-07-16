package com.dbstar.multiple.media.gallery;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;

import com.dbstar.multiple.media.model.FilmInfo;
import com.dbstar.multiple.media.util.ImageUtil;

/**
 * @author LittleLiByte
 * 
 */
public class ImageAdapter extends FancyCoverFlowAdapter {
	private Context context;
	private List<FilmInfo> filmList;

	public ImageAdapter(Context context, List<FilmInfo> filmList) {
		this.context = context;
		this.filmList = filmList;
	}

	@Override
	public int getCount() {
		if (filmList != null && filmList.size() > 0) {
			return filmList.size();
		} else 
			return 0;
	}

	public void refresh(List<FilmInfo> filmList) {
		this.filmList = filmList;
		notifyDataSetChanged();
	}
	
	@Override
	public Object getItem(int position) {
		if (filmList == null || filmList.size() <= 0) {
			return null;
		} else 
			return filmList.get(position);			
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getCoverFlowItem(int position, View reusableView, ViewGroup parent) {
		ImageView imageView = (ImageView) reusableView;

		if (imageView == null) {
			imageView = new ImageView(context);
		}
		// ps.电影海报宽高比例一般为3：4
//		Log.d("GalleryImageAdapter", "-===============mImageUri.get(position = " + filmList.get(position));
//		Bitmap bm = ImageUtil.getGalleryBitmap(filmList.get(position).getFilmImageLink());
		BitmapDrawable bitmapDrawable = ImageUtil.setDrawable(filmList.get(position).getFilmImageLink(), 190);
		imageView.setImageBitmap(bitmapDrawable.getBitmap());

		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new Gallery.LayoutParams(190, 270));
		return imageView;
	}

}