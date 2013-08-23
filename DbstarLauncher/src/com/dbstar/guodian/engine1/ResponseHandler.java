package com.dbstar.guodian.engine1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

import com.dbstar.guodian.data.AddTimedTaskResponse;
import com.dbstar.guodian.data.AreaInfo;
import com.dbstar.guodian.data.BillDetailData;
import com.dbstar.guodian.data.BillDetailListData;
import com.dbstar.guodian.data.BusinessArea;
import com.dbstar.guodian.data.EPCConstitute;
import com.dbstar.guodian.data.ElectricalOperationMode;
import com.dbstar.guodian.data.ElectricalOperationMode.ModeElectrical;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.Notice;
import com.dbstar.guodian.data.PPCConstitute;
import com.dbstar.guodian.data.PaymentRecord;
import com.dbstar.guodian.data.PaymentRecord.Record;
import com.dbstar.guodian.data.PowerConsumptionTrend;
import com.dbstar.guodian.data.PowerData;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.guodian.data.PowerTarget;
import com.dbstar.guodian.data.PowerTips;
import com.dbstar.guodian.data.ResultData;
import com.dbstar.guodian.data.RoomData;
import com.dbstar.guodian.data.RoomData.ElecRefreshResponse;
import com.dbstar.guodian.data.RoomData.ElecTurnResponse;
import com.dbstar.guodian.data.RoomData.RoomElectrical;
import com.dbstar.guodian.data.SPCConstitute;
import com.dbstar.guodian.data.StepPowerConsumptionTrack;
import com.dbstar.guodian.data.TimedTask;
import com.dbstar.guodian.engine1.ClientRequestService.RequestTask;
import com.dbstar.guodian.parse.AreaInfoHandler;
import com.dbstar.guodian.parse.BillDetailDataHandler;
import com.dbstar.guodian.parse.BillDetailOfRecentDataHandler;
import com.dbstar.guodian.parse.BillMonthListHandler;
import com.dbstar.guodian.parse.BusinessAreaHandler;
import com.dbstar.guodian.parse.EPCConstituteDataHandler;
import com.dbstar.guodian.parse.FamilyPowerEfficencyDataHandler;
import com.dbstar.guodian.parse.LoginDataHandler;
import com.dbstar.guodian.parse.NoticeDataHandler;
import com.dbstar.guodian.parse.PPCConstituteDataHandler;
import com.dbstar.guodian.parse.PanelDataHandler;
import com.dbstar.guodian.parse.PaymentRecordsDataHandler;
import com.dbstar.guodian.parse.PowerConsumptionTrendDataHandler;
import com.dbstar.guodian.parse.PowerTipsDataHandler;
import com.dbstar.guodian.parse.SPCConstituteDataHandler;
import com.dbstar.guodian.parse.SetPowerTargetDataHandler;
import com.dbstar.guodian.parse.SmartHomeDataHandler;
import com.dbstar.guodian.parse.StepPowerConsumptionTrackDataHandler;
import com.dbstar.util.LogUtil;

public class ResponseHandler {
    
    private static final String TAG = "ResponseHandler";
    
    private HandlerThread mThread;
    
    private Handler mHandler;
    
    private static ResponseHandler mInstance;
    
    private ClientRequestService mService;
    
    private ResponseHandler(ClientRequestService service){
        
        this.mService = service;
        
        mThread = new HandlerThread("ResponseHandler", Process.THREAD_PRIORITY_BACKGROUND);
        mThread.start();
        
        mHandler = new Handler(mThread.getLooper()){
            public void handleMessage(android.os.Message msg) {
                String data = (String) msg.obj;
                
                String [] arr = CmdHelper.processResponse(data);
                if(arr == null){
                    mService.handErrorResponse(null);
                    return;
                }
                
                String id = arr[0];
                RequestTask task = mService.removeTask(id);

                if (task != null) {
                    task.ResponseData = arr;
                    processResponse(task);
                }
            };
        };
        
    }
    
    private void processResponse(RequestTask task) {
        LogUtil.i(TAG, " ==== do processResponse ==== " + task.TaskType);
        
        String contentType = task.ResponseData[5];
        
        if (contentType.equals("error")) {
            LogUtil.i(TAG, "========== error ==== " + task.ResponseData[7]);
            mService.handErrorResponse(task);
            return;
        }
        
        switch (task.TaskType) {
        case GDRequestType.DATATYPE_LOGIN: {
            LoginData loginData = LoginDataHandler.parse(task.ResponseData[7]);
            task.ParsedData = loginData;
            break;
        }

        case GDRequestType.DATATYPE_POWERPANELDATA: {
            PowerPanelData panelData = PanelDataHandler
                    .parse(task.ResponseData[7]);
            task.ParsedData = panelData;
            break;
        }

        case GDRequestType.DATATYPE_BILLMONTHLIST: {
            ArrayList<String> list = BillMonthListHandler
                    .parse(task.ResponseData[7]);
            task.ParsedData = list;
            break;
        }
        case GDRequestType.DATATYPE_BILLDETAILOFMONTH: {
            BillDetailData detail = BillDetailDataHandler
                    .parse(task.ResponseData[7]);
            task.ParsedData = detail;
            break;
        }

        case GDRequestType.DATATYPE_BILLDETAILOFRECENT: {
            BillDetailListData details = BillDetailOfRecentDataHandler
                    .parse(task.ResponseData[7]);
            task.ParsedData = details;
            break;
        }

        case GDRequestType.DATATYPE_NOTICES: {
            ArrayList<Notice> notices = NoticeDataHandler
                    .parse(task.ResponseData[7]);
            task.ParsedData = notices;
            break;
        }

        case GDRequestType.DATATYPE_BUSINESSAREA: {
            ArrayList<BusinessArea> business = BusinessAreaHandler
                    .parse(task.ResponseData[7]);
            task.ParsedData = business;
            break;
        }

        case GDRequestType.DATATYPE_USERAREAINFO: {
            AreaInfo areaInfo = AreaInfoHandler.parse(task.ResponseData[7]);
            task.ParsedData = areaInfo;
            break;
        }
        case GDRequestType.DATATYPE_CITYES:
            ArrayList<AreaInfo.Area> cityInfos= AreaInfoHandler.parseAreas(task.ResponseData[7]);
            task.ParsedData = cityInfos;
            break;
            
        case GDRequestType.DATATYPE_ZONES:
            ArrayList<AreaInfo.Area> zoneInfo= AreaInfoHandler.parseAreas(task.ResponseData[7]);
            task.ParsedData = zoneInfo;
            break;
        case GDRequestType.DATATYPE_ELECTRICAL_POWER_CONSUMPTION_CONSTITUTE:
            EPCConstitute dimension = EPCConstituteDataHandler.parse(task.ResponseData[7]);
            task.ParsedData = dimension;
            break;
        case GDRequestType.DATATYPE_PAYMENT_RECORDS:
            PaymentRecord paymentRecord = PaymentRecordsDataHandler.parase(task.ResponseData[7]);
            task.ParsedData = paymentRecord;
            break;
        case GDRequestType.DATATYPE_YREAR_FEE_DETAIL:
           Map<String, Record> yearDetail = PaymentRecordsDataHandler.paraseYearDetail(task.ResponseData[7]);
           task.ParsedData = yearDetail;
            break;
            
        case GDRequestType.DATATYPE_FAMILY_POWER_EFFICENCY:
            EPCConstitute dimension1 = FamilyPowerEfficencyDataHandler.parse(task.ResponseData[7]);
            task.ParsedData = dimension1;
            break;
        case GDRequestType.DATATYPE_STEP_POWER_CONSUMPTION_CONSTITUTE:
            SPCConstitute spcConstitute =  SPCConstituteDataHandler.parse(task.ResponseData[7]);
            task.ParsedData = spcConstitute;
            break;
        case GDRequestType.DATATYPE_PERIOD_POWER_CONSUMPTION_CONSTITUTE:
            PPCConstitute ppcConstitute = PPCConstituteDataHandler.parse(task.ResponseData[7]);
            task.ParsedData = ppcConstitute;
            break;
        case GDRequestType.DATATYPE_STEP_POWER_CONSUMPTION_TRACK:
            StepPowerConsumptionTrack track = StepPowerConsumptionTrackDataHandler.parse(task.ResponseData[7]);
            task.ParsedData = track;
            break;
        case GDRequestType.DATATYPE_EQUMENTLIST:
            List<RoomElectrical> elelist = SmartHomeDataHandler.parseRoomElectrical(task.ResponseData[7]);
            task.ParsedData = elelist;
            break;
        case GDRequestType.DATATYPE_POWER_CONSUMPTION_TREND:
            PowerConsumptionTrend trend = PowerConsumptionTrendDataHandler.parse(task.ResponseData[7]);
            task.ParsedData = trend;
            break;
            
        case GDRequestType.DATATYPE_POWER_TIPS:
            List<PowerTips>tips = PowerTipsDataHandler.parse(task.ResponseData[7]);
            task.ParsedData = tips;
            break;
            
        case GDRequestType.DATATYPE_ROOM_LIST:
            List<RoomData> rooms = SmartHomeDataHandler.parseRooms(task.ResponseData[7]);
            task.ParsedData = rooms;
            break;
        case GDRequestType.DATATYPE_ROOM_ELECTRICAL_LIST:
            List<RoomElectrical> eles = SmartHomeDataHandler.parseRoomElectrical(task.ResponseData[7]);
            task.ParsedData = eles;
            break;
        case GDRequestType.DATATYPE_TUNN_ON_OFF_ELECTRICAL:
            ElecTurnResponse turnResponse = SmartHomeDataHandler.parseEleTurnResponse(task.ResponseData[7]);
            task.ParsedData = turnResponse;
            break;
        case GDRequestType.DATATYPE_TUNN_ON_OFF_SMART_ELECTRICAL:
            task.ParsedData = SmartHomeDataHandler.parseExecuteModeResult(task.ResponseData[7]);
            break;
        case GDRequestType.DATATYPE_REFRESH_ELECTRICAL:
            ElecRefreshResponse refreshResponse = SmartHomeDataHandler.parseEleRefreshResponse(task.ResponseData[7]);
            task.ParsedData = refreshResponse;
            break;
            
        case GDRequestType.DATATYPE_MODEL_LIST:
            List<ElectricalOperationMode> modeList = SmartHomeDataHandler.parseEleOperModel(task.ResponseData[7]);
            task.ParsedData = modeList;
           break;
           
        case GDRequestType.DATATYPE_MODEL_ELECTRICAL_LIST:
            List<ModeElectrical> modeEleList = SmartHomeDataHandler.parseModeElectrical(task.ResponseData[7]);
            task.ParsedData = modeEleList;
            break;
        case GDRequestType.DATATYPE_EXECUTE_MODE:
            ResultData executeResult =  SmartHomeDataHandler.parseExecuteModeResult(task.ResponseData[7]);
            task.ParsedData = executeResult;
            break;
            
        case GDRequestType.DATATYPE_TIMED_TASK_LIST:
            List<TimedTask> tasks  = SmartHomeDataHandler.parseTimedTaskList(task.ResponseData[7]);
            task.ParsedData = tasks;
            break;
            
        case GDRequestType.DATATYPE_NO_TASK_ELCTRICAL_LIST:
            List<String> notaskEleList = SmartHomeDataHandler.parseNoTimedTaskElectricalList(task.ResponseData[7]);
            task.ParsedData = notaskEleList;
            break;
        case GDRequestType.DATATYPE_ADD_TIMED_TASK:
            AddTimedTaskResponse atr = SmartHomeDataHandler.parseAddTimedTaskResponse(task.ResponseData[7]);
            task.ParsedData = atr;
            break;
        case GDRequestType.DATATYPE_MODIFY_TIMED_TASK:
            ResultData resultData = SmartHomeDataHandler.parseModifyTimedTaskResponse(task.ResponseData[7]);
            task.ParsedData = resultData;
            break;
        case GDRequestType.DATATYPE_DELETE_TIMED_TASK:
            ResultData deleteRes = SmartHomeDataHandler.parseDeleteTimedTaskResponse(task.ResponseData[7]);
            task.ParsedData = deleteRes;
            break;
        case GDRequestType.DATATYPE_EXECUTE_TIMED_TASK:
            ResultData executeRes = SmartHomeDataHandler.parseExecuteTimedTaskResponse(task.ResponseData[7]);
            task.ParsedData = executeRes;
            break;
        case GDRequestType.DATATYPE_DEFAULT_POWER_TARGET:
            PowerData defaultTarget = SetPowerTargetDataHandler.parsePowerDefaultTarget(task.ResponseData[7]);
            task.ParsedData = defaultTarget;
            break;
        case GDRequestType.DATATYPE_POWER_TARGET:
            PowerTarget powerTarget = SetPowerTargetDataHandler.parsetPowerTarget(task.ResponseData[7]);
            task.ParsedData = powerTarget;
            break;
        case GDRequestType.DATATYPE_SETTING_POWER_TARGET:
            ResultData setTargetResult = SetPowerTargetDataHandler.parseSetPowerTargetResult(task.ResponseData[7]);
            task.ParsedData = setTargetResult;
            break;
        }
        
        mService.handResponseFinish(task);
        
    }



    public static synchronized ResponseHandler getInstance(ClientRequestService service){
        if(mInstance == null)
            mInstance  = new ResponseHandler(service);
        
        return mInstance;
    }
    
    
    public void handResponse(String data){
        Message message = mHandler.obtainMessage();
        message.obj = data;
        message.sendToTarget();
    }
    
    
}
