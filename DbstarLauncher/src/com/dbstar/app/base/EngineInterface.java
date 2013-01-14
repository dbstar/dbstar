package com.dbstar.app.base;

import android.app.Service;

public interface EngineInterface {
	Service getService();
	void registerObserver(FragmentObserver observer);
	void unregisterObserver(FragmentObserver observer);
}
