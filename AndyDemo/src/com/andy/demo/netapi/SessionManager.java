package com.andy.demo.netapi;


public final class SessionManager {
	public static SessionManager get() {
		return mInstance;
	}
	
	public final Session getCurSession() {
    	return mSession;
    }
    public final void setCurSession(Session session) {
    	if(mSession != session) {
    		if(mSession != null) {
    			mSession.setExpired();
    		}
    		mSession = session;
    	}
    }
	private static SessionManager mInstance = new SessionManager();
	private Session mSession;
}
