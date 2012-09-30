package com.dbstar.DbstarDVB;

public class TaskInfo {
	public static final int STATUS = 0x0001;

	public static final int STATUS_INITED = 0x1001;
	public static final int STATUS_STARTED = 0x1002;
	public static final int STATUS_PAUSED = 0x1003;
	public static final int STATUS_STOPPED = 0x1004;
	public static final int STATUS_FINISHED = 0x1005;
	public static final int STATUS_AUTOPAUSED = 0x1006;
	public static final int STATUS_ERROR = 0x1009;
	public static final int STATUS_UNKNOW = 0x1010;

	public static final int ERROR_OK = 0x2000;

	public static final int ERROR_TASK_NULL = 0x2001;
	public static final int ERROR_TASK_EXIST = 0x2002;
	public static final int ERROR_TASK_FINISHED = 0x2003;

	public static final int ERROR_FILE_EXIST = 0x2011;
	public static final int ERROR_FILE_NULL = 0x2012;
	public static final int ERROR_FILE_CREATE_FAILED = 0x2013;
	public static final int ERROR_FILE_RENAME_FAILED = 0x2014;

	public static final int ERROR_DATA_GET_FAILED = 0x2021;
	public static final int ERROR_UNKONW = 0x2100;

	public int status;

	public String id;
	public String fileName;
	public long downloadedSize = 0;
	public long totalSize = 0;
}
