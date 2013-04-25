package com.dbstar.app.base;

public interface SmartcardInterface {
	void getSmartcardInfo(FragmentObserver observer, int type);
	void manageCA(FragmentObserver observer, int type);
	void getMailContent(FragmentObserver observer, String id);
	void queryDeviceInfo(FragmentObserver observer, String[] keys);
}
