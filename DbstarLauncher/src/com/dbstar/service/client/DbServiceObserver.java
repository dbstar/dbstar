package com.dbstar.service.client;

public interface DbServiceObserver {
	public void onServerStarted();
	public void onServerRestarted();
	public void onServerStopped();
}
