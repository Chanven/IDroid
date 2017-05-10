package com.andy.android.content;

import java.util.List;
import java.util.Map.Entry;

public interface ObjectCache<K, O> {
	/**
	 * 清空缓存
	 */
	public void clear();
	
	/**
	 * 存放对象
	 * @param key 对象的唯一索引键
	 * @param object 对象
	 * @return 是否成功存放
	 */
	public boolean put(K key, O object);
	/**
	 * 获取指定的已缓存对象
	 * @param key 对象的唯一索引键
	 * @return 存在则返回对象，否则返回null
	 */
	public O get(K key);
	/**
	 * 返回缓存容量
	 * @return 缓存容量
	 */
	public int capacity();
	/**
	 * 返回已保存的对象数量
	 * @return 已保存的对象数量
	 */
	public int size();
	/**
	 * 移除指定的对象
	 * @param key 对象的唯一索引键
	 * @return 成功移除则返回对象，否则返或null
	 */
	public O remove(K key);
	/**
	 * 判断缓存是否为空
	 * @return
	 */
	public boolean isEmpty();
	/**
	 * 检查缓存中是否已保存指定的对象
	 * @param key 对象的唯一索引键
	 * @return
	 */
	public boolean contains(K key);
	
	/**
	 * 返回所有的缓存键值及对象
	 * @return 缓存键值及对象列表
	 */
	public List<Entry<K, O>> getEntries();

	/**
	 * 执行修剪操作（由缓存对象决定修剪策略）
	 * @return 实际修剪的数量
	 */
	public int prune();
}
