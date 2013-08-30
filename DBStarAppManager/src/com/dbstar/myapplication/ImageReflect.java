package com.dbstar.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.view.View;

/**
 * ʹ ImageView ����Ч��
 * ������ ��ӰЧ��
 * 
 * @author Administrator
 * 
 */
public class ImageReflect {

	private static int reflectImageHeight = 100;

	public static Bitmap convertViewToBitmap(View paramView) {
		paramView.buildDrawingCache();
		return paramView.getDrawingCache();
	}

	public static Bitmap createCutReflectedImage(Bitmap paramBitmap,
			int paramInt) {
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		Matrix localMatrix = new Matrix();
		localMatrix.preScale(1.0F, -1.0F);
		Bitmap localBitmap1 = Bitmap.createBitmap(paramBitmap, 0, j
				- reflectImageHeight - paramInt, i, reflectImageHeight,
				localMatrix, true);
		Bitmap localBitmap2 = Bitmap.createBitmap(i, reflectImageHeight,
				Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap2);
		localCanvas.drawBitmap(localBitmap1, 0.0F, 0.0F, null);
		LinearGradient localLinearGradient = new LinearGradient(0.0F, 0.0F,
				0.0F, localBitmap2.getHeight(), -2130706433, 16777215,
				Shader.TileMode.CLAMP);
		Paint localPaint = new Paint();
		localPaint.setShader(localLinearGradient);
		localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		localCanvas.drawRect(0.0F, 0.0F, i, localBitmap2.getHeight(),
				localPaint);
		if (!localBitmap1.isRecycled())
			localBitmap1.recycle();
		System.gc();
		return localBitmap2;
	}

	public static Bitmap createReflectedImage(Bitmap paramBitmap) {
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		Matrix localMatrix = new Matrix();
		localMatrix.preScale(1.0F, -1.0F);
		Bitmap localBitmap1 = Bitmap.createBitmap(paramBitmap, 0, j
				- reflectImageHeight, i, reflectImageHeight, localMatrix, true);
		Bitmap localBitmap2 = Bitmap.createBitmap(i, reflectImageHeight,
				Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap2);
		localCanvas.drawBitmap(localBitmap1, 0.0F, 0.0F, null);
		LinearGradient localLinearGradient = new LinearGradient(0.0F, 0.0F,
				0.0F, localBitmap2.getHeight(), -2130706433, 16777215,
				Shader.TileMode.CLAMP);
		Paint localPaint = new Paint();
		localPaint.setShader(localLinearGradient);
		localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		localCanvas.drawRect(0.0F, 0.0F, i, localBitmap2.getHeight(),
				localPaint);
		return localBitmap2;
	}
}
