package com.andy.android.transfer;

/**
 * 传输任务上下文
 *
 */
public abstract class TransferTaskContext {
	protected long mBytesCompleted = 0;
	protected long mContentLength = 0;
	protected int mTransferType;
	protected long mCreateTime;
	
	public TransferTaskContext(int transferType) {
		mTransferType = transferType;
	}
	
	public synchronized int getTransferType() {
		return mTransferType;
	}
	
	public synchronized long getCreateTime() {
		return mCreateTime;
	}
	
	public synchronized long setCreateTime(long createTime) {
		return mCreateTime = createTime;
	}
	
	public synchronized void addBytesCompleted(long commitedBytes) {
		mBytesCompleted += commitedBytes;
	}
	public synchronized void setBytesCompleted(long bytesCompleted) {
		mBytesCompleted = bytesCompleted;
	}
	public synchronized long getBytesCompleted() {
		return mBytesCompleted;
	}
	public synchronized void setContentLength(long contentLength) {
		mContentLength = contentLength;
	}
	public synchronized long getContentLength() {
		return mContentLength;
	}
}
