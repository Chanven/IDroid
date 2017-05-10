package com.andy.android.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.andy.android.util.DLog;

import android.content.Context;
import android.os.ConditionVariable;

public class TransferManager {
	protected ArrayList<TransferHandler> mTransferList = new ArrayList<TransferHandler>(16);
	
	private ExecutorService mExecutor;
	private boolean mbShutdown = false;
	private ConditionVariable mReqCond = new ConditionVariable(false);

	////////////////////////////////////////////////////////////////////
	//  传输状态
	public final static int TRANSFER_STATE_PAUSED = 0;
	public final static int TRANSFER_STATE_PENDING = 1;
	public final static int TRANSFER_STATE_RUNNING = 2;
	public final static int TRANSFER_STATE_KILLED = 3;
	public final static int TRANSFER_STATE_COMPLETED = 4;
	public final static int TRANSFER_STATE_ERROR = 5;
	
	////////////////////////////////////////////////////////////////////
	// 传输类型
	public final static int TRANSFER_TYPE_DOWNLOAD = 0;
	public final static int TRANSFER_TYPE_UPLOAD = 1;
	public static final int TRANSFER_TYPE_CAMERA_UPLOAD = 2;
	private static long msCurTransferID = 0;
	
	protected TransferManager() {
		
	}
	
	/**
	 * 传输管理初始化
	 * @param concurrentThreads 同时运行的线程数量
	 */
	public void init(Context context, int concurrentThreads) {
		mExecutor = Executors.newFixedThreadPool(concurrentThreads);
		// 运行传输处理循环
		for(int i=0; i<concurrentThreads; i++) {
			mExecutor.execute(
					new Runnable() {
						@Override
						public void run() {
							transferLoop();
						}
					});
		}
	}
	/**
	 * 关闭传输管理(关闭后不可重用)
	 */
	public void shutdown() {
		mbShutdown = true;
		synchronized (mTransferList) {
			for(TransferHandler taskHandler : mTransferList) {
				taskHandler.pause();
			}
			mTransferList.clear();
		}
		mReqCond.open();
		if(mExecutor != null) {
			mExecutor.shutdown();
		}
	}
	public boolean isShutdown() {
		return mbShutdown;
	}
	public final int getTransferCount() {
		synchronized (mTransferList) {
			return mTransferList.size();
		}
	}

	/**
	 * 移除指定的传输任务
	 * @param transferID 任务ID
	 * @return 任务被移除返回true，任务不存在返回false
	 */
	public boolean remove(long transferID) {
		TransferHandler taskHandler = null;
		boolean bRemoved = false;
		synchronized (mTransferList) {
			int i = 0;
			for(TransferHandler th : mTransferList) {
				if(transferID == th.mTransferID) {
					taskHandler = th;
					break;
				}
				++i;
			}
			if(taskHandler != null) {
				mTransferList.remove(i);
			}
		}
		if(taskHandler != null) {
			taskHandler.kill();
			mReqCond.open();
			bRemoved = true;
			handleTaskRemoved(taskHandler);
		}
		return bRemoved;
	}
	
	/**
	 * 恢复传输任务运行，文件被加载或新建后需调用此方法运行
	 * @param transferID 任务ID
	 * @return 当前任务状态
	 */
	public int resume(long transferID) {
		TransferHandler taskHandler;
		synchronized (mTransferList) {
			taskHandler = findTransferHandler(transferID);	
		}
		if(taskHandler != null) {
			DLog.d("resume", "Transfer task resume. id=" + transferID);
			taskHandler.resume();
			mReqCond.open();
			return taskHandler.mInternalState;
		}
		return TRANSFER_STATE_ERROR;
	}
	
	/**
	 * 暂停传输任务
	 * @param transferID 任务ID
	 * @return 当前任务状态
	 */
	public int pause(long transferID) {
		TransferHandler taskHandler;
		synchronized (mTransferList) {
			taskHandler = findTransferHandler(transferID);	
		}
		if(taskHandler != null) {
			DLog.d("pause", "Transfer task pause. id=" + transferID);
			taskHandler.pause();
			mReqCond.open();
			return taskHandler.mInternalState;
		}
		return TRANSFER_STATE_ERROR;
	}
	
	/**
	 * 获取传输任务信息
	 * @param transferID 任务ID
	 * @return 
	 */
	public TransferInfo getTransferInfo(long transferID) {
		TransferHandler taskHandler = null;
		synchronized (mTransferList) {
			taskHandler = findTransferHandler(transferID);
		}
		if(taskHandler != null) {
			TransferInfo info = new TransferInfo(taskHandler.mTransferID, 
					taskHandler.mInternalState,
					taskHandler.mTaskName,
					taskHandler.mTaskContext,
					taskHandler.mLastException);
			return info;
		}
		return null;
	}
	
	/**
	 * 获取所有传输任务ID
	 * @return
	 */
	public List<Long> getTransferIDList() {
		ArrayList<Long> idList;
		synchronized (mTransferList) {
			idList = new ArrayList<Long>(mTransferList.size());
			for(TransferHandler taskHandler : mTransferList) {
				idList.add(Long.valueOf(taskHandler.mTransferID));
			}
		}
		return idList;
	}
	
	/**
	 * 获取所有传输任务信息
	 * @return
	 */
	public List<TransferInfo> getAllTranferInfo() {
		ArrayList<TransferInfo> infoList;
		synchronized (mTransferList) {
			infoList = new ArrayList<TransferInfo>(mTransferList.size());
			for(TransferHandler taskHandler : mTransferList) {
				TransferInfo info = new TransferInfo(taskHandler.mTransferID, 
						taskHandler.mInternalState,
						taskHandler.mTaskName,
						taskHandler.mTaskContext,
						taskHandler.mLastException);
				infoList.add(info);
			}
		}
		return infoList;
	}
	
	/**
	 * 通过传输任务的唯一标识返回该任务在传输管理器中的任务ID
	 * @param taskIdentity 传输任务的唯一标识
	 * @return 返回 > 0的数值表示任务ID，0表示没有此任务
	 */
	public long findTransfer(String taskIdentity) {
		synchronized (mTransferList) {
			for(TransferHandler taskHandler : mTransferList) {
				if(taskIdentity.equals(taskHandler.mTransferTask.getIdentity())) {
					return taskHandler.mTransferID;
				}
			}
		}
		return 0;
	}
	
	private TransferHandler findTransferHandler(long transferID) {
		for(TransferHandler taskHandler : mTransferList) {
			if(transferID == taskHandler.mTransferID) {
				return taskHandler;
			}
		}
		return null;
	}
	
	final private long generateTransferID() {
		return (++msCurTransferID);
	}
	/**
	 * 返回下一个等待运行的任务
	 * @return
	 */
	protected TransferHandler getNextTask() {
		for(TransferHandler taskHandler : mTransferList) {
			if(taskHandler == null) {
				continue;
			}
			if(0 == taskHandler.mThreadID) {
				// 检查其状态是否等待传输
				if(taskHandler.isPending()) {
					// 选取此任务
					return taskHandler;
				}
			}
		}
		return null;
	}
	protected void handleTaskAdded(TransferHandler taskHandler) {
		
	}
	protected void handleTaskComplete(TransferHandler taskHandler) {
	}
	protected void handleStartTaskRunning(TransferHandler taskHandler) {
	}
	protected void handleEndOfTaskRunning(TransferHandler taskHandler) {
	}
	protected void handleTaskRemoved(TransferHandler taskHandler) {
		
	}
	
	/**
	 * 传输任务调度循环处理
	 */
	private void transferLoop() {
		TransferHandler taskHandler;
		DLog.d("transferLoop", "Transfer loop started");
		while(true) {
			// 循环遍历
			synchronized (mTransferList) {
				taskHandler = getNextTask();
				if(taskHandler != null) {
					taskHandler.mThreadID = Thread.currentThread().getId();
				} else {
					// 条件置为未有请求以便
					// 进入等待状态
					mReqCond.close();
				}
			}
			if(taskHandler != null) {
				try {
					// 通知任务开始执行
					handleStartTaskRunning(taskHandler);
					taskHandler.run();
					if(taskHandler.isCompleted()) {
						// 通知任务已完成
						handleTaskComplete(taskHandler);
					} else {
						// 通知任务已停止（暂停或出现错误）
						handleEndOfTaskRunning(taskHandler);
					}
				} finally {
					synchronized (mTransferList) {
						// 声明此任务没有占用该线程
						taskHandler.mThreadID = 0;
					}
				}
			} else {
				if(mbShutdown) {
					break;
				}
				// 等待下一次检查
				mReqCond.block();
				if(mbShutdown) {
					break;
				}
			}
		}
		DLog.d("transferLoop", "Transfer loop shutdown");
	}
	
	protected long addTransfer(TransferTask task) {
		long transferID = 0;
		TransferTaskContext taskContext = task.getTaskContext();
		TransferHandler newTaskHandler = null;
		synchronized (mTransferList) {
			for(TransferHandler taskHandler : mTransferList) {
				if(taskHandler.mTransferType != taskContext.getTransferType()) {
					continue;
				}
				if(task.getIdentity().equals(taskHandler.mTransferTask.getIdentity())) {
					// 已经存在
					onTransferTaskRepeated(taskHandler);
					return 0;
				}
			}
			// 创建任务
			transferID = generateTransferID();
			newTaskHandler = new TransferHandler(transferID, task);
			mTransferList.add(newTaskHandler);
		}
		handleTaskAdded(newTaskHandler);
		return transferID;
	}
	/**
	 * 相同的任务加入到传输队列时
	 * @param taskHandler 原任务句柄
	 */
	protected void onTransferTaskRepeated(TransferHandler taskHandler){
		
	}

	public static class TransferHandler implements Runnable {
		TransferHandler(long transferID, TransferTask task) {
			mTransferID = transferID;
			mTransferTask = task;
			//mInternalState = TRANSFER_STATE_PAUSED;
			mTaskContext = task.getTaskContext();
			mInternalState = (mTaskContext.mBytesCompleted == mTaskContext.mContentLength)?TRANSFER_STATE_COMPLETED:TRANSFER_STATE_PAUSED;
			mTransferType = mTaskContext.getTransferType();
			mTaskName = task.getName();
		}
		public long mTransferID;
		public int mTransferType;
		public int mInternalState;
		public String mTaskName;
		public TransferTask mTransferTask;
		public TransferTaskContext mTaskContext;
		public long mThreadID = 0;
		public Throwable mLastException = null;
		
		public final boolean isPaused() {
			return (TRANSFER_STATE_PAUSED == mInternalState);
		}
		public final boolean isPending() {
			return (TRANSFER_STATE_PENDING == mInternalState);
		}
		public final boolean isRunning() {
			return (TRANSFER_STATE_RUNNING == mInternalState);
		}
		public final boolean isCompleted() {
			return (TRANSFER_STATE_COMPLETED == mInternalState);
		}
		public final boolean isKilled() {
			return (TRANSFER_STATE_KILLED == mInternalState);
		}
		public final boolean isError() {
			return (TRANSFER_STATE_ERROR == mInternalState);
		}
		
		public void resume() {
			synchronized (this) {
				if(isPaused() || isError()) {
					mInternalState = TRANSFER_STATE_PENDING;
					mLastException = null;
				}
			}
		}
		public void pause() {
			synchronized (this) {
				if(isPending() || isRunning()) {
					mInternalState = TRANSFER_STATE_PAUSED;
					if(mTransferTask != null) {
						mTransferTask.cancel();
					}
				}
			}
		}
		public void kill() {
			boolean bCleanup = false;
			synchronized (this) {
				if(!isCompleted() && !isKilled()) {
					boolean bRunning = isRunning();
					mInternalState = TRANSFER_STATE_KILLED;
					if(mTransferTask != null) {
						mTransferTask.cancel();
					}
					if(!bRunning) {
						bCleanup = true;
					}
				}
			}
			if(bCleanup) {
				mTransferTask.kill();
			}
		}

		@Override
		public void run() {
			Throwable tr = null;
			synchronized(this) {
				if(!isPending()) {
					// 不是等待运行状态或者已有传输任务，立即返回
					return;
				} else {
					mInternalState = TRANSFER_STATE_RUNNING;
				}
			}
			
			try {
				// 确保任务环境正常
				mTransferTask.prepare();
				// 运行传输任务
				mTransferTask.startTransfer();
			} catch(Exception e) {
				tr = e;
				DLog.d(this.getClass().getSimpleName(), "Transfer Ex ", e);
			}
			boolean bCleanup = false;
			synchronized(this) {
				if(isRunning()) {
					if(tr == null) {
						mInternalState = TRANSFER_STATE_COMPLETED;
					} else if(!(tr instanceof CancellationException)) {
						mInternalState = TRANSFER_STATE_ERROR;
						mLastException = tr;
					}
				}
				else if(isKilled()) {
					// 释放资源
					bCleanup = true;
				}
				
			}
			if(bCleanup) {
				mTransferTask.kill();
			}
		}
		
	}
}
