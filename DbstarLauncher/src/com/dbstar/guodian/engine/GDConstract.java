package com.dbstar.guodian.engine;

public class GDConstract {
	public static final int DATATYPE_LOGIN = 0x60001;
	public static final int DATATYPE_POWERPANELDATA = 0x60002;
	public static final int DATATYPE_BILLMONTHLIST = 0x60003;
	public static final int DATATYPE_BILLDETAILOFMONTH = 0x60004;
	public static final int DATATYPE_BILLDETAILOFRECENT = 0x60005;
	
	public static final int DATATYPE_NOTICES = 0x60006;
	public static final int DATATYPE_USERAREAINFO = 0x60007;
	public static final int DATATYPE_BUSINESSAREA = 0x60008;
	public static final int DATATYPE_CITYES = 0x60009; 
	public static final int DATATYPE_ZONES = 0x60010; 
	public static final int DATATYPE_ELECTRICAL_POWER_CONSUMPTION_CONSTITUTE = 0x60011; 
	public static final int DATATYPE_PAYMENT_RECORDS = 0x60012; 
	public static final int DATATYPE_YREAR_FEE_DETAIL = 0x60013; 
	public static final int DATATYPE_FAMILY_POWER_EFFICENCY = 0x60014; 
	public static final int DATATYPE_STEP_POWER_CONSUMPTION_CONSTITUTE = 0x60015; 
	public static final int DATATYPE_PERIOD_POWER_CONSUMPTION_CONSTITUTE = 0x60016;
	public static final int DATATYPE_STEP_POWER_CONSUMPTION_TRACK = 0x60017; 
	public static final int DATATYPE_EQUMENTLIST = 0x60018;
	public static final int DATATYPE_POWER_CONSUMPTION_TREND = 0x60019;
	public static final int DATATYPE_POWER_TIPS = 0x60020;
	public static final int DATATYPE_ROOM_LIST = 0x60021;
	public static final int DATATYPE_ROOM_ELECTRICAL_LIST = 0x60022;
	public static final int DATATYPE_TUNN_ON_OFF_ELECTRICAL = 0x60023;
	public static final int DATATYPE_REFRESH_ELECTRICAL = 0x60024;
	public static final int DATATYPE_MODEL_LIST = 0x60025;
	public static final int DATATYPE_MODEL_ELECTRICAL_LIST = 0x60026;
	public static final int DATATYPE_EXECUTE_MODE = 0x60027;
	public static final int DATATYPE_TIMED_TASK_LIST = 0x60028;
	public static final int DATATYPE_NO_TASK_ELCTRICAL_LIST = 0x60029;
	public static final int DATATYPE_ADD_TIMED_TASK = 0x60030;
	public static final int DATATYPE_MODIFY_TIMED_TASK = 0x60031;
	public static final int DATATYPE_DELETE_TIMED_TASK = 0x60032;
	public static final int DATATYPE_EXECUTE_TIMED_TASK = 0x60033;
	
	public static final int DATATYPE_DEFAULT_POWER_TARGET = 0x60034;
	public static final int DATATYPE_POWER_TARGET = 0x60035;
	public static final int DATATYPE_SETTING_POWER_TARGET = 0x60036;
	
	
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
