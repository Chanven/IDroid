/**
 * 
 */
package com.andy.android.util;

import java.util.HashSet;

/**
 * @author luogh
 * 任务自动取消控制器
 *
 */
public class AutoCancelController {
	public AutoCancelController() {
		mGCSet = new HashSet<Cancellable>();
	}
	AutoCancelController(int initCapacity) {
		mGCSet = new HashSet<Cancellable>(initCapacity);
	}
	/**
	 * 添加受托管清理的任务
	 * @param task 需要被自动取消对象
	 * @return
	 */
	public boolean add(Cancellable task) {
		return mGCSet.add(task);
	}
	/**
	 * 删除受托管清理的任务
	 * @param task
	 * @return
	 */
	public boolean remove(Cancellable task) {
		return mGCSet.remove(task);
	}
	/**
	 * 执行取消操作。将对每一个受托管清理的任务
	 * 进行取消操作，并在执行后清空列表
	 */
	public void clean() {
		for(Cancellable task : mGCSet) {
			task.cancel();
		}
		mGCSet.clear();
	}
	
	/**
	 * 清除所有受托管清理的任务（并不取消任务）
	 */
	public void clear() {
		mGCSet.clear();
	}
	private HashSet<Cancellable> mGCSet = null;
}
