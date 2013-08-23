package com.dbstar.guodian.data;

import java.util.List;
import java.util.Map;

public class PaymentRecord{
    public String serviceSysDate;
    public List<Record> recentPaymentList;
    public List<Record> yearPaymentList;
    public Map<String, Map<String,Record>> paymentListYear;
    public PowerData avilablePowerAndFee;
    
    public static class Record {
       public String date;
       public float fee;
    }
}
