package com.dbstar.guodian.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dbstar.guodian.data.AddTimedTaskResponse;
import com.dbstar.guodian.data.AreaInfo;
import com.dbstar.guodian.data.BillDetailData;
import com.dbstar.guodian.data.BillDetailListData;
import com.dbstar.guodian.data.BusinessArea;
import com.dbstar.guodian.data.EPCConstitute;
import com.dbstar.guodian.data.ElectricalOperationMode;
import com.dbstar.guodian.data.EqumentData;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.guodian.data.Notice;
import com.dbstar.guodian.data.PPCConstitute;
import com.dbstar.guodian.data.PaymentRecord;
import com.dbstar.guodian.data.PowerConsumptionTrend;
import com.dbstar.guodian.data.PowerData;
import com.dbstar.guodian.data.PowerTarget;
import com.dbstar.guodian.data.PowerTips;
import com.dbstar.guodian.data.ResultData;
import com.dbstar.guodian.data.RoomData;
import com.dbstar.guodian.data.TimedTask;
import com.dbstar.guodian.data.RoomData.ElecRefreshResponse;
import com.dbstar.guodian.data.RoomData.ElecTurnResponse;
import com.dbstar.guodian.data.RoomData.RoomEletrical;
import com.dbstar.guodian.data.SPCConstitute;
import com.dbstar.guodian.data.ElectricalOperationMode.ModeElectrical;
import com.dbstar.guodian.data.PaymentRecord.Record;
import com.dbstar.guodian.data.PowerPanelData;
import com.dbstar.guodian.data.StepPowerConsumptionTrack;
import com.dbstar.guodian.parse.AreaInfoHandler;
import com.dbstar.guodian.parse.BillDetailDataHandler;
import com.dbstar.guodian.parse.BillDetailOfRecentDataHandler;
import com.dbstar.guodian.parse.BillMonthListHandler;
import com.dbstar.guodian.parse.BusinessAreaHandler;
import com.dbstar.guodian.parse.DataHandler;
import com.dbstar.guodian.parse.EPCConstituteDataHandler;
import com.dbstar.guodian.parse.EqumentDataHandler;
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
import com.dbstar.util.GDNetworkUtil;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.util.Log;

public class GDClient {
	private static final String TAG = "GDClient";

	public static final int MSG_REQUEST = 0x1001;
	public static final int MSG_RESPONSE = 0x1002;
	public static final int MSG_COMMAND = 0x1003;
	public static final int MSG_SOCKET_ERROR = 0x1004;

	// Command type
	public static final int CMD_CONNECT = 0x2001;
	public static final int CMD_STOP = 0x2002;

	// Request type
	public static final int REQUEST_LOGIN = 0x3001;
	public static final int REQUEST_POWERPANELDATA = 0x3002;
	public static final int REQUEST_BILLMONTHLIST = 0x3003;
	public static final int REQUEST_BILLDETAILOFMONTH = 0x3004;
	public static final int REQUEST_BILLDETAILOFRECENT = 0x3005;
	public static final int REQUEST_NOTICE = 0x3006;
	public static final int REQUEST_USERAREAINFO = 0x3007;
	public static final int REQUEST_BUSINESSAREA = 0x3008;
	public static final int REQUEST_CITYS = 0x3009;
	public static final int REQUEST_ZONES = 0x3010;
	public static final int REQUEST_ELECTRICAL_POWER_CONSUPTION_CONSTITUTE = 0x3011;
	public static final int REQUEST_PAYMENT_RECORDS = 0x3012;
	public static final int REQUEST_YEAR_FEE_DETAIL = 0x3013;
	public static final int REQUEST_FAMILY_POWER_EFFICENCY = 0x3014;
	public static final int REQUEST_STEP_POWER_CONSUPTION_CONSTITUTE = 0x3015;
	public static final int REQUEST_PERIOD_POWER_CONSUPTION_CONSTITUTE = 0x3016;
	public static final int REQUEST_STEP_POWER_CONSUMPTION_TRACK = 0x3017;
	public static final int REQUEST_EQUMENTLIST = 0x3018;
	public static final int REQUEST_POWER_CONSUMPTION_TREND = 0x3019;
	public static final int REQUEST_POWER_TIPS = 0x3020;
	public static final int REQUEST_ROOM_LIST = 0x3021;
	public static final int REQUEST_ROOM_ELECTRICAL_LIST = 0x3022;
	public static final int REQUEST_TURN_ON_OFF_ELECTRICAL = 0x3023;
	public static final int REQUEST_REFRESH_ELECTRICAL = 0x3024;
	public static final int REQUEST_MODEL_LIST = 0x3025;
	public static final int REQUEST_MODEL_ELECTRICAL_LIST = 0x3026;
	public static final int REQUEST_EXECUTE_MODE = 0x3027;
	public static final int REQUEST_TIMED_TASK_LIST= 0x3028;
	public static final int REQUEST_NO_TASK_ELECTRICAL_LIST= 0x3029;
	public static final int REQUEST_ADD_TIMED_TASK= 0x3030;
	public static final int REQUEST_MODIFY_TIMED_TASK= 0x3031;
	public static final int REQUEST_DELETE_TIMED_TASK= 0x3032;
	public static final int REQUEST_EXECUTE_TIMED_TASK= 0x3033;
	
	public static final int REQUEST_DEFAULT_POWER_TARGET= 0x3034;
	public static final int REQUEST_POWER_TARGET= 0x3035;
	public static final int REQUEST_SET_POWER_TARGET= 0x3036;
	// not include current month, just before;

	class Task {
		public int TaskType;
		public String TaskId;
		public String Command;
		public String[] ResponseData;
		public Object ParsedData;
	}

	private String mHostAddr = null;
	private int mHostPort = 0;
	private Socket mSocket = null;
	private BufferedReader mIn = null;
	private BufferedOutputStream mOut = null;
	private ReceiveThread mInThread;
	private HandlerThread mClientThread = null;
	private Handler mClientHandler = null;
	private Context mContext = null;

	private LinkedList<Task> mWaitingQueue = new LinkedList<Task>();
	private Handler mAppHander = null;

	private UncaughtExceptionHandler mExceptionHandler = new UncaughtExceptionHandler() {
		public void uncaughtException(Thread thread, Throwable ex) {

			Log.d(TAG, " ====== uncaughtException ======= " + ex.getMessage());
		}
	};

	public GDClient(Context context, Handler handler) {
		mContext = context;
		mAppHander = handler;

		mClientThread = new HandlerThread("GDClient",
				Process.THREAD_PRIORITY_BACKGROUND);

		mClientThread.start();

		mClientHandler = new Handler(mClientThread.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				int msgType = msg.what;
				switch (msgType) {
				case MSG_COMMAND: {
					performCommand(msg.arg1, msg.obj);
					break;
				}
				case MSG_REQUEST: {
					performRequest((Task) msg.obj);
					break;
				}
				case MSG_RESPONSE: {
					handleResponse((String) msg.obj);
					break;
				}
				case MSG_SOCKET_ERROR: {
					handleSocketError();
					break;
				}
				}
			}
		};

	}

	public void setHostAddress(String hostAddr, int port) {
		mHostAddr = hostAddr;
		mHostPort = port;
	}

	public void connectToServer() {
	    mClientHandler.removeMessages(MSG_COMMAND);
	    Log.i(TAG, "connectToServer + mClientHandler.removeMessages(CMD_RECONNECT)");
		Message msg = mClientHandler.obtainMessage(MSG_COMMAND);
		msg.arg1 = CMD_CONNECT;
		msg.sendToTarget();
	}

    Runnable ReconnectToServerTask = new Runnable() {
        public void run() {
            Log.i(TAG, " mClientHandler.postDelayed(new Runnable() {");
            Message msg = mClientHandler.obtainMessage(MSG_COMMAND);
            msg.arg1 = CMD_CONNECT;
            msg.sendToTarget();
        }
    };
	public void connectToServerDelayed(long delayMillis) {
	    Log.i(TAG, "connectToServerDelayed" + delayMillis);
	    mClientHandler.removeMessages(MSG_COMMAND);
		mClientHandler.postDelayed(ReconnectToServerTask, delayMillis);
	}
	
	public void login() {
		Task task = new Task();
		String taskId = GDCmdHelper.generateUID();
		String macAddr = GDNetworkUtil.getMacAddress(mContext, true);
		String cmdStr = GDCmdHelper.constructLoginCmd(taskId, macAddr);

		task.TaskType = REQUEST_LOGIN;
		task.TaskId = taskId;
		task.Command = cmdStr;

		Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
		msg.obj = task;
		msg.sendToTarget();
	}

	public void getPowerPanelData(String userId, String ctrlNoGuid,
			String userType) {

		String taskId = GDCmdHelper.generateUID();
		String cmdStr = GDCmdHelper.constructGetPowerPanelDataCmd(taskId,
				userId, ctrlNoGuid, userType);

		Task task = new Task();
		task.TaskType = REQUEST_POWERPANELDATA;
		task.TaskId = taskId;
		task.Command = cmdStr;

		Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
		msg.obj = task;
		mClientHandler.sendMessage(msg);
	}

	public void getBillMonthList(String userId, String ctrlNoGuid,
			String yearNum) {
		String taskId = GDCmdHelper.generateUID();
		String cmdStr = GDCmdHelper.constructGetBillMonthListCmd(taskId,
				userId, ctrlNoGuid, yearNum);

		Task task = new Task();
		task.TaskType = REQUEST_BILLMONTHLIST;
		task.TaskId = taskId;
		task.Command = cmdStr;

		Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
		msg.obj = task;
		mClientHandler.sendMessage(msg);
	}

	public void getBillDetailOfMonth(String userId, String ctrlNoGuid,
			String date) {
		String taskId = GDCmdHelper.generateUID();
		String cmdStr = GDCmdHelper.constructGetBillDetailOfMonthCmd(taskId,
				userId, ctrlNoGuid, date);

		Task task = new Task();
		task.TaskType = REQUEST_BILLDETAILOFMONTH;
		task.TaskId = taskId;
		task.Command = cmdStr;

		Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
		msg.obj = task;
		mClientHandler.sendMessage(msg);
	}

	public void getBillDetailOfRecent(String userId, String ctrlNoGuid,
			String dateNum) {
		String taskId = GDCmdHelper.generateUID();
		String cmdStr = GDCmdHelper.constructGetBillDetailOfRecentCmd(taskId,
				userId, ctrlNoGuid, dateNum);

		Task task = new Task();
		task.TaskType = REQUEST_BILLDETAILOFRECENT;
		task.TaskId = taskId;
		task.Command = cmdStr;

		Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
		msg.obj = task;
		mClientHandler.sendMessage(msg);
	}

	public void getNotices(String userId, String ctrlNoGuid) {
		String taskId = GDCmdHelper.generateUID();
		String cmdStr = GDCmdHelper.constructGetNoticeCmd(taskId, userId,
				ctrlNoGuid);

		Task task = new Task();
		task.TaskType = REQUEST_NOTICE;
		task.TaskId = taskId;
		task.Command = cmdStr;

		Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
		msg.obj = task;
		mClientHandler.sendMessage(msg);
	}

	public void getUserAreaInfo(String userId, String areaIdPath) {
		String taskId = GDCmdHelper.generateUID();
		String cmdStr = GDCmdHelper.constructGetUserAreaInfoCmd(taskId, userId,
				areaIdPath);

		Task task = new Task();
		task.TaskType = REQUEST_USERAREAINFO;
		task.TaskId = taskId;
		task.Command = cmdStr;

		Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
		msg.obj = task;
		mClientHandler.sendMessage(msg);
	}

	public void getBusinessArea(String userId, String areaId) {
		String taskId = GDCmdHelper.generateUID();
		String cmdStr = GDCmdHelper.constructGetBusinessAreaCmd(taskId, userId,
				areaId);

		Task task = new Task();
		task.TaskType = REQUEST_BUSINESSAREA;
		task.TaskId = taskId;
		task.Command = cmdStr;

		Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
		msg.obj = task;
		mClientHandler.sendMessage(msg);
	}

	public void getCitysArea(String userId, String pid) {
		String taskId = GDCmdHelper.generateUID();
		String cmdStr = GDCmdHelper.constructGetAreasCmd(taskId, userId, pid);
		Task task = new Task();
		task.TaskType = REQUEST_CITYS;
		task.TaskId = taskId;
		task.Command = cmdStr;

		Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
		msg.obj = task;
		mClientHandler.sendMessage(msg);
	}

	public void getZonesArea(String userId, String pid) {
		String taskId = GDCmdHelper.generateUID();
		String cmdStr = GDCmdHelper.constructGetAreasCmd(taskId, userId, pid);
		Task task = new Task();
		task.TaskType = REQUEST_ZONES;
		task.TaskId = taskId;
		task.Command = cmdStr;

		Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
		msg.obj = task;
		mClientHandler.sendMessage(msg);
	}

	public void getElecDimension(String userId, Map<String, String> params) {
		String taskId = GDCmdHelper.generateUID();
		String cmdStr = GDCmdHelper.constructGetElecDimensionCmd(userId, taskId, params);
	        Task task = new Task();
	        task.TaskType = REQUEST_ELECTRICAL_POWER_CONSUPTION_CONSTITUTE;
	        task.TaskId = taskId;
	        task.Command = cmdStr;

	        Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	        msg.obj = task;
	        mClientHandler.sendMessage(msg);
	    }
	   public void getPaymentRecords(String userId,Map<String, String> params){
           String taskId = GDCmdHelper.generateUID();
           String cmdStr = GDCmdHelper.constructPaymentRecordsCmd(userId,taskId,params);
           Task task = new Task();
           task.TaskType = REQUEST_PAYMENT_RECORDS;
           task.TaskId = taskId;
           task.Command = cmdStr;

           Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
           msg.obj = task;
           mClientHandler.sendMessage(msg);
       }
	   public void getYearFeeDetail(String userId,Map<String, String> params){
           String taskId = GDCmdHelper.generateUID();
           String cmdStr = GDCmdHelper.constructYearFeeDetailCmd(userId,taskId,params);
           Task task = new Task();
           task.TaskType = REQUEST_YEAR_FEE_DETAIL;
           task.TaskId = taskId;
           task.Command = cmdStr;

           Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
           msg.obj = task;
           mClientHandler.sendMessage(msg);
       }
	   
	   public void getFamilyPowerEfficency(String userId,Map<String, String> params){
           String taskId = GDCmdHelper.generateUID();
           String cmdStr = GDCmdHelper.constructFamilyPowerEffiCmd(userId,taskId,params);
           Task task = new Task();
           task.TaskType = REQUEST_FAMILY_POWER_EFFICENCY;
           task.TaskId = taskId;
           task.Command = cmdStr;

           Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
           msg.obj = task;
           mClientHandler.sendMessage(msg);
       }
	   public void getSPCConstitute(String userId,Map<String, String> params){
           String taskId = GDCmdHelper.generateUID();
           String cmdStr = GDCmdHelper.constructSPCConstituteCmd(userId,taskId,params);
           Task task = new Task();
           task.TaskType = REQUEST_STEP_POWER_CONSUPTION_CONSTITUTE;
           task.TaskId = taskId;
           task.Command = cmdStr;

           Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
           msg.obj = task;
           mClientHandler.sendMessage(msg);
       }
	   public void getPPCConstitute(String userId,Map<String, String> params){
           String taskId = GDCmdHelper.generateUID();
           String cmdStr = GDCmdHelper.constructPPCConstituteCmd(userId,taskId,params);
           Task task = new Task();
           task.TaskType = REQUEST_PERIOD_POWER_CONSUPTION_CONSTITUTE;
           task.TaskId = taskId;
           task.Command = cmdStr;

           Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
           msg.obj = task;
           mClientHandler.sendMessage(msg);
       }
	   public void getStepPowerTrack(String userId,Map<String, String> params){
           String taskId = GDCmdHelper.generateUID();
           String cmdStr = GDCmdHelper.constructStepPowerTrackCmd(userId,taskId,params);
           Task task = new Task();
           task.TaskType = REQUEST_STEP_POWER_CONSUMPTION_TRACK;
           task.TaskId = taskId;
           task.Command = cmdStr;

           Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
           msg.obj = task;
           mClientHandler.sendMessage(msg);
       }
	   public void getEqumentList(String userId,Map<String, String> params){
           String taskId = GDCmdHelper.generateUID();
           String cmdStr = GDCmdHelper.constructEqumentListCmd(userId,taskId,params);
           Task task = new Task();
           task.TaskType = REQUEST_EQUMENTLIST;
           task.TaskId = taskId;
           task.Command = cmdStr;

           Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
           msg.obj = task;
           mClientHandler.sendMessage(msg);
       }
	   public void getPowerConsumptionTrend(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructPowerTrendCmd(userId,taskId,params);
	       Task task = new Task();
	       task.TaskType = REQUEST_POWER_CONSUMPTION_TREND;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void getPowerTips(String userId){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructPowerTipsCmd(userId,taskId);
	       Task task = new Task();
	       task.TaskType = REQUEST_POWER_TIPS;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void getRoomList(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructRoomListCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_ROOM_LIST;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void getRoomElectricalList(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructRoomElectricalListCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_ROOM_ELECTRICAL_LIST;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void turnOnOrOffElectrical(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructTurnOnOrOffEleCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_TURN_ON_OFF_ELECTRICAL;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void refreshElectrical(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructRefreshElectricalCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_REFRESH_ELECTRICAL;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void getModelList(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructModelListCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_MODEL_LIST;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void getModelElectricalList(String userId,Map<String, String> params){
           String taskId = GDCmdHelper.generateUID();
           String cmdStr = GDCmdHelper.constructModelEleListCmd(userId, taskId, params);
           Task task = new Task();
           task.TaskType = REQUEST_MODEL_ELECTRICAL_LIST;
           task.TaskId = taskId;
           task.Command = cmdStr;
           
           Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
           msg.obj = task;
           mClientHandler.sendMessage(msg);
       }
	   public void executeMode(String userId,Map<String, String> params){
           String taskId = GDCmdHelper.generateUID();
           String cmdStr = GDCmdHelper.constructExecuteModeCmd(userId, taskId, params);
           Task task = new Task();
           task.TaskType = REQUEST_EXECUTE_MODE;
           task.TaskId = taskId;
           task.Command = cmdStr;
           
           Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
           msg.obj = task;
           mClientHandler.sendMessage(msg);
       }
	   public void getTimedTaskList(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructTimedTaskListCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_TIMED_TASK_LIST;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void getNoTaskElectricalList(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructNoTaskEleListCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_NO_TASK_ELECTRICAL_LIST;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void addTimeDTask(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructAddTimedTaskCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_ADD_TIMED_TASK;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void modifyTimeDTask(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructModifyTimedTaskCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_MODIFY_TIMED_TASK;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void executeTimeDTask(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructExecuteTimedTaskCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_EXECUTE_TIMED_TASK;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void getDefaultPowerTarget(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructDefaultPowerTargetCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_DEFAULT_POWER_TARGET;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void getPowerTarget(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructPowerTargetCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_POWER_TARGET;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void setPowerTarget(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructSetPowerTargetCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_SET_POWER_TARGET;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	   public void deleteTimeDTask(String userId,Map<String, String> params){
	       String taskId = GDCmdHelper.generateUID();
	       String cmdStr = GDCmdHelper.constructDeleteTimedTaskCmd(userId, taskId, params);
	       Task task = new Task();
	       task.TaskType = REQUEST_DELETE_TIMED_TASK;
	       task.TaskId = taskId;
	       task.Command = cmdStr;
	       
	       Message msg = mClientHandler.obtainMessage(MSG_REQUEST);
	       msg.obj = task;
	       mClientHandler.sendMessage(msg);
	   }
	public void stop() {
		Log.d(TAG, " ============ stop GDClient thread ============");
		Message msg = mClientHandler.obtainMessage(MSG_COMMAND);
		msg.arg1 = CMD_STOP;
		msg.sendToTarget();
	}

	public void destroy() {
		Log.d(TAG, " ============ destroy GDClient thread ============");
		mClientThread.quit();

		doStop();
	}

	// run in client thread
	private void performCommand(int cmdType, Object cmdData) {
		switch (cmdType) {
		case CMD_CONNECT: {
			doConnectToServer();
			break;
		}
		case CMD_STOP: {
			doStop();
			break;
		}
		}
	}

	private void performRequest(Task task) {
		doRequest(task);
	}

	private void handleResponse(String response) {
		// Log.d(TAG, " ++++++++++++handleResponse++++++++" + response);

		String[] data = GDCmdHelper.processResponse(response);

		if (data == null) {
			return;
		}

		String id = data[0];
		Task task = null;

		for (Task t : mWaitingQueue) {
			if (t.TaskId.equals(id)) {
				mWaitingQueue.remove(t);
				task = t;
				break;
			}
		}

		if (task != null) {
			task.ResponseData = data;
			processResponse(task);
		}

	}

	private void processResponse(Task task) {

		Log.d(TAG, " ++++++++++++processResponse++++++++" + task.TaskType);

		String contentType = task.ResponseData[5];

		// Log.d(TAG, "==========response content= " + contentType);

		if (contentType.equals("error")) {
			Log.d(TAG, "========== error ==== " + task.ResponseData[7]);
			handleRequestError(task);
			return;
		}

		switch (task.TaskType) {
		case REQUEST_LOGIN: {
			LoginData loginData = LoginDataHandler.parse(task.ResponseData[7]);
			task.ParsedData = loginData;
			break;
		}

		case REQUEST_POWERPANELDATA: {
			PowerPanelData panelData = PanelDataHandler
					.parse(task.ResponseData[7]);
			task.ParsedData = panelData;
			break;
		}

		case REQUEST_BILLMONTHLIST: {
			ArrayList<String> list = BillMonthListHandler
					.parse(task.ResponseData[7]);
			task.ParsedData = list;
			break;
		}
		case REQUEST_BILLDETAILOFMONTH: {
			BillDetailData detail = BillDetailDataHandler
					.parse(task.ResponseData[7]);
			task.ParsedData = detail;
			break;
		}

		case REQUEST_BILLDETAILOFRECENT: {
			BillDetailListData details = BillDetailOfRecentDataHandler
					.parse(task.ResponseData[7]);
			task.ParsedData = details;
			break;
		}

		case REQUEST_NOTICE: {
			ArrayList<Notice> notices = NoticeDataHandler
					.parse(task.ResponseData[7]);
			task.ParsedData = notices;
			break;
		}

		case REQUEST_BUSINESSAREA: {
			ArrayList<BusinessArea> business = BusinessAreaHandler
					.parse(task.ResponseData[7]);
			task.ParsedData = business;
			break;
		}

		case REQUEST_USERAREAINFO: {
			AreaInfo areaInfo = AreaInfoHandler.parse(task.ResponseData[7]);
			task.ParsedData = areaInfo;
			break;
		}
		case REQUEST_CITYS:
		    ArrayList<AreaInfo.Area> cityInfos= AreaInfoHandler.parseAreas(task.ResponseData[7]);
            task.ParsedData = cityInfos;
		    break;
		    
		case REQUEST_ZONES:
		    ArrayList<AreaInfo.Area> zoneInfo= AreaInfoHandler.parseAreas(task.ResponseData[7]);
            task.ParsedData = zoneInfo;
            break;
		case REQUEST_ELECTRICAL_POWER_CONSUPTION_CONSTITUTE:
		    EPCConstitute dimension = EPCConstituteDataHandler.parse(task.ResponseData[7]);
		    task.ParsedData = dimension;
		    break;
		case REQUEST_PAYMENT_RECORDS:
		    PaymentRecord paymentRecord = PaymentRecordsDataHandler.parase(task.ResponseData[7]);
		    task.ParsedData = paymentRecord;
		    break;
		case REQUEST_YEAR_FEE_DETAIL:
		   Map<String, Record> yearDetail = PaymentRecordsDataHandler.paraseYearDetail(task.ResponseData[7]);
		   task.ParsedData = yearDetail;
		    break;
		    
		case REQUEST_FAMILY_POWER_EFFICENCY:
		    EPCConstitute dimension1 = FamilyPowerEfficencyDataHandler.parse(task.ResponseData[7]);
            task.ParsedData = dimension1;
            break;
		case REQUEST_STEP_POWER_CONSUPTION_CONSTITUTE:
		    SPCConstitute spcConstitute =  SPCConstituteDataHandler.parse(task.ResponseData[7]);
		    task.ParsedData = spcConstitute;
		    break;
		case REQUEST_PERIOD_POWER_CONSUPTION_CONSTITUTE:
		    PPCConstitute ppcConstitute = PPCConstituteDataHandler.parse(task.ResponseData[7]);
		    task.ParsedData = ppcConstitute;
            break;
		case REQUEST_STEP_POWER_CONSUMPTION_TRACK:
		    StepPowerConsumptionTrack track = StepPowerConsumptionTrackDataHandler.parse(task.ResponseData[7]);
            task.ParsedData = track;
		    break;
		case REQUEST_EQUMENTLIST:
		    List<RoomEletrical> elelist = SmartHomeDataHandler.parseRoomElectrical(task.ResponseData[7]);
            task.ParsedData = elelist;
		    break;
		case REQUEST_POWER_CONSUMPTION_TREND:
		    PowerConsumptionTrend trend = PowerConsumptionTrendDataHandler.parse(task.ResponseData[7]);
		    task.ParsedData = trend;
		    break;
		    
		case REQUEST_POWER_TIPS:
		    List<PowerTips>tips = PowerTipsDataHandler.parse(task.ResponseData[7]);
		    task.ParsedData = tips;
		    break;
		    
		case REQUEST_ROOM_LIST:
		    List<RoomData> rooms = SmartHomeDataHandler.parseRooms(task.ResponseData[7]);
		    task.ParsedData = rooms;
		    break;
		case REQUEST_ROOM_ELECTRICAL_LIST:
            List<RoomEletrical> eles = SmartHomeDataHandler.parseRoomElectrical(task.ResponseData[7]);
            task.ParsedData = eles;
            break;
		case REQUEST_TURN_ON_OFF_ELECTRICAL:
		    ElecTurnResponse turnResponse = SmartHomeDataHandler.parseEleTurnResponse(task.ResponseData[7]);
		    task.ParsedData = turnResponse;
		    break;
		    
		case REQUEST_REFRESH_ELECTRICAL:
		    ElecRefreshResponse refreshResponse = SmartHomeDataHandler.parseEleRefreshResponse(task.ResponseData[7]);
		    task.ParsedData = refreshResponse;
		    break;
		    
		case REQUEST_MODEL_LIST:
		    List<ElectricalOperationMode> modeList = SmartHomeDataHandler.parseEleOperModel(task.ResponseData[7]);
		    task.ParsedData = modeList;
		   break;
		   
		case REQUEST_MODEL_ELECTRICAL_LIST:
		    List<ModeElectrical> modeEleList = SmartHomeDataHandler.parseModeElectrical(task.ResponseData[7]);
		    task.ParsedData = modeEleList;
		    break;
		case REQUEST_EXECUTE_MODE:
		    ResultData executeResult =  SmartHomeDataHandler.parseExecuteModeResult(task.ResponseData[7]);
		    task.ParsedData = executeResult;
		    break;
		    
		case REQUEST_TIMED_TASK_LIST:
		    List<TimedTask> tasks  = SmartHomeDataHandler.parseTimedTaskList(task.ResponseData[7]);
		    task.ParsedData = tasks;
		    break;
		    
		case REQUEST_NO_TASK_ELECTRICAL_LIST:
		    List<String> notaskEleList = SmartHomeDataHandler.parseNoTimedTaskElectricalList(task.ResponseData[7]);
		    task.ParsedData = notaskEleList;
		    break;
		case REQUEST_ADD_TIMED_TASK:
		    AddTimedTaskResponse atr = SmartHomeDataHandler.parseAddTimedTaskResponse(task.ResponseData[7]);
		    task.ParsedData = atr;
		    break;
		case REQUEST_MODIFY_TIMED_TASK:
		    ResultData resultData = SmartHomeDataHandler.parseModifyTimedTaskResponse(task.ResponseData[7]);
		    task.ParsedData = resultData;
		    break;
		case REQUEST_DELETE_TIMED_TASK:
            ResultData deleteRes = SmartHomeDataHandler.parseDeleteTimedTaskResponse(task.ResponseData[7]);
            task.ParsedData = deleteRes;
            break;
		case REQUEST_EXECUTE_TIMED_TASK:
            ResultData executeRes = SmartHomeDataHandler.parseExecuteTimedTaskResponse(task.ResponseData[7]);
            task.ParsedData = executeRes;
            break;
		case REQUEST_DEFAULT_POWER_TARGET:
		    PowerData defaultTarget = SetPowerTargetDataHandler.parsePowerDefaultTarget(task.ResponseData[7]);
            task.ParsedData = defaultTarget;
            break;
		case REQUEST_POWER_TARGET:
		    PowerTarget powerTarget = SetPowerTargetDataHandler.parsetPowerTarget(task.ResponseData[7]);
            task.ParsedData = powerTarget;
            break;
		case REQUEST_SET_POWER_TARGET:
		    ResultData setTargetResult = SetPowerTargetDataHandler.parseSetPowerTargetResult(task.ResponseData[7]);
            task.ParsedData = setTargetResult;
            break;
		}

		if (mAppHander != null) {
			Message msg = mAppHander
					.obtainMessage(GDEngine.MSG_REQUEST_FINISHED);
			msg.obj = task;
			msg.sendToTarget();
		}
	}

	private int getErrorCode(String errorStr) {
		int errorCode = GDConstract.ErrorCodeUnKnown;
		if (errorStr.equals(GDConstract.ErrorStrRepeatLogin)) {
			errorCode = GDConstract.ErrorCodeRepeatLogin;
		}

		return errorCode;
	}
	
	private void handleRequestError(Task task) {
		int error = getErrorCode(task.ResponseData[7]);
		
		if (mAppHander != null) {
			Message msg = mAppHander
					.obtainMessage(GDEngine.MSG_REQUEST_ERROR);
			msg.arg1 = error;
			msg.obj = task;
			msg.sendToTarget();
		}
	}

	private void handleSocketError() {
		Log.d(TAG, " === handleSocketError ===");

		doStop();

		if (mAppHander != null) {
			mAppHander.sendEmptyMessage(GDEngine.MSG_SOCKET_ERROR);
		}
	}

	private void doConnectToServer() {
		try {
			Log.d(TAG, " ====== doConnectToServer ===");

			if (mSocket != null) {
				if (mSocket.isConnected()) {
					// socket has already been connected.
					Log.d(TAG, " == socket already connected == ");
					mAppHander.sendEmptyMessage(GDEngine.MSG_CONNECT_ALREADY);
					return;
				} else {
					if (!mSocket.isClosed()) {
						Log.d(TAG, " == close socket == ");
						mSocket.close();
					}
				}

				mSocket = null;
			}

			Log.d(TAG, " server ip = " + mHostAddr + " port=" + mHostPort);

			mSocket = new Socket(mHostAddr, mHostPort);
			mSocket.setKeepAlive(true);

			mIn = new BufferedReader(new InputStreamReader(
					mSocket.getInputStream(), "UTF-8"));

			Log.d(TAG, " ==== input is shutdwon= " + mSocket.isInputShutdown());

			mOut = new BufferedOutputStream(new DataOutputStream(
					mSocket.getOutputStream()));

			Log.d(TAG,
					" ==== output is shutdown= " + mSocket.isOutputShutdown());

			mInThread = new ReceiveThread(mSocket, mIn, mClientHandler);
			mInThread.start();

			Log.d(TAG, " ====== socket connected =" + mSocket.isConnected());

			if (mSocket.isConnected()) {
				mAppHander.sendEmptyMessage(GDEngine.MSG_CONNECTED);
			}
		} catch (IOException e) {
			e.printStackTrace();

			doStop();

			mAppHander.sendEmptyMessage(GDEngine.MSG_CONNECT_FAILED);
			return;
		}
	}

	private boolean isOutputAvailable() {
		if (mSocket == null)
			return false;

		Log.d(TAG, "isOutputShutdown " + mSocket.isOutputShutdown());

		return mSocket.isConnected() && !mSocket.isClosed();
	}

	private void doRequest(Task task) {

		Log.d(TAG, "======= doRequest =========");
		Log.d(TAG, "task type" + task.TaskType);
		Log.d(TAG, "task cmd" + task.Command);

		if (!isOutputAvailable()) {
			Log.d(TAG, "======= no connection to server =========");
			return;
		}

		mWaitingQueue.add(task);

		try {
			mOut.write(task.Command.getBytes());
			mOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// stop receive thread,
	// close socket.
	private void doStop() {
		Log.d(TAG, " ============ doStop ============");

		if (mInThread != null) {
			mInThread.setExit();
			mInThread = null;
		}

		Log.d(TAG, " ============ stop 1 ============");

		try {
			if (mSocket != null
					&& (mSocket.isConnected() || !mSocket.isClosed())) {
				if (!mSocket.isInputShutdown()) {
					mSocket.shutdownInput();

					Log.d(TAG, "Shut down input.");
				}

				if (!mSocket.isOutputShutdown()) {
					mSocket.shutdownOutput();

					Log.d(TAG, "Shut down output.");
				}

				mSocket.close();

				Log.d(TAG, "Close socket.");
			}

			mSocket = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (mSocket != null && !mSocket.isClosed()) {
					mSocket.close();
				}
			} catch (Exception e) {

			}

			mSocket = null;
			mWaitingQueue.clear();

			Log.d(TAG, " ============ stop 2 ============");
		}
	}
	
	public boolean isSocketConnected(){
	    if(mSocket != null && mSocket.isConnected())
	        return true;
	    else 
	        return false;
	}
}
