package com.dbstar.service;


public interface GDApplicationObserver {
	public void initializeApp();
	public void deinitializeApp();
	public void handleNotifiy(int what, Object data);
	public void handleEvent(int type, Object event);
}
