package com.settings.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	public static String getMD5(String value){
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
//		md5.update(value.getBytes());
//		byte[] mByte = md5.digest();
		char[] charArray = value.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		
		byte[] mByte = md5.digest(byteArray);
		
		return getString(mByte);
	}

	private static String getString(byte[] mByte) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < mByte.length; i++) {
			int value = ((int)mByte[i]) & 0xff;
			if (value < 16) {
				buffer.append(0);
			}
			buffer.append(Integer.toHexString(value));
		}
		return buffer.toString();
	}
	
}
