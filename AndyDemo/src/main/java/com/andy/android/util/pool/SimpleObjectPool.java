package com.andy.android.util.pool;

import java.util.LinkedList;


public class SimpleObjectPool<O> implements ObjectPool<O> {

	public SimpleObjectPool(int maxPoolSize) {
		mMaxPoolSize = maxPoolSize;
		mAllocator = null;
	}
	public SimpleObjectPool(int maxPoolSize, ObjectAllocator<O> allocator) {
		mMaxPoolSize = maxPoolSize;
		mAllocator = allocator;
	}
	
	@Override
	public O acquire() {
		synchronized (this) {
			if(mObjectList != null) {
				return mObjectList.poll();
			}
			else if(mAllocator != null) {
				return mAllocator.create();
			}
		}
		return null;
	}

	@Override
	public void release(O object) {
		synchronized (this) {
			if(0 == mMaxPoolSize) {
				mObjectList.add(object);
			}
			else if(mObjectList.size() >= mMaxPoolSize) {
				return;
			} 
			else if(!mObjectList.contains(object)){
				mObjectList.add(object);
			}
		}
	}

	@Override
	public void setMaxPoolSize(int size) {
		synchronized (this) {
			if(size > 0 && size < mMaxPoolSize) {
				// 减少多余对象
				int len = mMaxPoolSize - size;
				for(int i=0; i<len; i++) {
					mObjectList.poll();
				}
			}
			mMaxPoolSize = size;
		}
	}

	@Override
	public int getMaxPoolSize() {
		return mMaxPoolSize;
	}
	
	protected int mMaxPoolSize;
	protected LinkedList<O> mObjectList = new LinkedList<O>();
	protected ObjectAllocator<O> mAllocator;

}
