package com.dbstar.settings.base;

import android.os.Bundle;

public interface PageManager {

	public static class Page {
		public int Id;
		public String ComponentName;
		public Bundle Args;
		
		public int NextPageId;
		public int PrevPageId;
		
		public Page() {
			Id = -1;
			ComponentName = null;
			NextPageId = -1;
			PrevPageId = -1;
		}
	}
	
	public void nextPage(int currentPageId, int nextPageId);
	public void prevPage(int currentPageId);
}
