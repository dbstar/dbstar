package com.dbstar.guodian.egine;

public class GDConstract {
	public static final int DATATYPE_LOGIN = 0x60001;
	public static final int DATATYPE_POWERPANELDATA = 0x60002;
	public static final int DATATYPE_BILLMONTHLIST = 0x60003;
	public static final int DATATYPE_BILLDETAILOFMONTH = 0x60004;
	public static final int DATATYPE_BILLDETAILOFRECENT = 0x60005;
	
	public static final int DATATYPE_NOTICES = 0x60006;
	public static final int DATATYPE_USERAREAINFO = 0x60007;
	public static final int DATATYPE_BUSINESSAREA = 0x60008;
	
	public static final String KeyPriceType = "price_type";
	
	public static final String KeyUserName = "user_name";
	public static final String KeyDeviceNo = "device_no";
	public static final String KeyUserAddress = "user_address";
	
	public static final String KeyUserAreaId = "user_area_id";
	
	public static final int PriceTypeSingle = 0;
	public static final int PriceTypeStep = 1;
	public static final int PriceTypeStepPlusTiming = 2;
	public static final int PriceTypeTiming = 3;
	
	
	public static final String ErrorStrRepeatLogin = "ERROR-LONG-RepeatLogin";
	public static final int ErrorCodeUnKnown = 0xE0000;
	public static final int ErrorCodeRepeatLogin = 0xE001;
}
