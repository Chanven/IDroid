/**
 * 
 */
package com.andy.android.util.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeoutException;

/**
 * @author luogh
 *
 * 轮询类任务托管访问接口
 */
public interface PollableTaskHost {
	/**
	 * 等待下一次轮询
	 * @param poller 轮询请求者
	 * @throws InterruptedException
	 */
	public void waitNextPoll(TaskPoller poller) 
		throws IllegalArgumentException, CancellationException;
	
	public void waitNextPoll(TaskPoller poller, long millSeconds) 
		throws IllegalArgumentException, CancellationException, TimeoutException;
	/**
	 * 立即获取下一个可执行对象
	 * @param poller 轮询请求者
	 * @return 可执行对象或null（无可执行对象时）
	 */
	public Runnable getNextTask(TaskPoller poller);
	/**
	 * 检查是否有等待执行的任务
	 * @return
	 */
	public boolean hasPendingTask();
	
	/**
	 * 通知任务已执行完成
	 * @param task 已执行的任务
	 * @param exception 任务运行过程中抛出的异常
	 */
	public void notifyTaskCompleted(Runnable task, Throwable exception);
}
