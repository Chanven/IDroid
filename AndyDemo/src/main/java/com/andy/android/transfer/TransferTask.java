package com.andy.android.transfer;

import java.io.IOException;
import java.util.concurrent.CancellationException;

import com.andy.android.util.Cancellable;
import com.andy.demo.netapi.exception.XResponseException;

/**
 * 传输任务
 *
 */
public abstract class TransferTask implements Cancellable {
	protected String mTaskID = "";
	protected boolean mbCancelled = false;
	protected boolean mbTransfering = false;
	protected boolean mbKilled = false;
	protected TransferTask() {
	}
	public final String getIdentity() {
		return mTaskID;
	}
	public abstract String getName();
	public abstract TransferTaskContext getTaskContext();
	public boolean prepare() throws IOException {
		synchronized (this) {
			if(mbKilled) {
				throw new IllegalStateException("This task had already been killed!");
			}
			else if(mbTransfering) {
				return false;
			}
			mbCancelled = false;
		}
		return true;
	}
	public void startTransfer() throws CancellationException, XResponseException, IOException {
		synchronized (this) {
			if(mbKilled) {
				throw new IllegalStateException("This task had already been killed!");
			}
			else if(mbTransfering) {
				throw new IllegalStateException("Already running in another thread!");
			}
			else if(mbCancelled) {
				throw new CancellationException();
			}
			// 表明任务已被运行
			mbTransfering = true;
		}
		try {
			doTransfer();
		} finally {
			synchronized (this) {
				// 表明任务已结束运行
				mbTransfering = false;
			}
		}
	}
	
	protected abstract void doTransfer() throws CancellationException, XResponseException, IOException;
	
	/* (non-Javadoc)
	 * @see com.cn21.android.util.Cancellable#cancel()
	 */
	@Override
	public void cancel() {
		synchronized (this) {
			mbCancelled = true;
		}
	}
	/* (non-Javadoc)
	 * @see com.cn21.android.util.Cancellable#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		return mbCancelled;
	}
	public boolean isKilled() {
		return mbKilled;
	}
	public void kill() {
		synchronized (this) {
			if(mbTransfering) {
				throw new IllegalStateException("Already running in another thread!");
			}
		}
	}
	
}
