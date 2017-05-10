package com.andy.android.util;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;

/**
 * 批量操作执行者类
 *
 */
public class BatchExecutor implements CancellableRunnable {
	protected CancellableRunnable mCurOperation = null; 
	protected BatchExecuteObserver mObserver = null;
	protected ArrayList<CancellableRunnable> mOperationList = null;
	private boolean mbCanceled = false;
	private boolean mbRunning = false;
	
	/**
	 * 操作处理的观察者接口
	 *
	 */
	public interface BatchExecuteObserver {
		/**
		 * Called before any operation will be executed.
		 * @param executor
		 */
		public void onPreExecute(BatchExecutor executor);
		
		/** Called before a operation will be executed.
		 * @param executor
		 * @param operation
		 */
		public void onPreExecuteOperation(BatchExecutor executor, CancellableRunnable operation);
		/**
		 * Called after a operation was executed.
		 * @param executor
		 * @param operation
		 */
		public void onPostExecuteOperation(BatchExecutor executor, CancellableRunnable operation);
		/**
		 * Called after all operations were done.
		 * @param executor
		 */
		public void onExecuteDone(BatchExecutor executor);
	}
	
	public BatchExecutor(int initOperationsCapacity) {
		mOperationList = new ArrayList<CancellableRunnable>(initOperationsCapacity);
	}
	public void setExecuteObserver(BatchExecuteObserver observer) {
		mObserver = observer;
	}
	
	public BatchExecutor add(CancellableRunnable operation) {
		if(mbRunning) {
			throw new IllegalStateException("Can't add new operation while the executor is running");
		}
		mOperationList.add(operation);
		return this;
	}
	
	public boolean remove(CancellableRunnable operation) {
		if(mbRunning) {
			throw new IllegalStateException("Can't add new operation while the executor is running");
		}
		return mOperationList.remove(operation);
	}

	protected void doOperation(CancellableRunnable operation) {
		operation.run();
	}
	
	@Override
	public void cancel() {
		synchronized (this) {
			mbCanceled = true;
			if(mCurOperation != null) {
				mCurOperation.cancel();
			}
		}
	}
	
	@Override
	public boolean isCancelled() {
		return mbCanceled;
	}
	
	@Override
	public void run() {
		mbRunning = true;
		if(mObserver != null) {
			mObserver.onPreExecute(this);
		}
		for(CancellableRunnable operation : mOperationList) {
			if(mbCanceled) {
				break;
			}
			if(mObserver != null) {
				mObserver.onPreExecuteOperation(this, operation);
			}
			synchronized (this) {
				mCurOperation = operation;
			}
			doOperation(operation);
			if(mObserver != null) {
				mObserver.onPostExecuteOperation(this, operation);
			}
		}
		synchronized (this) {
			mCurOperation = null;
		}
		if(mObserver != null) {
			mObserver.onExecuteDone(this);
		}
		mOperationList.clear();
		mbRunning = false;
		if(mbCanceled) {
			throw new CancellationException();
		}
	}
}
