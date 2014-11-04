package com.andy.android.network.http;

import org.apache.http.params.HttpParams;

/**
 * Http客户端组件
 * 实现基本的Http请求及取消操作
 *
 */
public class ApacheHttpClient extends ApacheHttpClientFramework {
	protected RequestParams mReqParams;
	
	public ApacheHttpClient(int method, String uri) {
		super();
		reset(method, uri);
	}
	
	public void reset(int method, String uri) {
		super.reset();
		mReqParams = new RequestParams(method, uri);
	}
	
	/**
	 * 添加Http Header
	 * @param name Header名称
	 * @param value Header值
	 */
	public final void addHeader(String name, String value) {
		if(mReqParams != null) {
			mReqParams.addHeader(name, value);
		} else {
			throw new IllegalStateException("Not initialize!");
		}
	}
	/**
	 * 添加URL的Query参数
	 * @param name 参数名
	 * @param value 参数值
	 */
	public final void addUrlQueryParam(String name, String value) {
		if(mReqParams != null) {
			mReqParams.addUrlQueryParam(name, value);
		} else {
			throw new IllegalStateException("Not initialize!");
		}
	}
	/**
	 * 添加x-www-form-urlencoded格式的参数
	 * @param name 参数名
	 * @param value 参数值
	 */
	public final void addFormParam(String name, String value) {
		if(mReqParams != null) {
			mReqParams.addFormParam(name, value);
		} else {
			throw new IllegalStateException("Not initialize!");
		}
	}
	
	/**
	 * 获取参数设置对象
	 * @return
	 */
	public HttpParams getHttpParams() {
		synchronized (this) {
			if(mHttpClient != null) {
				return mHttpClient.getParams();
			}
		}
		return null;
	}
}
