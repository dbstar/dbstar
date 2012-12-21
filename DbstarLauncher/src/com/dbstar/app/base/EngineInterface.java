package com.dbstar.app.base;

import android.app.Service;

public interface EngineInterface {
	public Service getService();
	public void registerObserver(FragmentObserver observer);
	public void unregisterObserver(FragmentObserver observer);
	
	public void getSmartcardInfo(FragmentObserver observer, int type);
	public void manageCA(FragmentObserver observer, int type);
	public void getMailContent(FragmentObserver observer, String id);
}
