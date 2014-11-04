package com.andy.demo.netapi.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CancellationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;

import com.andy.android.util.DLog;
import com.andy.demo.analysis.bean.MyIdInitResult;
import com.andy.demo.netapi.ConstantConfig;
import com.andy.demo.netapi.DownloadService;
import com.andy.demo.netapi.Session;
import com.andy.demo.netapi.exception.XResponseException;
import com.andy.demo.netapi.param.BasicServiceParams;
import com.andy.demo.netapi.request.impl.MyIdInitRequest;
import com.andy.demo.netapi.util.FlowAccumulatorInputStream;
import com.andy.demo.netapi.util.HelperUtil;

public final class DownloadServiceAgent extends
		AbstractXService<BasicServiceParams> implements DownloadService {

	private final static long DOWNLOAD_BYTES_TO_PUBLISH = 16 * 1024;
	private final static long DOWNLOAD_PUBLISH_INTERVAL = 1500;
	private final static int DEFAULT_CONN_TIME_OUT = 15000;
	private final static int DEFAULT_SEND_TIME_OUT = 20000;
	private final static int DEFAULT_RECV_TIME_OUT = 30000;
	private Session mSession;
	
	public DownloadServiceAgent(Session session) {
		super();
		if (null != session) {
			mSession = session;
		}
		mParams = new BasicServiceParams();
		mParams.setDefaultConnTimeout(DEFAULT_CONN_TIME_OUT);
		mParams.setDefaultSendTimeout(DEFAULT_SEND_TIME_OUT);
		mParams.setDefaultRecvTimeout(DEFAULT_RECV_TIME_OUT);
		applyServiceParams(mParams);
	}
	
	public void resetSession(Session session) {
		reset();
		mSession = session; 
	}
	
	//http请求服务模块方法，放在这里测试
	public MyIdInitResult initMyId(boolean qrCodeLogin)
			throws XResponseException, ClientProtocolException,
			IOException, CancellationException {
		return send(new MyIdInitRequest(qrCodeLogin), null);
	}

	@Override
	public long download(String url, long bytesOffset, long bytesToDownload,
			OutputStream outStream, DownloadObserver observer)
			throws XResponseException, IOException, CancellationException {
		
		HttpGet request = new HttpGet(url);
		HelperUtil.addSessionHeader(request, mSession, url);
		if (bytesOffset > 0 || bytesToDownload > 0) {
			// 设置Range头部
			request.setHeader("Range",
					"bytes="
							+ bytesOffset
							+ "-"
							+ ((bytesToDownload > 0) ? (bytesOffset
									+ bytesToDownload - 1) : ""));
		}
		
		//写日志
		if(ConstantConfig.DEBUG){
			DLog.write2File("download", request.getRequestLine().toString());
		}
		
		long completedBytes = bytesOffset;
		try {
			HttpResponse response = null;
			try {
				response = mHttpClient.execute(request);
			} catch (IOException e) {
				e.printStackTrace();
				//写日志
				if(ConstantConfig.DEBUG){
					DLog.write2File("error", e.getMessage()+"    with "+request.getRequestLine().toString());
				}
				throw e;
			}
			//写日志
			if(ConstantConfig.DEBUG){
				DLog.write2File("http status line", response.getStatusLine().toString()+"    with "+request.getRequestLine().toString());
			}
			
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode < 200 || statusCode >= 300) {
				// error
				throw new HttpResponseException(statusCode, response
						.getStatusLine().getReasonPhrase());
			}
			if (observer != null) {
				// 通知已连接
				observer.onConnected(this);
			}
			InputStream is = response.getEntity().getContent();
			if (is == null) {
				contentLength = 0;
				return 0;
			}
			
			contentLength = response.getEntity().getContentLength();
			
			is = new FlowAccumulatorInputStream(is, false);
			try {
				byte buf[] = new byte[1024];

				long lastPublishBytes = 0;
				long lastPublishTime = System.currentTimeMillis();
				while (!mbAborted) {
					// 循环读取
					int numread = is.read(buf);
					if (numread <= 0) {
						// 没有更多内容了
						if (observer != null) {
							if (completedBytes != lastPublishBytes) {
								observer.onProgress(this, completedBytes, 0);
							}
						}
						break;
					}
					outStream.write(buf, 0, numread);
					completedBytes += numread;
					if (observer != null) {
						long curTime = System.currentTimeMillis();
						if (completedBytes - lastPublishBytes >= DOWNLOAD_BYTES_TO_PUBLISH
								|| curTime - lastPublishTime >= DOWNLOAD_PUBLISH_INTERVAL) {
							// 通知当前进度
							observer.onProgress(this, completedBytes, 0);
							lastPublishBytes = completedBytes;
							lastPublishTime = curTime;
						}
					}

				}
			} finally {
				is.close();
			}
		} finally {
			request.abort();
			request = null;
		}
		if (mbAborted) {
			throw new CancellationException();
		}
		return completedBytes;
	}
	
	private long contentLength;
	@Override
	public long getContentLength(){
		return contentLength;
	}

}
