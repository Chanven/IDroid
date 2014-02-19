package com.andy.demo.netapi.request.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CancellationException;

import org.apache.http.client.ClientProtocolException;

import com.andy.demo.analysis.Analysis;
import com.andy.demo.analysis.MyIdInitAnalysis;
import com.andy.demo.analysis.bean.MyIdInitResult;
import com.andy.demo.netapi.ConstantConfig;
import com.andy.demo.netapi.Session;
import com.andy.demo.netapi.exception.XResponseException;
import com.andy.demo.netapi.request.RestfulRequest;

public class MyIdInitRequest extends RestfulRequest<MyIdInitResult>{
	private final static String ACTION_NAME = "myid/init.action";
	private final static String REQUEST_URI = ConstantConfig.PLATFORM_SERVER_HOST + ACTION_NAME;

	public MyIdInitRequest(boolean qrCodelogin) {
		super(METHOD_GET);
		if (qrCodelogin) {
			setRequestParam("qrCodelogin", 1 + "");
		}
	}

	@Override
	public MyIdInitResult send(Session session) throws XResponseException,
			ClientProtocolException, IOException, CancellationException,
			IllegalArgumentException {
		addSessionHeaders(session, ACTION_NAME);
		InputStream responseContent = send(REQUEST_URI);
		if (mbCancelled) {
			throw new CancellationException();
		}
		if (responseContent != null) {
			MyIdInitAnalysis analysis = new MyIdInitAnalysis();
			Analysis.parser(analysis, responseContent);
			responseContent.close();
			if (analysis.succeeded()) {
				// 成功
				return analysis.myIdInitResult;
			} else {
				// 失败
				// Should never goes here!
				throw new XResponseException(Integer.valueOf(analysis._error._code),
						analysis._error._message);
			}
		} else {
			throw new XResponseException("No response content!");
		}
	}
	
}
