package com.dbstar.guodian.data;

import java.util.List;
/**
 * 
 * @author john
 *Electrical power consumption constitute
 */
public class EPCConstitute {
    public PowerData totalPower;
    public List<ElectricalItemDetail> electricalItemDetails;
    public String serviceSysDate ;
    public static class ElectricalItemDetail{
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
