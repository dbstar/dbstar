package com.dbstar.guodian.data;

import java.util.List;

public class RoomData {
	public String RoomGuid;
	public String RoomName;
	
	public List<RoomElectrical> EletricalList;
	public List<RoomElectrical []> ElePageList;
	public static class RoomElectrical{
	    public String RoomGuid;
	    public String DeviceName;
	    public String AdapterFlag;
	    public String EleAmountOfDay;
	    public String EleAmountOfMonth;
	    public String RealTimePower;
	    public String EleDeviceCode;
	    public String AdapterSeridNo;
	    public String DevicePic;
	    public String DeviceComCode;
	    public String CompanyName;
	    public String DeviceTypeCode;
	    public String DeviceTypeName;
	    public String ComTypeModelGuid;
	    public String DeviceModelCode;
	    public String StandByPowerValue;
	    public String OpenStandByRemind;
	    public String DeviceGuid;
	}
	
	public static class ElecTurnResponse extends ResultData{
	    public String DeviceGuid;
	    public String RealTimePowerValue;
	    
	}
	public static class ElecRefreshResponse{
        public String DeviceGuid;
        public String EleAmountOfDay;
        public String EleAmountOfMonth;
        public String RealTimePowerValue;
        
    }
}
