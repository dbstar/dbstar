package com.dbstar.guodian.model;

public interface GDApplicationObserver {
	public void initializeApp();
	public void handleNotifiy(int what, Object data);
}
