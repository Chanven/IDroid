package com.andy.demo.netapi.param;

import java.util.HashMap;

public class BasicServiceParams {
	private final static Integer KEY_DEFAULT_CONN_TIMEOUT = 1;
	private final static Integer KEY_DEFAULT_SEND_TIMEOUT = 2;
	private final static Integer KET_DEFAULT_RECV_TIMEOUT = 3;
	
	public BasicServiceParams(){
		mParams = new HashMap<Integer, Object>();
	}

	public void clear() {
		mParams.clear();
	}

	public int getDefaultConnTimeout() {
		Integer value = (Integer) mParams.get(KEY_DEFAULT_CONN_TIMEOUT);
		if (value != null) {
			return value;
		}
		return 0;
	}

	public void setDefaultConnTimeout(int seconds) {
		mParams.put(KEY_DEFAULT_CONN_TIMEOUT, seconds);
	}

	public int getDefaultSendTimeout() {
		Integer value = (Integer) mParams.get(KEY_DEFAULT_SEND_TIMEOUT);
		if (value != null) {
			return value;
		}
		return 0;
	}

	public void setDefaultSendTimeout(int seconds) {
		mParams.put(KEY_DEFAULT_SEND_TIMEOUT, seconds);
	}

	public int getDefaultRecvTimeout() {
		Integer value = (Integer) mParams.get(KET_DEFAULT_RECV_TIMEOUT);
		if (value != null) {
			return value;
		}
		return 0;
	}

	public void setDefaultRecvTimeout(int seconds) {
		mParams.put(KET_DEFAULT_RECV_TIMEOUT, seconds);
	}

	public void applyTo(BasicServiceParams params) {
		params.mParams.putAll(mParams);
	}

	protected HashMap<Integer, Object> mParams;
}
