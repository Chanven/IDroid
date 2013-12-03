package com.andy.demo.netapi.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import com.andy.android.util.DLog;
import com.andy.demo.netapi.ConstantConfig;
import com.andy.demo.netapi.Session;
import com.andy.demo.netapi.param.BasicServiceParams;
import com.andy.demo.utils.HMACSHA1;

import android.text.format.DateFormat;

public final class HelperUtil {
	
	public static String getSignatrue(String uri, String sessKey,
			String sessSecret, String operate, String date) {
		StringBuilder data = new StringBuilder();
		data.append(ConstantConfig.msSessionKeyName + "=");
		data.append(sessKey);
		data.append("&Operate=");
		data.append(operate);
		if(uri.startsWith("/")) {
			data.append("&RequestURI=");
		} else {
			data.append("&RequestURI=/");
		}
		data.append(uri);
		data.append("&Date=");
		data.append(date);
		DLog.v("httpSignature", data.toString());
		return HMACSHA1.getHmacSHA1(data.toString(), sessSecret);
	}
	
	public static String getAppSignature(String appKey, String appSecret, String loginName, String password, String date) {
		// hmac_sha1(“AppKey=相应的值&LoginName=相应的值&Password=相应的值&Date=相应的值”, AppSecret)
		StringBuilder data = new StringBuilder();
		data.append("AppKey=" + appKey);
		data.append("&LoginName=" + loginName);
		data.append("&Password=" + password);
		data.append("&Date=" + date);
		String sign = data.toString();
		DLog.d("AppSignature:", sign);
		return HMACSHA1.getHmacSHA1(sign, appSecret);
	}
	
	public static String getAppSignatureByCTAccount(String appKey, String appSecret, String surfingToken, String date) {
		// hmac_sha1(“AppKey=相应的值&LoginName=相应的值&Password=相应的值&Date=相应的值”, AppSecret)
		StringBuilder data = new StringBuilder();
		data.append("AppKey=" + appKey);
		data.append("&surfingToken=" + surfingToken);
		data.append("&Date=" + date);
		String sign = data.toString();
		DLog.d("AppSignature:", sign);
		return HMACSHA1.getHmacSHA1(sign, appSecret);
	}
	
	public static String getAppSignatureByIMSI(String appKey, String appSecret, String imsi, String date) {
		// hmac_sha1(“AppKey=相应的值&LoginName=相应的值&Password=相应的值&Date=相应的值”, AppSecret)
		StringBuilder data = new StringBuilder();
		data.append("AppKey=" + appKey);
		data.append("&IMSI=" + imsi);
		data.append("&Date=" + date);
		String sign = data.toString();
		DLog.d("AppSignature:", sign);
		return HMACSHA1.getHmacSHA1(sign, appSecret);
	}
	
	public static String getAppSignature(String appKey, String appSecret, long timeStamp) {
		StringBuilder data = new StringBuilder();
		data.append("appKey=" + appKey);
		data.append("&timestamp=" + String.valueOf(timeStamp));
		String sign = data.toString();
		DLog.d("appSignature:", sign);
		return HMACSHA1.getHmacSHA1(sign, appSecret);
	}
	
	public static void addHttpHeaders(HttpRequestBase request, HashMap<String,String> headerMap) {
		if(null == headerMap || headerMap.isEmpty()){
			return;
		}
		
		Set<Map.Entry<String,String>> mapSet = headerMap.entrySet();
		Iterator<Map.Entry<String,String>> it = mapSet.iterator();
		while(it.hasNext()){
			Map.Entry<String, String> entry = it.next();
			request.setHeader(entry.getKey(), entry.getValue());
		}
		
	}
	
	public static void addSessionHeader(HttpRequestBase request, Session session, String requestURI) {
		if(session == null ){
			return;
		}
		Date nowDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
	    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	    String date = sdf.format(nowDate);
		String sessKey = session.getSessionKey();
		request.setHeader(ConstantConfig.msSessionKeyName, sessKey);
		request.setHeader(
				"Signature",
				HelperUtil.getSignatrue(requestURI, sessKey, session.getSessionSecret(),
						request.getMethod(), date));
		request.setHeader("Date", date);
	}
	
	public static void applyServiceParams(BasicServiceParams params, HttpClient httpClient) {
		int connTimeout = params.getDefaultConnTimeout();
		int sendTimeout = params.getDefaultSendTimeout();
		int recvTimeout = params.getDefaultRecvTimeout();
		HttpParams httpParams = httpClient.getParams();
		httpParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connTimeout);
		httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, (sendTimeout > recvTimeout)? sendTimeout : recvTimeout);
	}
	
	public static String formatDateTime(long dt) {
		return (String) DateFormat.format("yyyy-MM-dd kk:mm:ss", dt);
	}
}
