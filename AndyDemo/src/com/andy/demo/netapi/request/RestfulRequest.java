package com.andy.demo.netapi.request;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.net.Uri;

import com.andy.android.util.Cancellable;
import com.andy.android.util.DLog;
import com.andy.demo.analysis.Analysis;
import com.andy.demo.analysis.ErrorAnalysis;
import com.andy.demo.netapi.ConstantConfig;
import com.andy.demo.netapi.Session;
import com.andy.demo.netapi.exception.XResponseException;
import com.andy.demo.netapi.util.FlowAccumulatorInputStream;
import com.andy.demo.netapi.util.HelperUtil;

public abstract class RestfulRequest<R> implements Cancellable {
	private static final String REQUEST_SEND_TAG = ">>";
	private static final String REQUEST_RECEIVE_TAG = "<<";
	protected final static String METHOD_GET = "GET";
	protected final static String METHOD_POST = "POST";

	public RestfulRequest(String method) {
		if (method.equalsIgnoreCase(METHOD_GET)) {
			mHttpRequest = new HttpGet();
		} else {
			mHttpRequest = new HttpPost();
		}
	}

	public synchronized RestfulRequest<R> setHttpClient(HttpClient httpClient) {
		mHttpClient = httpClient;
		return this;
	}

	public abstract R send(Session session) throws XResponseException,
			ClientProtocolException, IOException, CancellationException,
			IllegalArgumentException;

	@Override
	public void cancel() {
		synchronized (this) {
			mbCancelled = true;
			if (mHttpClient != null) {
				mHttpClient.getConnectionManager().shutdown();
			}
		}

	}

	@Override
	public boolean isCancelled() {
		return mbCancelled;
	}

	protected final void setHeader(String name, String value) {
		mHttpRequest.setHeader(name, value);
	}
	/**
	 * post 时调用
	 * @param httpEntity
	 */
	protected final void setEntity(HttpEntity httpEntity) {
		if(mHttpRequest.getMethod().equals(METHOD_POST)){
			((HttpPost)mHttpRequest).setEntity(httpEntity);
		}
	}

	protected final void setRequestParam(String name, String value) {
		mRequestParams.add(new BasicNameValuePair(name, Uri.encode(value)));
	}
	
	protected final void addHttpHeaders(HashMap<String,String> headerMap) {
		HelperUtil.addHttpHeaders(mHttpRequest, headerMap);
	}

	protected final void addSessionHeaders(Session session, String requestURI) {
		HelperUtil.addSessionHeader(mHttpRequest, session, requestURI);
	}
	
	protected final void dumpRequest(HttpRequestBase request, ArrayList<NameValuePair> params) {
		DLog.d(REQUEST_SEND_TAG, request.getRequestLine().toString());
		DLog.write2File("http request", request.getRequestLine().toString());
		Header [] headers = request.getAllHeaders();
		for(Header header : headers) {
			DLog.d(REQUEST_SEND_TAG, header.toString());
		}
		for(NameValuePair param : params) {
			DLog.d(REQUEST_SEND_TAG, param.toString());
		}
	}
	
	protected void setContentType(){
		mHttpRequest.setHeader("Content-Type", "text/xml; charset=utf-8");
	}

	protected InputStream send(String uri) throws ClientProtocolException,
			IOException, XResponseException {

		String reqUri = uri;
		InputStream responseContent = null;
//		if (mHttpRequest.getMethod().equals(METHOD_GET)) {
			setContentType();
			if (!mRequestParams.isEmpty()) {
				// 将参数置于uri后面
				StringBuffer sb = new StringBuffer();
				boolean bFirstParam = true;
				for (NameValuePair pair : mRequestParams) {
					if (!bFirstParam) {
						sb.append('&');
					} else {
						bFirstParam = false;
					}
					sb.append(pair.getName() + "=");
					sb.append(pair.getValue());
				}
				reqUri = reqUri + "?" + sb;
			}
//		} else {
//			HttpPost postMethod = (HttpPost) mHttpRequest;
//			if (!mRequestParams.isEmpty()) {
//				postMethod.setHeader("Content-Type",
//						"application/x-www-form-urlencoded");
//				postMethod.setEntity(new UrlEncodedFormEntity(mRequestParams,
//						HTTP.UTF_8));
//			} else {
//				postMethod.setHeader("Content-Type", "text/xml; charset=utf-8");
//			}
//		}

		mHttpRequest.setURI(URI.create(reqUri));
		synchronized (this) {
			if(mHttpClient == null) {
				mHttpClient = new DefaultHttpClient();
			}
		}
		if(mbCancelled) {
			throw new CancellationException();
		}
		if(ConstantConfig.DEBUG) {
			dumpRequest(mHttpRequest, mRequestParams);
		}
		HttpResponse response = null;
		try {
			response = mHttpClient.execute(mHttpRequest);
		} catch(IOException e) {
			e.printStackTrace();
			if(ConstantConfig.DEBUG){
				dumpException(e);
			}
			if(mbCancelled) {
				throw new CancellationException();
			} else {
				throw e;
			}
		}catch(IllegalStateException e) {
			e.printStackTrace();
			if(mbCancelled) {
				throw new CancellationException();
			} else {
				throw e;
			}
		}
		StatusLine statusLine = response.getStatusLine();
		if(ConstantConfig.DEBUG) {
			dumpResponse(response);
		}
		if (statusLine != null) {
			int statusCode = statusLine.getStatusCode();
			int internalStatusCode = 200;
			Header statusCodeHeader = response.getFirstHeader("Status-Code");
			if(statusCodeHeader != null) {
				// 有错误发生
				internalStatusCode = Integer.parseInt(statusCodeHeader.getValue());
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				if(is != null) {
					// Ugly code here! Since we have bound the flow stat work inside
					responseContent = new FlowAccumulatorInputStream(is);
				}
			}
			if (statusCode < 200 || statusCode >= 300
					|| internalStatusCode < 200 || internalStatusCode >= 300) {
				// Error
				try {
					if (responseContent != null) {
						ErrorAnalysis analysis = new ErrorAnalysis();
						Analysis.parser(analysis, responseContent);
						// Make sure all contents are fetch
						// so we can reuse the connection again.
						entity.consumeContent();
						responseContent = null;
						if (analysis.succeeded()) {
							// Unexpected!!
							throw new XResponseException("StatusCode:"
									+ statusCode
									+ " Failed to parse error message!");
						} else {
							throw new XResponseException(
									Integer.valueOf(analysis._error._code), analysis._error._message);
						}
					} else {
						// No content
						throw new XResponseException("StatusCode:"
								+ statusCode + " No response content!");
					}
				} finally {
					mHttpRequest.abort();
				}
			}
		}

		return responseContent;
	}

	private void dumpResponse(HttpResponse response) {
		if(response == null){
			return;
		}
		DLog.d(REQUEST_RECEIVE_TAG, "status line is " + response.getStatusLine());
		DLog.write2File("http status line", response.getStatusLine().toString()+"   with "+ mHttpRequest.getRequestLine().toString());
		Header [] headers = response.getAllHeaders();
		for(Header header : headers) {
			DLog.d(REQUEST_RECEIVE_TAG, header.toString());
			DLog.write2File("http response header", header.toString());
		}
	}
	
	private void dumpException(Exception e){
		DLog.write2File("error", e.getMessage()+"    with "+mHttpRequest.getRequestLine().toString());
	}

	protected boolean mbCancelled;
	protected HttpClient mHttpClient;
	protected HttpRequestBase mHttpRequest;
	protected ArrayList<NameValuePair> mRequestParams = new ArrayList<NameValuePair>(4);
}
