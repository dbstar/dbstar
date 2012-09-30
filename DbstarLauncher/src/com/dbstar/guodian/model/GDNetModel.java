package com.dbstar.guodian.model;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.util.Log;

public class GDNetModel {
	private static final String TAG = "GDNetModel";

	// Guodian SOAP API
	//private static final String ServiceURL = "http://211.99.30.254:8081/dataanalysis/analysis-ws/PowerEasyCal?wsdl";
	private static final String PackageName = "com.fibrlink.da.ws";
	private static final String ClassName = "PowerEasyCalPort";
	private static final String MethodGetPowerConsumption = "getPowerConsumption";
	private static final String MethodGetTotalCostByChargeType = "getTotalCostByChargeType1";
	private static final String ParameterCCID = "in0";
	private static final String ParameterDateStart = "in1";
	private static final String ParameterDateEnd = "in2";
	private static final String ParameterChargeTyep = "in3";

	private static final String WebPowerUrl = "http://211.99.30.254:8081";
	private static final String ServiceURL = WebPowerUrl + "/dataanalysis/analysis-ws/PowerEasyCal";
	private static final String ServiceNameSpace = "http://ws.da.fibrlink.com";

	private DefaultHttpClient mHttpClient = null;

	public GDNetModel(Context context) {

	}

	public void initialize() {
		mHttpClient = new DefaultHttpClient();
	}

	public String getPowerConsumption(String cc_id, String date_start,
			String date_end) {

		Log.d(TAG, "getPowerConsumption: cc_id=" + cc_id + " date_start="
				+ date_start + " date_end=" + date_end);

		SoapObject request = new SoapObject(ServiceNameSpace,
				MethodGetPowerConsumption);
		request.addProperty(ParameterCCID, cc_id);
		request.addProperty(ParameterDateStart, date_start);
		request.addProperty(ParameterDateEnd, date_end);
		Log.d(TAG, " "+request.toString());

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = request;
		envelope.dotNet = true;
		//envelope.setOutputSoapObject(request);

		(new MarshalBase64()).register(envelope);

		HttpTransportSE transport = new HttpTransportSE(ServiceURL);		
		transport.debug = true;
		
		try {
			String soapAction = "";
			transport.call(soapAction, envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String value = "";

		try {
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			if (response != null) {
				value = response.toString();
				Log.d(TAG, "response = " + value);
			}
		} catch (SoapFault e) {
			e.printStackTrace();
		}

		return value;
	}

	public String getTotalCostByChargeType(String cc_id, String date_start,
			String date_end, String charge_type) {

		Log.d(TAG, "getTotalCostByChargeType: cc_id=" + cc_id + " date_start="
				+ date_start + " date_end=" + date_end + " charge_type"
				+ charge_type);

		SoapObject request = new SoapObject(ServiceNameSpace,
				MethodGetTotalCostByChargeType);
		request.addProperty(ParameterCCID, cc_id);
		request.addProperty(ParameterDateStart, date_start);
		request.addProperty(ParameterDateEnd, date_end);
		request.addProperty(ParameterChargeTyep, charge_type);

		Log.d(TAG, " "+request.toString());
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = request;
		envelope.dotNet = true;
		//envelope.setOutputSoapObject(request);

		(new MarshalBase64()).register(envelope);

		HttpTransportSE transport = new HttpTransportSE(ServiceURL);
		transport.debug = true;
		try {
			String soapAction = "";
			transport.call(soapAction, envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String value = "";

		try {
			SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
			if (result != null) {
				value = result.toString();
				Log.d(TAG, "response = " + value);
			}
		} catch (SoapFault e) {
			e.printStackTrace();
		}

		return value;
	}

	private String query(String uri) {
		String value = "";
		HttpResponse response = null;
		InputStream in = null;

		Log.d(TAG, "requst " + uri);
		HttpGet httpGet = new HttpGet(uri);

		try {
			response = mHttpClient.execute(httpGet);

			if (response != null
					&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				in = entity.getContent();

				if (in != null) {
					value = readString(in);
					in.close();
				}
			} else {
				httpGet.abort();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d(TAG, "value = " + value);

		return value;
	}

	private String readString(InputStream in) {
		byte[] data = null;
		BufferedInputStream bIn = new BufferedInputStream(in);
		data = readData(bIn);

		String str = null;
		if (data != null) {
			str = new String(data);
		}
		return str;
	}

	private byte[] readData(InputStream in) {
		byte[] data = null;

		byte[] buffer = new byte[1024 * 8];

		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

		try {
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				byteBuffer.write(buffer, 0, len);
			}

			data = byteBuffer.toByteArray();
			byteBuffer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}
}
