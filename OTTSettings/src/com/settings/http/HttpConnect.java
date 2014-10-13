package com.settings.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.settings.utils.LogUtil;

import android.util.Log;

public class HttpConnect {
    
    private HttpClient customerHttpClient;
    private static final String CHARSET = HTTP.UTF_8;
    public static final int POST = 0;
    public static final int GET = 1;

    private HttpConnect() {
        initClient();
    }
    
    public static class HttpConnectInstance{
        private static HttpConnect mHttpConnect = new HttpConnect();
        public static HttpConnect instance(){
           return mHttpConnect; 
        }
    }
    
    private void initClient(){

        HttpParams params = new BasicHttpParams();
        // 设置一些基本参数
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
//        HttpProtocolParams
//                .setUserAgent(
//                        params,
//                        "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
//                                + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
        // 超时设置
        /* 从连接池中取连接的超时时间 */
        ConnManagerParams.setTimeout(params, 60000);
        // 设置最大连接数
        ConnManagerParams.setMaxTotalConnections(params, 800); 

        /* 连接超时 */
        HttpConnectionParams.setConnectionTimeout(params, 60000);
        /* 请求超时 */
        HttpConnectionParams.setSoTimeout(params, 60000);

        // 设置我们的HttpClient支持HTTP和HTTPS两种模式
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        // 使用线程安全的连接管理来创建HttpClient
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
        customerHttpClient = new DefaultHttpClient(conMgr, params);
    }
    
    private HttpClient getHttpClient() {
        if (null == customerHttpClient) {
            initClient();
        }
        return customerHttpClient;
    }

    public HttpEntity openConnect(int type, String uri, List<NameValuePair> params) {
    	
        if (null == uri) {
            throw new IllegalStateException("uri is null");
        }
        
        HttpEntity entity = null;
        HttpResponse response = null;
        
        switch (type) {
        case POST:
            try {
                HttpPost post = new HttpPost(uri);
                if (params != null && params.size() != 0) {
                    UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(params, CHARSET);
                    post.setEntity(paramsEntity);
                }
                response = getHttpClient().execute(post);
                if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                	entity = response.getEntity();
                }
                LogUtil.d("HttpConnect openConnect", "post：：联网成功");
            } catch (UnsupportedEncodingException e) {
                LogUtil.w("HttpConnect openConnect", "post：：UnsupportedEncodingException异常");
                e.printStackTrace();
            } catch (ClientProtocolException e) {
            	LogUtil.w("HttpConnect openConnect", "post：：ClientProtocolException异常");
            	e.printStackTrace();
            } catch (ConnectionPoolTimeoutException e) {
            	LogUtil.w("HttpConnect openConnect", "post：：ConnectionPoolTimeoutException连接异常" + uri);
            	e.printStackTrace();
			} catch (IOException e) {
            	LogUtil.w("HttpConnect openConnect", "post：：IOException异常");
            	e.printStackTrace();
            }
            break;
        case GET:
			try {
				if (params != null && params.size() != 0) {
					String paramsTemp = URLEncodedUtils.format(params, CHARSET);
					StringBuilder sb = new StringBuilder();
					sb.append(uri);
					sb.append("?");
					sb.append(paramsTemp);
					uri = sb.toString();
				}
				Log.e("HttpConnect", "GET  uri: " + uri);
				HttpGet get = new HttpGet(uri);
				response = getHttpClient().execute(get);
				if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					entity = response.getEntity();
				}
				LogUtil.d("HttpConnect openConnect", "get：：联网成功");
			} catch (ClientProtocolException e) {
				LogUtil.w("HttpConnect openConnect", "get：：ClientProtocolException异常");
				e.printStackTrace();
			} catch (ConnectionPoolTimeoutException e) {
				LogUtil.w("HttpConnect openConnect", "get：：ConnectionPoolTimeoutException连接异常" + uri);
				e.printStackTrace();
			} catch (IOException e) {
				LogUtil.w("HttpConnect openConnect", "get：：IOException异常");
				e.printStackTrace();
			}
            break;
        default:
            break;
        }
        
        
        return entity;
    }
}

