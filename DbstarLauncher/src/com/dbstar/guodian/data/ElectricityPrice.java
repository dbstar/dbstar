package com.dbstar.guodian.data;

import java.util.List;

public class ElectricityPrice {
	public static final String PRICETYPE_SINGLE = "0";
	public static final String PRICETYPE_STEP = "1";
	public static final String PRICETYPE_STEPPLUSTIMING = "2";
	public static final String PRICETYPE_TIMING = "3";
	
	public static final String STEP_1 = "1";
	public static final String STEP_2 = "2";
	public static final String STEP_3 = "3";
	
	public static final String CYCLETYPE_MONTH = "M";
	public static final String CYCLETYPE_YEAR = "Y";
	
	public static class StepPrice {
		public String GroupName;
		public String CycleType;
		public String Step;
		public String StepStartValue;
		public String StepEndValue;
		public String StepPrice;
		public List<PeriodPrice> PeriodPriceList;
	}
	
	public static class PeriodPrice {
		public String GroupName;
		public String PeriodType;
		public String TimePeriod;
		public String Price;
	}

	public String Type;
	public String SinglePrice;
	public List<StepPrice> StepPriceList;
	public List<PeriodPrice> PeriodPriceList;
}
