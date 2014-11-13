package com.dbstar.app;

public interface TaskObserver {
	public void onFinished(int resultCode, Object result);
}
