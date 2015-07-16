package com.dbstar.multiple.media.gallery;

import android.content.Context;

public class ViewUtil {
	
	/**
	 * dp转px
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int  Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
	
	/**
	 * px转dp
	 * @param context
	 * @param px
	 * @return
	 */
	public static int Px2Dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}
}
