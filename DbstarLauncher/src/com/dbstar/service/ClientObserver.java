package com.dbstar.service;


public interface ClientObserver {
	public void updateData(int type, int param1, int param2, Object data);
	public void updateData(int type, Object key, Object data);
	
	public void updatePage();
}
