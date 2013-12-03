package com.andy.demo.netapi;

public final class Session {
	public Session(String account, String sessionKey, String sessionSecret, int keepAlive) {
		mAccount = account;
		mSessionKey = sessionKey;
		mSessionSecret = sessionSecret;
		mKeepAlive = keepAlive;
	}
	public final String getAccount() {
		return mAccount;
	}
	public final String getSessionKey() {
		return mSessionKey;
	}
	public final String getSessionSecret() {
		return mSessionSecret;
	}
	public final int getKeepAlive() {
		return mKeepAlive;
	}
	public final boolean isBroken() {
		return mbBroken;
	}
	public final boolean isExpired() {
		return mbExpired;
	}
	public final boolean isAvailable() {
		return (!mbBroken && !mbExpired);
	}
	public final void setBroken() {
		mbBroken = true;
	}
	public final void setExpired() {
		mbExpired = true;
	}
	private boolean mbBroken;
	private boolean mbExpired;
	private String mAccount;
	private String mSessionKey;
	private String mSessionSecret;
	private int mKeepAlive;
}
