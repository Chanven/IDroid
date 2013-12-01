/**
 * 
 */
package com.andy.android.util.concurrent;

import java.util.concurrent.CancellationException;

import com.andy.android.util.Cancellable;


/**
 * @author luogh
 * 
 * 任务轮询者。用于以不断轮询任务的机制向任务托管对象
 * 请求任务并执行。一般可用ThreadPoolExecutor配合使用
 * <h2>Usage</h2>
 * <p>
 * executor.execute(new TaskPoller(taskHost));
 * </p>
 */
public class TaskPoller implements Cancellable, Runnable {
	
	/**
	 * @param taskHost 任务托管对象
	 */
	public TaskPoller(PollableTaskHost taskHost) {
		mTaskHost = taskHost;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while(!mbCancel)
		{
			// 尝试获取下一个任务
			Runnable task = mTaskHost.getNextTask(this);
			if(task != null) {
				Throwable ex = null;
				try {
					// 执行任务
					task.run();
				} catch(Throwable t) {
					ex = t;
				} finally {
					// 通知任务完成
					mTaskHost.notifyTaskCompleted(task, ex);
				}
			}
			// 等待下一次轮询
			try {
				mTaskHost.waitNextPoll(this);
			} catch (CancellationException e) {
				// 退出
				break;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				// 退出
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.cn21.android.util.Cancellable#cancel()
	 */
	@Override
	public void cancel() {
		mbCancel = true;
	}

	/* (non-Javadoc)
	 * @see com.cn21.android.util.Cancellable#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		return mbCancel;
	}
	
	protected PollableTaskHost mTaskHost;
	protected boolean mbCancel;

}
