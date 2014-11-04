package com.andy.android.transfer;

/**
 * 传输任务信息
 *
 */
public class TransferInfo {
	public TransferInfo(long transferID, 
			int transferState, 
			String taskName,  
			TransferTaskContext taskContext,
			Throwable lastException) {
		mTransferID = transferID;
		mTaskName = taskName;
		mTaskContext = taskContext;
		mTransferState = transferState;
		mLastException = lastException;
		 
	}
	public long mTransferID;
	public int mTransferState;
	public String mTaskName;
	public TransferTaskContext mTaskContext;
	public Throwable mLastException;
}
