package com.dbstar.app.base;

import android.app.Service;

import com.dbstar.guodian.engine1.RequestParams;

public interface EngineInterface {
	Service getService();
	void registerObserver(FragmentObserver observer);
	void unregisterObserver(FragmentObserver observer);
	/*void request(int type, Object args);*/
	void request(RequestParams params);
}
