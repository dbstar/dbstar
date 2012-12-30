package com.dbstar.app.base;

public interface FragmentObserver {
	public void serviceReady(EngineInterface engine);
	public void serviceStop();
	public void updateData(FragmentObserver observer, int type, Object key, Object data);
	public void notifyEvent(FragmentObserver observer, int type, Object event);
	public boolean onBackkeyPress(); 
}
