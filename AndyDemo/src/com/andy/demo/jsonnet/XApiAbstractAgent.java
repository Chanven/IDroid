package com.andy.demo.jsonnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CancellationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;

import com.andy.android.network.http.ApacheHttpClientFramework;
import com.andy.android.network.http.RequestParams;
import com.andy.android.util.DLog;
import com.andy.demo.BuildConfig;
import com.andy.demo.base.Constant;
import com.andy.demo.netapi.exception.XResponseException;
import com.andy.demo.utils.CodeUtils;
import com.andy.demo.utils.JsonUtils;
import com.andy.demo.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public abstract class XApiAbstractAgent extends ApacheHttpClientFramework {

	protected final static String OPERATE_POST = "POST";

	protected String mToken;
	
	protected XApiAbstractAgent() {
		
	}

	/**
	 * 网络请求前的准备，网络、账号等状态判断
	 * @throws XResponseException
	 */
	protected void prepare() throws XResponseException {
		reset();
		NetworkUtils.checkNetWorkConnected();
		String errorMsg = "";
//		if (...) {
//			...
//			errorMsg = "帐号身份已过期，请重新登录。";
//			errorMsg = "请先登录帐号";
//		}
		if (TextUtils.isEmpty(errorMsg)) {
			return;
		}
		throw new XResponseException(XResponseException.ERRORCODE_INVALID_ACCESSTOKEN,
				errorMsg);
	}
	
	protected <T> T doPost(Map<String, String> map, String serverUrl,
			String uriAction, int connectTimes, Class<T> objClass)
			throws CancellationException, IOException, XResponseException {
		if(BuildConfig.DEBUG){
            dumpRequest(serverUrl,uriAction, map);
        }
		T resultData = null;
		/*map.put("clientVersion", Constant.VERSION);
		map.put("deviceId", Constant.IMEI);
		map.put("deviceOS", "android" + android.os.Build.VERSION.RELEASE);
		map.put("connectType", Constant.CONNECTTYPE);
		map.put("deviceMac", Constant.MAC);
		map.put("deviceIp", Constant.IP);
		map.put("channel", Constant.CHANNELID);
		map.put("deviceType", "android");
		map.put("uuid", Constant.ID);
		map.put("appSign", Constant.SIGN);*/
		RequestParams rp = new RequestParams(RequestParams.METHOD_POST,
				serverUrl + uriAction);
		setupRequiredHeader(rp, uriAction);
		if (map != null) {
			Set<Entry<String, String>> set = map.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				rp.addFormParam(entry.getKey(), entry.getValue());
			}
		}
		HttpResponse response = null;
		try {
			response = send(rp);
			int statusCode = getStatusCode(response);
			if (statusCode >= 200 && statusCode <= 206) {
				if (response.getEntity() != null) {
                    try {
                        if (BuildConfig.DEBUG) {
                            // dumpResponse(uriAction, response);
                        }

                        resultData = parseObjectFromInputStreamByGson(response, objClass);
                    } catch (Exception e) {
						throw new XResponseException(
								XResponseException.ERRCODE_SERVER_RETURN_JSON_TRANS);
					}
					if (resultData == null) {
						throw new XResponseException(
								XResponseException.ERRCODE_SERVER_RETURN_EMPTY);
					} else {
						return resultData;
					}
				} else {
					// 抛服务器内容返回异常
					throw new XResponseException(
							XResponseException.ERRCODE_SERVER_RETURN_EMPTY);
				}
			} else {
				// step5: parse wrong returnString
				parseReturnStringAndThrowErrorException(response);
			}
		} catch (ConnectTimeoutException e) {
			throw new XResponseException(
					XResponseException.ERRCODE_SERVER_TIMEOUT);
		} catch (SocketTimeoutException e) {
			throw new XResponseException(
					XResponseException.ERRCODE_SERVER_TIMEOUT);
		} finally {
			releaseRequest(response);
		}
		return null;
	}

	protected void setupRequiredHeader(RequestParams reqParams, String actionUrl) {
		if ("/uploadPhoto.do".equals(actionUrl)) {
			reqParams.addHeader("Content-Type", "application/octet-stream");
		}
	}

	@Override
	protected void setDefaultHttpParams() {
		super.setDefaultHttpParams();
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT,
				Constant.READ_TIMEOUT);// 从socket读数据时发生阻塞的超时时间
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				Constant.CONNECTION_TIMEOUT);// 连接的超时时间
		mHttpClient.setParams(params);
	}
	
    protected void setHttpParams() {
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);// 从socket读数据时发生阻塞的超时时间
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);// 连接的超时时间
        mHttpClient.setParams(params);
    }

	/**
	 * 利用gson包方法将网络返回的inputstream流转成对象（objClass类型）
	 * @param response
	 * @param objClass
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	protected <T> T parseObjectFromInputStreamByGson(HttpResponse response,
			Class<T> objClass) throws IllegalStateException, IOException {
		Gson gson = new Gson();
		HttpEntity entity = response.getEntity();
		Reader reader = new InputStreamReader(entity.getContent());
		JsonReader jReader = new JsonReader(reader);
		return (T)gson.fromJson(jReader, objClass);
	}

	/**
	 * 利用gson包方法将网络返回的inputstream流转成对象（typeOfT类型）
	 * 
	 * @param response
	 * @param typeOfT
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected <T> T parseObjectFromInputStreamByGson(HttpResponse response,
			Type typeOfT) throws IllegalStateException, IOException {
		Gson gson = new Gson();
		HttpEntity entity = response.getEntity();
		Reader reader = new InputStreamReader(entity.getContent());
		JsonReader jReader = new JsonReader(reader);
		return (T)gson.fromJson(jReader, typeOfT);
	}

	/**
	 * 根据返回错误码，抛出相应的自定义错误描述
	 * 
	 * @param response
	 * @throws IOException
	 * @throws ContactResponseException
	 */
	protected void parseReturnStringAndThrowErrorException(HttpResponse response)
			throws CancellationException, IOException, XResponseException {
		if (response != null) {
			String returnString = null;
			returnString = EntityUtils.toString(response.getEntity());
			if (returnString != null) {
				ErrorResponse err = null;
				try {
					err = JsonUtils.fromJsonString(returnString,
							ErrorResponse.class);
				} catch (Exception e) {
					throw new XResponseException(
							XResponseException.ERRCODE_SERVER_RETURN_JSON_TRANS);
				}
				if (err != null) {
					if (err.errorCode == XResponseException.ERRORCODE_INVALID_ACCESSTOKEN) {
						// 需要重新获取accessToken
//						FPAPITokenManager.get().remove();
					}
					throw new XResponseException(
							XResponseException.ERRCODE_SERVER_RETURN_ERROR_MESSAGE,
							err.message);
				} else {
					throw new XResponseException(
							XResponseException.ERRCODE_SERVER_RETURN_JSON_TRANS);
				}

			} else {
				throw new XResponseException(
						XResponseException.ERRCODE_SERVER_RETURN_EMPTY);
			}
		} else {
			// 抛j服务器内容返回异常
			throw new XResponseException(XResponseException.ERRCODE_SERVER_RETURN_EMPTY);
		}
	}

	/**
	 * 获得请求头日期信息
	 * 
	 * @return
	 */
	protected static String getRequestHeaderDate() {
		Date nowDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String result = sdf.format(nowDate);
		if (result != null) {
			return result;
		} else {
			return "";
		}
	}

	/**
	 * 生成请求头签名信息用到了的hmac_sha1方法
	 * 
	 * @param secrect
	 * @param datas
	 * @return
	 */
	protected static String hmac_sha1(String secrect, String datas) {
		return CodeUtils.hmacsha1(datas, secrect);
	}

	protected static class ErrorResponse {
		public int errorCode;
		public String message;
	}
	
	/**
	 * 打印请求信息
	 * @param uriAction
	 * @param map
	 */
	private void dumpRequest(String serverUrl,String uriAction, Map<String, String> map) {
		try {
			StringBuffer sb = new StringBuffer("");
			StringBuffer parasSb = new StringBuffer("");
			if(null != map && map.size() > 0){
				Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry<String, String> entry = it.next();
					sb.append(entry.getKey() + ":" + entry.getValue() + "\n");
					parasSb.append("&" + entry.getKey() + "=" + entry.getValue());
				}
			}
			DLog.d("request_>>_", uriAction + "：\n" + sb.toString() + "\n");
			DLog.d("request_>>_", serverUrl + uriAction + "?" + parasSb.toString() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
		打印响应信息，打印后会影响后面对JSON的解析，
		所以需要解析JSON的话，不能打印
	 */
	private void dumpResponse(String uriAction, HttpResponse response) {
		if(response == null){
			return;
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer("");
			while((line = br.readLine()) != null){
				sb.append(TextUtils.isEmpty(line) ? "" : line);
			}
			DLog.d("response_<<_", uriAction + "：\n" + sb.toString() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
