package com.dbstar.guodian.data;

import java.util.List;

public class PowerConsumptionTrend {
    
    public List<ConsumptionPercent> TongBiList;
    public List<ConsumptionPercent> HuanBiList;
    
    public static class ConsumptionPercent{
        public String DateTime;
        public String CountPercent;
        public String FeePercent;
    }
}
