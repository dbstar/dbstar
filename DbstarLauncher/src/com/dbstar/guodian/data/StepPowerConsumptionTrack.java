package com.dbstar.guodian.data;

import java.util.List;

public class StepPowerConsumptionTrack {
    public PowerData totalConsumption;
    public List<DateStepPower> dateStepPowerList;
    public String serviceSysDate;
    
    
    public static class DateStepPower{
        public String dateTime;
        public List<StepPower> stepPowerList;
        public float allCount;
    }
    
    public static class StepPower{
        public String dateTime;
        public String count;
        public String fee;
        public String stepName;
    }
}
