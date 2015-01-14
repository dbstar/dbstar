/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.media.zlibrary.ui.android.view;

import android.graphics.Bitmap;
import com.media.player.common.Utils;
import com.media.zlibrary.core.view.ZLView;

public class BitmapManager {
	private final int SIZE = 4;
	private final PageEntity[] pageEntities = new PageEntity[SIZE];
	private final ZLView.PageIndex[] myIndexes = new ZLView.PageIndex[SIZE];

	private int myWidth;
	private int myHeight;
	
	private static BitmapManager instance;
	
	private  ZLAndroidWidget myWidget;
	class PageEntity{
		Bitmap bmp;
		EdgePosition edgePos;
	}
	
	public enum EdgePosition{
		NO_EDGE,
		EDGE_RIGHT,
		EDGE_LEFT
	}
	
	public BitmapManager(ZLAndroidWidget widget) {
		myWidget = widget;
		for (int i = 0; i < SIZE; ++i) {
			pageEntities[i] = new PageEntity();
		}
		instance = this;
	}

	public static BitmapManager Instance() {
		return instance;
	}
	
	void setSize(int w, int h) {
		if (myWidth != w || myHeight != h) {
			myWidth = w;
			myHeight = h;
			for (int i = 0; i < SIZE; ++i) {
				if(pageEntities[i].bmp!=null&&!pageEntities[i].bmp.isRecycled()){
					pageEntities[i].bmp.recycle();
				}
				pageEntities[i].bmp = null;
				pageEntities[i].edgePos = EdgePosition.EDGE_RIGHT;
				myIndexes[i] = null;
			}
			System.gc();
			System.gc();
			System.gc();
		}
	}

	/**
	 * progress :read progress page index
	 */
	public Bitmap getBitmap(ZLView.PageIndex index, EdgePosition edgepos, ZLView.PageIndex progress) {
		
		/**
		 * drop cache for change background,
		 * so now background will change every time
		 * */
		for (int i = 0; i < SIZE; ++i) {
			if (index == myIndexes[i]) {
				if(pageEntities[i].edgePos!=edgepos){
					paintPage(i,index,edgepos,progress);
				}
				if(pageEntities[i].bmp!=null&&!pageEntities[i].bmp.isRecycled()){
					return pageEntities[i].bmp;
				}else{
					/**
					 * exception the bitmap is recycled, rarely appear
					 * should repaint
					 * */
					pageEntities[i].bmp = null;
					pageEntities[i].bmp = Bitmap.createBitmap(myWidth, myHeight, Bitmap.Config.ARGB_8888);
					paintPage(i,index,edgepos,progress);
					return pageEntities[i].bmp;
				}
			}
		}
		final int iIndex = getInternalIndex(index);
		myIndexes[iIndex] = index;
		if (pageEntities[iIndex].bmp == null) {
			try{
				Utils.printLogError(getClass().getSimpleName(), "pageEntities bmp==null create it iIndex="+iIndex);
				pageEntities[iIndex].bmp = Bitmap.createBitmap(myWidth, myHeight, Bitmap.Config.ARGB_8888);
			}catch (OutOfMemoryError e) {
				// TODO: handle exception
				Utils.printLogError(getClass().getSimpleName(), "OutOfMemoryError "+e.getMessage());
				e.printStackTrace();
				System.gc();
				System.gc();
				pageEntities[iIndex].bmp = Bitmap.createBitmap(myWidth, myHeight, Bitmap.Config.ARGB_8888);
			}
		}
		paintPage(iIndex,index,edgepos,progress);
		return pageEntities[iIndex].bmp;
	}
	
	private void paintPage(int cacheindex, ZLView.PageIndex pageindex,EdgePosition edgepos,ZLView.PageIndex progress){
		myWidget.drawOnBitmap(myWidth,myHeight,pageEntities[cacheindex].bmp, pageindex,edgepos,progress);
		pageEntities[cacheindex].edgePos = edgepos;
	}
	
	private int getInternalIndex(ZLView.PageIndex index) {
		for (int i = 0; i < SIZE; ++i) {
			if (myIndexes[i] == null) {
				return i;
			}
		}
		for (int i = 0; i < SIZE; ++i) {
			if (myIndexes[i] != ZLView.PageIndex.current&&myIndexes[i] != ZLView.PageIndex.curright) {
				return i;
			}
		}
		reset();
		return 0;
//		throw new RuntimeException("That's impossible");
	}

	void reset() {
		for (int i = 0; i < SIZE; ++i) {
			myIndexes[i] = null;
		}
	}

	void shift(boolean forward) {
		for (int i = 0; i < SIZE; ++i) {
			if (myIndexes[i] == null) {
				continue;
			}
			myIndexes[i] = forward ? myIndexes[i].getPrevious() : myIndexes[i].getNext();
		}
	}
	
	/**
	 * destroy bitmaps
	 * */
	public void destroy() {
		Utils.printLogError(getClass().getSimpleName(), "destroy called ");
		for (int i = 0; i < SIZE; ++i) {
			if(pageEntities[i].bmp!=null&&!pageEntities[i].bmp.isRecycled()){
				pageEntities[i].bmp.recycle();
				pageEntities[i].bmp= null;
				pageEntities[i].edgePos = EdgePosition.EDGE_RIGHT;
			}
			myIndexes[i] = null;
		}
		System.gc();
	}
}
