package com.andy.android.util;

public class StorageVolumeWrapper {
	public StorageVolumeWrapper(Object storageVolume) {
		mStorageVolume = storageVolume;
	}
	
	public String getPath() {
		try {
			return (String)ReflectionHelper.invokeMethod(mStorageVolume, "getPath", null);
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean isRemovable() {
		try {
			return (Boolean) ReflectionHelper.invokeMethod(mStorageVolume, "isRemovable", null);
		} catch (Exception e) {
			return false;
		}
	}
	
	public Object getStorageVolume() {
		return mStorageVolume;
	}
	
	private Object mStorageVolume;
}
