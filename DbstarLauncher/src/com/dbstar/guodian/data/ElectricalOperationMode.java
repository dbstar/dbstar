package com.dbstar.guodian.data;

import java.util.List;

public class ElectricalOperationMode {
    public String ModelGuid;
    public String ModelId;
    public String ModelName;
    public String ModelPicId;
    
    public List<ModeElectrical []> ModelElectricalPageList;
    public List<ModeElectrical> ModelElectricalList;
    
    
    public static class ModeElectrical{
        public String DeviceGuid;
        public String DeviceName;
        public String typeId;
        public String Oper;
    }
}
