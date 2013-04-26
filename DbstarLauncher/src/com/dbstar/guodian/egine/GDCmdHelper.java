package com.dbstar.guodian.egine;

import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONStringer;

import com.dbstar.guodian.data.JsonTag;
import com.smartlife.mobile.service.FormatCMD;

import android.os.SystemClock;
import android.util.Log;

public class GDCmdHelper {

	private static final String TAG = "GDCmdHelper";
	private static final String CmdStartTag = "#!";
	private static final String CmdEndTag = "!#";
	private static final String CmdDelimiterTag = "#";

	private static final String DeviceVersion = "v3.3.5";
	private static final String DeviceId = "epg_htcm";

	private static String toJson(String key, String value) {
		String jsonStr = "";
		try {
			jsonStr = new JSONStringer().object()
					.key(key).value(value)
					.endObject().toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}
	
	private static String toJson(String[] keys, String[] values) {
		String jsonStr = null;
		try {
			JSONStringer jsonStringer = new JSONStringer();
			jsonStringer.object();
			int count = keys.length;
			for(int i=0; i<count ; i++) {
				jsonStringer.key(keys[i]).value(values[i]);
			}
			jsonStringer.endObject();
			jsonStr = jsonStringer.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}
	
	public static String generateUID() {
		String uid = null;
		long currentTime = SystemClock.currentThreadTimeMillis();
		Random random = new Random(currentTime);
		long randomValue = random.nextLong();
		uid = String.valueOf(currentTime) + String.valueOf(randomValue);
		return uid;
	}

	public static String constructLoginCmd(String cmdId, String macaddr) {
		String cmdStr = cmdId + CmdDelimiterTag
				+ "aut"     + CmdDelimiterTag
				+ "m008f001" + CmdDelimiterTag
				+ macaddr    + CmdDelimiterTag
				+ DeviceVersion + CmdDelimiterTag
				+ DeviceId   + CmdDelimiterTag
				+ toJson("macaddr", macaddr);
		
		Log.d(TAG, "cmd data = " + cmdStr);
		
		String encryptStr = FormatCMD.encryptCMD(cmdStr);
		cmdStr = CmdStartTag + encryptStr + CmdEndTag+"\n";
//		Log.d(TAG, " cmd ===== " + cmdStr);
		return cmdStr;
	}
	
	public static String constructGetPowerPanelDataCmd(String cmdId, String userId,
			String ctrlNoGuid, String userType) {
		String[] keys = new String[2];
		String[] values = new String[2];
		keys[0]=JsonTag.TAGNumCCGuid;
		keys[1]="user_type";
		values[0]=ctrlNoGuid;
		values[1]= userType;
		
		String cmdStr = cmdId + CmdDelimiterTag
				+ "elc"     + CmdDelimiterTag
				+ "m008f001" + CmdDelimiterTag
				+ userId    + CmdDelimiterTag
				+ DeviceVersion + CmdDelimiterTag
				+ DeviceId   + CmdDelimiterTag
				+ toJson(keys, values);
		
		Log.d(TAG, "cmd data = " + cmdStr);
		
		String encryptStr = FormatCMD.encryptCMD(cmdStr);
		cmdStr = CmdStartTag + encryptStr + CmdEndTag+"\n";
		
//		Log.d(TAG, " cmd ===== " + cmdStr);
		
		return cmdStr;
	}
	
	public static String constructGetBillMonthListCmd(String cmdId, String userId, String ctrlNoGuid, String yearNum) {
		String[] keys = new String[2];
		String[] values = new String[2];
		keys[0]=JsonTag.TAGNumCCGuid;
		keys[1]="num_years";
		values[0]=ctrlNoGuid;
		values[1]= yearNum;
		
		String cmdStr = cmdId + CmdDelimiterTag
				+ "elc"     + CmdDelimiterTag
				+ "m005f007" + CmdDelimiterTag
				+ userId    + CmdDelimiterTag
				+ DeviceVersion + CmdDelimiterTag
				+ DeviceId   + CmdDelimiterTag
				+ toJson(keys, values);
		
		Log.d(TAG, "cmd data = " + cmdStr);
		
		String encryptStr = FormatCMD.encryptCMD(cmdStr);
		cmdStr = CmdStartTag + encryptStr + CmdEndTag+"\n";
		
//		Log.d(TAG, " cmd ===== " + cmdStr);
		
		return cmdStr;
	}
	
	public static String constructGetBillDetailOfMonthCmd(String cmdId, String userId, String ctrlNoGuid, String date) {
		String[] keys = new String[2];
		String[] values = new String[2];
		keys[0]=JsonTag.TAGNumCCGuid;
		keys[1]="date";
		values[0]=ctrlNoGuid;
		values[1]= date;
		
		String cmdStr = cmdId + CmdDelimiterTag
				+ "elc"     + CmdDelimiterTag
				+ "m005f005" + CmdDelimiterTag
				+ userId    + CmdDelimiterTag
				+ DeviceVersion + CmdDelimiterTag
				+ DeviceId   + CmdDelimiterTag
				+ toJson(keys, values);
		
		Log.d(TAG, "cmd data = " + cmdStr);
		
		String encryptStr = FormatCMD.encryptCMD(cmdStr);
		cmdStr = CmdStartTag + encryptStr + CmdEndTag+"\n";
		
//		Log.d(TAG, " cmd ===== " + cmdStr);
		
		return cmdStr;
	}
	
	// dateNum: number of month to query.
	public static String constructGetBillOfRecentCmd(String cmdId, String userId, String ctrlNoGuid, String dateNum) {
		String[] keys = new String[2];
		String[] values = new String[2];
		keys[0]=JsonTag.TAGNumCCGuid;
		keys[1]="num_month";
		values[0]= ctrlNoGuid;
		values[1]= dateNum;
		
		String cmdStr = cmdId + CmdDelimiterTag
				+ "elc"     + CmdDelimiterTag
				+ "m005f004" + CmdDelimiterTag
				+ userId    + CmdDelimiterTag
				+ DeviceVersion + CmdDelimiterTag
				+ DeviceId   + CmdDelimiterTag
				+ toJson(keys, values);
		
		Log.d(TAG, "cmd data = " + cmdStr);
		
		String encryptStr = FormatCMD.encryptCMD(cmdStr);
		cmdStr = CmdStartTag + encryptStr + CmdEndTag+"\n";
		
//		Log.d(TAG, " cmd ===== " + cmdStr);
		
		return cmdStr;
	}
	
	// dateNum: number of month to query.
	public static String constructGetBillDetailOfRecentCmd(String cmdId,
			String userId, String ctrlNoGuid, String dateNum) {
		String[] keys = new String[2];
		String[] values = new String[2];
		keys[0] = JsonTag.TAGNumCCGuid;
		keys[1] = "num_month";
		values[0] = ctrlNoGuid;
		values[1] = dateNum;
			
		String cmdStr = cmdId + CmdDelimiterTag
				+ "elc"     + CmdDelimiterTag
				+ "m005f008" + CmdDelimiterTag
				+ userId    + CmdDelimiterTag
				+ DeviceVersion + CmdDelimiterTag
				+ DeviceId   + CmdDelimiterTag
				+ toJson(keys, values);

		Log.d(TAG, "cmd data = " + cmdStr);
		
		String encryptStr = FormatCMD.encryptCMD(cmdStr);
		cmdStr = CmdStartTag + encryptStr + CmdEndTag + "\n";

//		Log.d(TAG, " cmd ===== " + cmdStr);

		return cmdStr;
	}
	
	public static String constructGetNoticeCmd(String cmdId,
			String userId, String ctrlNoGuid) {
			
		String cmdStr = cmdId + CmdDelimiterTag
				+ "elc"     + CmdDelimiterTag
				+ "m007f001" + CmdDelimiterTag
				+ userId    + CmdDelimiterTag
				+ DeviceVersion + CmdDelimiterTag
				+ DeviceId   + CmdDelimiterTag
				+ toJson(JsonTag.TAGNumCCGuid, ctrlNoGuid);

		Log.d(TAG, "cmd data = " + cmdStr);
		
		String encryptStr = FormatCMD.encryptCMD(cmdStr);
		cmdStr = CmdStartTag + encryptStr + CmdEndTag + "\n";

//		Log.d(TAG, " cmd ===== " + cmdStr);

		return cmdStr;
	}
	
	public static String constructGetUserAreaInfoCmd(String cmdId,
			String userId, String areaIdPath) {
			
		String cmdStr = cmdId + CmdDelimiterTag
				+ "elc"     + CmdDelimiterTag
				+ "m007f005" + CmdDelimiterTag
				+ userId    + CmdDelimiterTag
				+ DeviceVersion + CmdDelimiterTag
				+ DeviceId   + CmdDelimiterTag
				+ toJson("areaidPath", areaIdPath);

		Log.d(TAG, "cmd data = " + cmdStr);
		
		String encryptStr = FormatCMD.encryptCMD(cmdStr);
		cmdStr = CmdStartTag + encryptStr + CmdEndTag + "\n";

//		Log.d(TAG, " cmd ===== " + cmdStr);

		return cmdStr;
	}
	
	public static String constructGetBusinessAreaCmd(String cmdId,
			String userId, String areaId) {
			
		String cmdStr = cmdId + CmdDelimiterTag
				+ "elc"     + CmdDelimiterTag
				+ "m007f002" + CmdDelimiterTag
				+ userId    + CmdDelimiterTag
				+ DeviceVersion + CmdDelimiterTag
				+ DeviceId   + CmdDelimiterTag
				+ toJson("num_area_id", areaId);

		Log.d(TAG, "cmd data = " + cmdStr);
		
		String encryptStr = FormatCMD.encryptCMD(cmdStr);
		cmdStr = CmdStartTag + encryptStr + CmdEndTag + "\n";

//		Log.d(TAG, " cmd ===== " + cmdStr);

		return cmdStr;
	}
	public static String constructGetAreasCmd(String cmdId,
            String userId, String pid) {
            
        String cmdStr = cmdId + CmdDelimiterTag
                + "aut"     + CmdDelimiterTag
                + "m002f001" + CmdDelimiterTag
                + userId    + CmdDelimiterTag
                + DeviceVersion + CmdDelimiterTag
                + DeviceId   + CmdDelimiterTag
                + pid;

        Log.d(TAG, "cmd data = " + cmdStr);
        
        String encryptStr = FormatCMD.encryptCMD(cmdStr);
        cmdStr = CmdStartTag + encryptStr + CmdEndTag + "\n";

//      Log.d(TAG, " cmd ===== " + cmdStr);

        return cmdStr;
    }
	public static String constructGetElecDimensionCmd( String userId,String cmdId,
	        Map<String, String> params) {
	    String keys [] = params.keySet().toArray(new String[]{});
	    String values [] = params.values().toArray(new String[]{});
        String cmdStr = cmdId + CmdDelimiterTag
                + "elc"     + CmdDelimiterTag
                + "m008f007" + CmdDelimiterTag
                + userId    + CmdDelimiterTag
                + DeviceVersion + CmdDelimiterTag
                + DeviceId   + CmdDelimiterTag
                + toJson(keys, values);

        Log.d(TAG, "cmd data = " + cmdStr);
        
        String encryptStr = FormatCMD.encryptCMD(cmdStr);
        cmdStr = CmdStartTag + encryptStr + CmdEndTag + "\n";

//      Log.d(TAG, " cmd ===== " + cmdStr);

        return cmdStr;
    }
	public static String[] processResponse(String response) {
		String data = response;
//		Log.d(TAG, "receive data = " + data);
		
		if (!isValideCommand(data))
			return null;
		
		String cmd = data.substring(CmdStartTag.length(), data.length() - CmdEndTag.length());
		String decryptedStr = FormatCMD.decryptCMD(cmd);
		
		Log.d(TAG, "decrypt data = " + decryptedStr);
		
		return decryptedStr.split(CmdDelimiterTag);
	}
	
	private static boolean isValideCommand(String cmd) {
		boolean valid = false;
		int cmdLength = cmd.length();
		int startTagLen = CmdStartTag.length();
		int endTagLen = CmdEndTag.length();
		if (cmdLength > (startTagLen + endTagLen)) {
			String startTag = cmd.substring(0, startTagLen);
			String endTag = cmd.substring(cmd.length() - endTagLen);
			valid = CmdStartTag.equals(startTag) && CmdEndTag.equals(endTag);
		}
		
		return valid;
	}

}
