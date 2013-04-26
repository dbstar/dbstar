package com.dbstar.guodian.data;

import java.util.List;

public class ElectriDimension {
    public PowerData totalPower;
    public List<CountAndFeePercent> AllCountFeePercent;
    
    public static class CountAndFeePercent{
        public String order;
        public String ElecGuid;
        public String ElecTypeId;
        public String ElecName;
        public String Count;
        public String Fee;
        public String CountPercent;
        public String FeePercent;
    }
}
