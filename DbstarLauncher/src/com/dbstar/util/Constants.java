package com.dbstar.util;

public class Constants {

	public static final String Server_Url = "http://1.202.248.179:8080/";
	
	// 登录，注册，获取验证码、心跳接口等的url前面的一部分
	public static final String Server_Url_AAA = "http://1.202.248.179:8080/OttSys/OttService/";
	
	// 获取海报、推荐位、应用信息接口等的url前面的一部分
	public static final String Server_Url_EPG = "http://1.202.248.179:8080/EPGPlus/OttService/";
	
	// 登录url
	public static final String Server_Url_Login = Server_Url_AAA + "Login?";
	
	// 注册url
	public static final String Server_Url_Register = Server_Url_AAA + "Open?";
	
	// 获取验证码的url
	public static final String Server_Url_Identify = Server_Url_AAA + "IdentifyCODE?";
	
	// 心跳接口的url
	public static final String Server_Url_Heartbeat = Server_Url_AAA + "Heartbeat?";
	
	// 获得海报的url
	public static final String Server_Url_QueryPoster = Server_Url_EPG + "QueryPoster?";
	
	// 获得推荐位的url
	public static final String Server_Url_QueryRecommand = Server_Url_EPG + "QueryRecommand?";
	
	// 获得海报、推荐位图片和应用商城里面应用、图片的url
	public static final String Server_Url_Image = Server_Url + "images";
	
	// 应用商城的url
	public static final String Server_Url_QueryApp = Server_Url_EPG + "QueryApp";
	
}
