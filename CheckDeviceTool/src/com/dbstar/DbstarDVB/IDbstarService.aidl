package com.dbstar.DbstarDVB;
import android.content.Intent;

interface IDbstarService{
	int initDvbpush();
	int uninitDvbpush();
	Intent sendCommand(int cmd, String buf, int len);
}
