package com.dbstar.guodian.data;

import java.util.List;
/**
 * 
 * @author john
 *period power consumption constitute
 */
public class PPCConstitute {
    public String serviceSysDate ;
    public PowerData totalPower;
    public List<PeriodItemDetail> periodItemDetails;
    public static  class PeriodItemDetail{
        public String periodName;
        public String Count;
        public String Fee;
        public String CountPercent;
        public String FeePercent;
    }
}
