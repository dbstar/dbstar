package com.dbstar.guodian.data;

import java.util.ArrayList;

public class AreaInfo {

	public static final int TypeProvince = 1;
	public static final int TypeCity = 2;
	public static final int TypeZone = 3;
	
	public static class Area {
		public int Type;
		public String Guid;
		public String Pid; // parent id
		public String Name;
		public ArrayList<Area> SubArea;
	}
	
	public ArrayList<Area> Provinces;

	public String ProvinceId;
	public String ProvinceName;
	public String CityId;
	public String CityName;
	public String ZoneId;
	public String ZoneName;
	
	public ArrayList<BusinessArea> BusinessList;
}
