package com.andy.android.network.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CancellationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import com.andy.android.util.Cancellable;

/**
 * Apache Http 客户端通信的框架类
 * 
 * 已实现以下处理:
 * 取消当前请求操作
 * 设置请求的Query参数（放在URL后）、x-www-form-urlencoded形式的参数（放在BODY中）、Http Header
 * 
 * 使用前提:
 * 	IO操作都是同步方式实现，Http请求操作同一时间只能在同一个线程中调用（但可以在其他线程调用cancel取消操作）
 * 
 * 使用方式:
 * 通过RequestParams对象设置相应的URL及参数
 * 调用send方法取得服务器返回的HttpResponse
 * 获取HttpResponse的数据并作相应的解析处理
 * 调用releaseRequest方法释放资源（应放在finally中保证被调用）
 *
 */
public class ApacheHttpClientFramework implements Cancellable {

	protected boolean mbCancelled;
	protected HttpRequestBase mHttpRequest;
	protected DefaultHttpClient mHttpClient;

	protected ApacheHttpClientFramework() {
		reset();
	}
	
	
	
	@Override
	public void cancel() {
		synchronized (this) {
			mbCancelled = true;
			if(mHttpRequest != null) {
				mHttpRequest.abort();
			}
		}
	}

	@Override
	public boolean isCancelled() {
		return mbCancelled;
	}
	
	/**
	 * 释放请求相关资源
	 * @param response 服务器响应的对象，不存在可为null
	 */
	protected void releaseRequest(HttpResponse response) {
		synchronized (this) {
			if(mHttpRequest != null) {
				mHttpRequest.abort();
				mHttpRequest = null;
			}
		}
	}

	/**
	 * 发送Http请求
	 * @param request
	 * @return HttpResponse
	 * @throws IOException
	 * @throws CancellationException
	 */
	protected HttpResponse send(RequestParams request) throws IOException, CancellationException {
		HttpRequestBase httpRequest = request.build();
		synchronized (this) {
			if(mbCancelled) {
				throw new CancellationException();
			}
			// 确保原有的请求已失效
			if(mHttpRequest != null) {
				mHttpRequest.abort();
			}
			mHttpRequest = httpRequest;
			//mHttpRequest.setHeader("Content-Type", "text/xml; charset=utf-8");
			if(mHttpClient == null) {
				mHttpClient = new DefaultHttpClient();
			}
		}
		
		HttpResponse response = null;
		try {
			response = mHttpClient.execute(mHttpRequest);
		} catch(IllegalStateException e) {
			e.printStackTrace();
			if(mbCancelled) {
				throw new CancellationException();
			} else {
				throw new IOException(e.getMessage());
			}
		}
		return response;
		
	}
	
	protected void setDefaultHttpParams() {
		// ToDo: 子类重载此方法并设置默认的http客户端参数
	}

	protected synchronized void reset() {
		if(mbCancelled || mHttpClient == null) {
			HttpParams params = null;
			if(mHttpClient != null) {
				//client = mHttpClient;
				params = mHttpClient.getParams().copy();
			} else {
				//client = new DefaultHttpClient();
			}
			//HttpParams params = client.getParams();
			cancel();
			if(params != null) {
				mHttpClient = new DefaultHttpClient(params); //(new SingleClientConnManager(params, schreg), params);
			} else {
				mHttpClient = new DefaultHttpClient();
			}
			setDefaultHttpParams();
			mbCancelled = false;
		}
	}
	
	static public int getStatusCode(HttpResponse response) {
		if(response != null) {
			StatusLine statusLine = response.getStatusLine();
			if(statusLine != null) {
				return statusLine.getStatusCode();
			}
		}
		return 0;
	}
	
	static public InputStream getEntityStream(HttpResponse response) throws IllegalStateException, IOException {
		if(response != null) {
			HttpEntity entity = response.getEntity();
			if(entity != null) {
				return entity.getContent();
			}
		}
		return null;
	}

}
