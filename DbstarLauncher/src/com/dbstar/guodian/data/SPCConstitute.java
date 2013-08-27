package com.dbstar.guodian.data;

import java.util.List;

/**
 * 
 * @author john
 *Step Power Consumption Constitute
 */
public class SPCConstitute {
    public String serviceSysDate ;
    public PowerData totalPower;
    public List<StepItemDetail> stepItemDetails;
    public static  class StepItemDetail{
        public String stepName;
        public String Count;
        public String Fee;
        public String CountPercent;
        public String FeePercent;
    }
}
