package com.dbstar.app;

public interface TaskController {
	
	public void taskFinished();

	public void registerTask(TaskObserver observer);
}
