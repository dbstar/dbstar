package com.dbstar.model;

import android.graphics.Bitmap;

public class Movie {
	public ContentData Content;

	public String Type;
	public String Region;
	public String Rate;
	public String Description;
	public String ThumbnailPath;	
	public Bitmap Thumbnail;
	
	public Movie() {
		Content = null;
		Description = null;
		Thumbnail = null;
	}
}
