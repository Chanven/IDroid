package com.andy.demo.utils;

import android.text.TextUtils;
import android.util.Base64;
/**
 * 简单加密/解密
 * @author chanven
 *
 */
public class SimpleDES {

	/**
	 * 明文加密
	 * @param input 明文
	 * @return 密文
	 */
	public static String getEncString(String input){
		if(TextUtils.isEmpty(input)) return "";
		String output = "";
		byte[] encStr =Base64.encode(input.getBytes(), Base64.DEFAULT);
		output = new String(encStr);
		return output;
	}
	
	/**
	 * 密文解密
	 * @param input 密文
	 * @return 明文
	 */
	public static String getDecString(String input){
		if(TextUtils.isEmpty(input)) return "";
		String output = "";
		byte[] encStr =Base64.decode(input.getBytes(), Base64.DEFAULT);
		output = new String(encStr);
		return output;
	}
}
