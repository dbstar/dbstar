package com.dbstar.service;


public interface GDApplicationObserver {
	public void initializeApp();
	public void handleNotifiy(int what, Object data);
}
