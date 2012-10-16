package com.dbstar.model;

import java.util.List;

import android.graphics.Bitmap;

public class TV {
	public ContentData Content;

	public String Type;
	public String Region;
	public String Rate;
	public String Description;
	public Bitmap Thumbnail;
	public int EpisodesCount;
	
	public EpisodeItem[] Episodes;
	
	public List<EpisodeItem[]> EpisodesPages;
	public int EpisodesPageCount;
	public int EpisodesPageNumber;
	
	public TV() {
		Content = null;
		Description = null;
		Thumbnail = null;
		
		EpisodesPages = null;
	}
	
	
	public static class EpisodeItem {
		public ContentData Content;
		public int Number;
		public boolean Watched;
		public String Url;
		
		public EpisodeItem() {
			Number = -1;
			Watched = false;
		}
	}
}
