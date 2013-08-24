package com.fb.FileBrower;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.fb.FileBrower.FileBrowerDatabase.ThumbnailCursor;




public class ThumbnailAdapter1 extends SimpleAdapter{
	public ThumbnailAdapter1(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

	public void setViewImage (ImageView v, String value) {
	    
		//if (value.startsWith("/mnt/sdcard1")) {
		//	  super.setViewImage(v, value);
		//} else {
			  
			  ThumbnailCursor cc = null;
			  try {
				  cc = ThumbnailView1.db.getThumbnailByPath(value);
				  if(cc != null && cc.moveToFirst()) {
					  if (cc.getCount()  > 0) {
						  Drawable drawable = Drawable.createFromStream( 
							        new ByteArrayInputStream(cc.getColFileData()), 
					        		"image.png");
						  if (drawable != null)
							  v.setImageDrawable(drawable);
						  else
							  super.setViewImage(v, R.drawable.item_preview_photo);

					  } else
						  super.setViewImage(v, R.drawable.item_preview_photo);
				  } else
					  super.setViewImage(v, R.drawable.item_preview_photo);
			  } finally {
				  if (cc != null) cc.close();
			  }
		//}
	}
}