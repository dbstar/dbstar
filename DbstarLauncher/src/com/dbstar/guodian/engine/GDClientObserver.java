package com.dbstar.guodian.engine;

public interface GDClientObserver {
	public void notifyEvent(int type, Object event);
}
