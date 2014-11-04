package com.andy.android.network.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * 请求参数设置类
 *
 */
public class RequestParams {
	
	public static final int METHOD_GET = 0;
	public static final int METHOD_POST = 1;
	public static final int METHOD_PUT = 2;
	
	private ArrayList<BasicNameValuePair> mHeaders;
	private ArrayList<BasicNameValuePair> mQueryParams;
	private ArrayList<BasicNameValuePair> mBodyParams;
	private String mRequestUri;
	private int mMethod;
	
	/**
	 * @param method Http请求方法
	 * @param uri 请求的Uri
	 */
	public RequestParams(int method, String uri) {
		mRequestUri = uri;
		mMethod = method;
	}
	/**
	 * 添加Http Header
	 * @param name Header名称
	 * @param value Header值
	 */
	public final void addHeader(String name, String value) {
		if(mHeaders == null) {
			mHeaders = new ArrayList<BasicNameValuePair>();
		}
		mHeaders.add(new BasicNameValuePair(name, value));
	}
	/**
	 * 添加URL的Query参数
	 * @param name 参数名
	 * @param value 参数值(需经过escape)
	 */
	public final void addUrlQueryParam(String name, String value) {
		if(mQueryParams == null) {
			mQueryParams = new ArrayList<BasicNameValuePair>();
		}
		mQueryParams.add(new BasicNameValuePair(name, value));
	}
	/**
	 * 添加x-www-form-urlencoded格式的参数
	 * @param name 参数名
	 * @param value 参数值(需经过escape)
	 */
	public final void addFormParam(String name, String value) {
		if(METHOD_POST != mMethod && METHOD_PUT != mMethod) {
			throw new IllegalStateException("Entity not suport in current http method");
		}
		if(mBodyParams == null) {
			mBodyParams = new ArrayList<BasicNameValuePair>();
		}
		mBodyParams.add(new BasicNameValuePair(name, value));
	}
	
	/**
	 * 根据已有参数设置请求对象
	 * @return 请求对象
	 * @throws UnsupportedEncodingException
	 */
	HttpRequestBase build() throws UnsupportedEncodingException {
		String reqUri;
		HttpRequestBase request;
		switch(mMethod)
		{
		case METHOD_POST:
			request = new HttpPost();
			break;
		case METHOD_PUT:
			request = new HttpPut();
		case METHOD_GET:
		default:
			request = new HttpGet();	
		}
		if (mQueryParams != null && !mQueryParams.isEmpty()) {
			// 将参数置于uri后面
			StringBuffer sb = new StringBuffer();
			boolean bFirstParam = true;
			for (BasicNameValuePair pair : mQueryParams) {
				if (!bFirstParam) {
					sb.append('&');
				} else {
					bFirstParam = false;
				}
				sb.append(pair.getName() + "=");
				sb.append(pair.getValue());
			}
			reqUri = mRequestUri + "?" + sb;
		} else {
			reqUri = mRequestUri;
		}
		if(mBodyParams != null && !mBodyParams.isEmpty()) {
			HttpEntityEnclosingRequestBase entityRequest = (HttpEntityEnclosingRequestBase)request;
			entityRequest.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			entityRequest.setEntity(new UrlEncodedFormEntity(mBodyParams,
					HTTP.UTF_8));
		}
		if(mHeaders != null && !mHeaders.isEmpty()) {
			for (BasicNameValuePair pair : mHeaders) {
				request.setHeader(pair.getName(), pair.getValue());
			}
		}
		request.setURI(URI.create(reqUri));
		return request;
	}
}
