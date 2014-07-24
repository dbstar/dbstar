package com.dbstar.app.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

import com.dbstar.bean.ImageSet;

/**
 * 实现一次只滑动一张图片，无惯性
 * @author john
 *
 */
public class FlashGallery extends Gallery {
	
	private Context context;
	private Integer[] pictures;
	private ImageSet images;

	public FlashGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlashGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setImageActivity(Context context, Integer[] pictures, ImageSet mImageSet) {
		this.context = context;
		this.pictures = pictures;
		this.images = mImageSet;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//		int kEvent;
//		if (isScrollingLeft(e1, e2)) {
//			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
//		} else {
//			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
//		}
//		onKeyDown(kEvent, null);

		if (this.getSelectedItemPosition() == 0) {// 实现后退功能
			
			if (images == null || images.getCount() == 0) {				
				this.setSelection(pictures.length);
			} else {
				this.setSelection(images.getCount());				
			}
			
		}
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
	
//	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
//		return e2.getX() > e1.getX();
//	}

//	@Override
//	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
//			float distanceY) {
//		return super.onScroll(e1, e2, distanceX, distanceY);
//	}
}
