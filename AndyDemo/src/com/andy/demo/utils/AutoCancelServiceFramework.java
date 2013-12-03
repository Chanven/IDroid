package com.andy.demo.utils;

import java.util.concurrent.CancellationException;

import com.andy.android.util.AutoCancelController;
import com.andy.android.util.AutoCancelFramework;
import com.andy.demo.activity.BaseActivity;
import com.andy.demo.netapi.DownloadService;
import com.andy.demo.netapi.Session;
import com.andy.demo.netapi.SessionManager;
import com.andy.demo.netapi.XServiceFactory;
import com.andy.demo.netapi.exception.XResponseException;

/**
 * @author ivankuo
 *	<p>支持自动取消的任务处理框架</p>
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class AutoCancelServiceFramework<Params, Progress, Result> extends
		AutoCancelFramework<Params, Progress, Result> {

	public AutoCancelServiceFramework(BaseActivity activity) {
		super(activity.getAutoCancelController());
	}
	
	public AutoCancelServiceFramework(AutoCancelController autoCancelController) {
		super(autoCancelController);
	}
	
	
	protected synchronized void createDownlaodService() throws CancellationException, XResponseException {
		if(isCancelled()) {
			throw new CancellationException();
		}
		if(mDownloadService != null) {
			return;
		}
		Session session = SessionManager.get().getCurSession();
		if(session != null && session.isAvailable()) {
			mDownloadService = XServiceFactory.get().createDownloadService(session);
		} else {
			throw new XResponseException(XResponseException.ERRORCODE_SESSION_INAVAILABLE, "Session not available!");
		}
	}
	
	@Override
	protected void finish(Result result) {
		super.finish(result);
		releaseAllServices();
	}

	

	@Override
	public void cancel() {
		super.cancel();
		synchronized (this) {
			if(mDownloadService != null) {
				mDownloadService.abortService();
			}
		}
	}
	
	private void releaseAllServices() {
		synchronized (this) {
			if(mDownloadService != null) {
				XServiceFactory.get().releaseDownloadService(mDownloadService);
				mDownloadService = null;
			}
		}
	}

	@Override
	protected Result doInBackground(Params... params) {
		return null;
	}
	
	protected DownloadService mDownloadService;
}
