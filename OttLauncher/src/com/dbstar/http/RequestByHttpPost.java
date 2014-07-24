package com.dbstar.http;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import com.dbstar.util.LogUtil;

public class RequestByHttpPost {
	public static String TIME_OUT = "操作超时";

	public static int doPost(List<BasicNameValuePair> params, String url) {
//		String result = null;
		int responseCode = -1;
		HttpPost httpPost = new HttpPost(url);
		try {
			// 设置字符集
			HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			// 设置参数实体
			httpPost.setEntity(entity);
			// 获取HttpClient对象
			HttpClient httpClient = new DefaultHttpClient();
			// 连接超时
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			// 请求超时
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

			// 获取HttpResponse实例
			HttpResponse httpResponse = httpClient.execute(httpPost);
			responseCode = httpResponse.getStatusLine().getStatusCode();
//			// 判断是否请求成功
//			if (httpResponse.getStatusLine().getStatusCode() == 200) {
//				// 获取返回的数据
//				result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//				LogUtil.i("HttpPost::result", "HttpPost方式请求成功，返回数据：" + result);
//			} else {
//				LogUtil.i("HttpPost", "HttpPost方式请求失败");
//			}

		} catch (Exception e) {
			LogUtil.w("HttpPost", "出现异常" + e);
		}

		return responseCode;
	}
}
