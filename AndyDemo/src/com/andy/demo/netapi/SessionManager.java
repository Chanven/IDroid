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
	private Session mSession = new Session("02085115258@189.cn", "0BF80C067991EEE114379AE5D7244A67","6100880b-38ae-4c55-8c74-3df7fdfbce4d", 1000);
}
