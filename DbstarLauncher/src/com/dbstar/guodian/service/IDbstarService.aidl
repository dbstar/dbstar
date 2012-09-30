package com.dbstar.guodian.service;
import android.content.Intent;

interface IDbstarService{
	int startDvbpush();
	int stopDvbpush();
	int startTaskInfo();
	int stopTaskInfo();
	Intent getTaskInfo();
}
