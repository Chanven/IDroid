package com.andy.demo.netapi;

import com.andy.android.util.DLog;
import com.andy.android.util.pool.SimpleObjectPool;
import com.andy.demo.netapi.impl.DownloadServiceAgent;

public class XServiceFactory{
	public final static XServiceFactory get() {
		return mInstance;
	}
	public final DownloadService createDownloadService(Session session) {
		DownloadServiceAgent service = (DownloadServiceAgent)mDownloadServicePool.acquire();
		if(service == null) {
			service = new DownloadServiceAgent(session);
			DLog.d(getClass().getSimpleName(), "Create new DownloadService:" + service.toString());
		} else {
			DLog.d(getClass().getSimpleName(), "Reuse DownloadService:" + service.toString());
			service.resetSession(session);
		}
		return service;
	}
	public final void releaseDownloadService(DownloadService service) {
		DLog.d(getClass().getSimpleName(), "Release DownloadService:" + service.toString());
		mDownloadServicePool.release(service);
	}
	private final static XServiceFactory mInstance = new XServiceFactory();
	private SimpleObjectPool<DownloadService> mDownloadServicePool = new SimpleObjectPool<DownloadService>(1);
}
