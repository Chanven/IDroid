package com.andy.android.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.andy.android.util.DLog;

/**
 * @author luogh
 * 
 * 具备线性关系的对象缓存
 * <p>
 * 使用可视中心作为对象修剪策略的依据。修剪时将尽可能先裁剪掉远离可视中心位置的对象
 * 并在修剪时尽量保持可视中心两边对象数量对称，也就是说越靠近可视中心的对象越重要。
 * </p>
 * 
 * @param <K> 对象键值类型
 * @param <O> 对象类型
 */
public final class LinearVisibleObjectCache<K, O> implements ObjectCache<K, O> {

	/**
	 * @param capacity 缓存最大容量，为0表示无限制
	 * @param maxPruneReserve 每次修剪后所预留的最大可用空间，为0表示不作任何修剪
	 * @throws IllegalArgumentException
	 */
	public LinearVisibleObjectCache(int capacity, int maxPruneReserve) throws IllegalArgumentException {
		if( capacity < 0 ||  maxPruneReserve < 0) {
			throw new IllegalArgumentException();
		}
		mCapacity = capacity;
		mMaxPruneReserv = maxPruneReserve;
	}
	
	/**
	 * 内部缓存结构,子类可扩展此类以存储更多信息
	 *
	 */
	class LinearEntry {
		LinearEntry(O object, int location) {
			mObject = object;
			mLocation = location;
		}
		public O getObject() {
			return mObject;
		}
		public int getLocation() {
			return mLocation;
		}
		protected O mObject;
		protected int mLocation;
	}

	/* (non-Javadoc)
	 * @see com.andy.android.content.ObjectCache#clear()
	 */
	@Override
	public synchronized void clear() {
		mContainer.clear();
	}

	/* (non-Javadoc)
	 * @see com.andy.android.content.ObjectCache#get(java.lang.Object)
	 */
	@Override
	public synchronized O get(K key) {
		LinearEntry entry = mContainer.get(key);
		if(entry != null) {
			return entry.getObject();
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.andy.android.content.ObjectCache#capacity()
	 */
	@Override
	public int capacity() {
		return mCapacity;
	}

	/* (non-Javadoc)
	 * @see com.andy.android.content.ObjectCache#size()
	 */
	@Override
	public synchronized int size() {
		return mContainer.size();
	}

	/* (non-Javadoc)
	 * @see com.andy.android.content.ObjectCache#remove(java.lang.Object)
	 */
	@Override
	public synchronized O remove(K key) {
		LinearEntry entry = mContainer.remove(key);
		if(entry != null) {
			return entry.getObject();
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.andy.android.content.ObjectCache#isEmpty()
	 */
	@Override
	public synchronized boolean isEmpty() {
		return mContainer.isEmpty();
	}

	/* (non-Javadoc)
	 * @see com.andy.android.content.ObjectCache#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public synchronized boolean put(K key, O object) {
		return put(key, object, mCenterVisble);
	}
	
	public synchronized boolean put(K key, O object, int location) {
		return put(key, new LinearEntry(object, location));
	}
	
	class MapEntry implements Entry<K, O> {
		
		MapEntry(K key, O object) {
			mKey = key;
			mObject = object;
		}

		@Override
		public K getKey() {
			return mKey;
		}

		@Override
		public O getValue() {
			return mObject;
		}

		@Override
		public O setValue(O object) {
			mObject = object;
			return object;
		}
		private K mKey;
		private O mObject;
	}

	/* (non-Javadoc)
	 * @see com.andy.android.content.ObjectCache#entrySet()
	 */
	@Override
	public synchronized List<Entry<K, O>> getEntries() {
		ArrayList<Entry<K, O>> entryList = new ArrayList<Entry<K, O>>(mContainer.size());
		if(mContainer.size() > 0) {
			Set<Entry<K, LinearEntry>> entrySet = mContainer.entrySet();
			for(Entry<K, LinearEntry> entry : entrySet) {
				Entry<K, O> newEntry = new MapEntry(entry.getKey(), entry.getValue().getObject());
				entryList.add(newEntry);
			}
		}
		return entryList;
	}

	/* (non-Javadoc)
	 * @see com.andy.android.content.ObjectCache#prune()
	 */
	@Override
	public synchronized int prune() {
		if(mCapacity > 0) {
			int num = mContainer.size() + mMaxPruneReserv - mCapacity;
			if(num > 0 ) {
				return pruneForReservation(num);
			}
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see com.andy.android.content.ObjectCache#contains(java.lang.Object)
	 */
	@Override
	public synchronized boolean contains(K key) {
		return mContainer.containsKey(key);
	}
	
	/**
	 * 设置可视中心的位置
	 * @param center 中心位置
	 */
	public final void setCenterVisible(int center) {
		mCenterVisble = center;
	}
	
	/**
	 * 获取可视中心位置
	 * @return 中心位置
	 */
	public final int getCenterVisible() {
		return mCenterVisble;
	}
	
	private final boolean put(K key, LinearEntry entry) {
		if(mCapacity > 0 && mContainer.size() + 1 >= mCapacity) {
			// 可能需要修剪
			if(prune() == 0) {
				// 没有做任何修剪，检查key是否已存在
				if(!contains(key)) {
					// 没有空间容纳更多的对象
					return false;
				}
			}
		}
		mContainer.put(key, entry);
		return true;
	}

	/**
	 * 通过修剪预留指定数量的缓存空间
	 * @param numOfReserv 需要保留的对象数量
	 * @return 实际被修剪掉的数量
	 */
	private final int pruneForReservation(int numOfReserv) {
		int count = 0;
		if(numOfReserv >= mCapacity) {
			count = mContainer.size();
			clear();
			return count;
		}
		else if(mContainer.size() + numOfReserv <= mCapacity) {
			return 0;
		}
		int numShouldPrune = (mContainer.size() + numOfReserv) - mCapacity;
		ArrayList<Entry<K, LinearEntry>> entryList = new ArrayList<Entry<K, LinearEntry>>(mContainer.entrySet());
		Collections.sort(entryList, new Comparator<Entry<K, LinearEntry>>() {
			@Override
			public int compare(Entry<K, LinearEntry> lhs, Entry<K, LinearEntry> rhs) {
				return lhs.getValue().getLocation() - rhs.getValue().getLocation();
			}
		});
		// 计算最后（最大）一个小于中心点的索引
		int i = 0;
		for(i=0; i<entryList.size(); ++i) {
			if(entryList.get(i).getValue().getLocation() >= mCenterVisble) {
				break;
			}
		}
		int leftMax = i - 1;
		// 计算第一个（最小）刚好大于中心点的索引
		int rightMin = entryList.size();
		for(i=leftMax+1; i<entryList.size(); ++i) {
			if(entryList.get(i).getValue().getLocation() > mCenterVisble) {
				rightMin = i;
				break;
			}
		}
		int leftToPrune = leftMax + 1;
		int rightToPrune = entryList.size() - rightMin;
		if(numShouldPrune <= leftToPrune + rightToPrune) {
			// 尽量使得中心点两边的对象数量一致
			if(leftToPrune > rightToPrune) {
				int delta = leftToPrune - rightToPrune;
				if(delta >= numShouldPrune) {
					// 只裁剪左边
					leftToPrune = numShouldPrune;
					rightToPrune = 0;
				} else {
					leftToPrune = delta + (numShouldPrune - delta)/2;
					rightToPrune = numShouldPrune - leftToPrune;
				}
			} else {
				int delta = rightToPrune - leftToPrune;
				if(delta >= numShouldPrune) {
					// 只裁剪右边
					rightToPrune = numShouldPrune;
					leftToPrune = 0;
				} else {
					rightToPrune = delta + (numShouldPrune - delta)/2;
					leftToPrune = numShouldPrune - rightToPrune;
				}
			}
		} else {
			// 左右都需要裁剪掉
			int remain = numShouldPrune - (leftToPrune + rightToPrune);
			// 裁剪掉左右两边依然不够，需要裁剪部分位于中心点的对象
			leftToPrune += remain;
		}
		// 裁剪左侧
		for(i=0; i<leftToPrune; ++i) {
			DLog.d("prune", entryList.get(i).getKey().toString());
			if(remove(entryList.get(i).getKey()) != null) {
				++count;
			}
		}
		// 裁剪右侧
		int size = mContainer.size();
		for(i=size - rightToPrune; i<size; ++i) {
			DLog.d("prune", entryList.get(i).getKey().toString());
			if(remove(entryList.get(i).getKey()) != null) {
				++count;
			}
		}
		assert(count == numShouldPrune);
		return count;
	}
	
	@Override
	protected void finalize() throws Throwable {
		DLog.d(getClass().getSimpleName(), "**************** finalize() ****************");
		super.finalize();
	}

	protected int mCenterVisble;
	protected Map<K, LinearEntry> mContainer = new HashMap<K, LinearEntry>();
	protected int mCapacity;
	protected int mMaxPruneReserv;
	

}
