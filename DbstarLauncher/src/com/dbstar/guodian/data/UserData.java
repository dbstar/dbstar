package com.dbstar.guodian.data;

public class UserData {
	public String CtrlNoGuid;
	public String CtrlSerialNo;

	public String AreaName;
	public String UserType;
	
	public UserInfo UserInfo;

	public static class UserInfo {
		public String Account;
		public String Guid;
		public String AreaGuid;
		public String Name;
		public String Sexual;
		public String Mobile;
		public String Phone;
		public String Address;
		public String Email;
		public String PriceType;
		public String PriceGroupName;
		public String ElecCard;
		public String UserType;
		public String UserAction;
		public String AreaIdPath;
	}
}
