package com.andy.demo.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.andy.android.util.DLog;
import com.andy.android.util.ReflectionHelper;
import com.andy.android.util.StorageVolumeWrapper;
import com.andy.demo.activity.ApplicationEx;

public class StoragePathManager {
	
	private static final String TAG = "StoragePathManager";
	
	private static StoragePathManager pathManager = null;
	
	private static Object synObject = new Object();
	/**
	 * 根目录,如/mnt/sdcard
	 */
	private String rootPath;
	
	private boolean useInternalStore = false;
	
	private String storageVolumeState;
	
	public static StoragePathManager get(){
		synchronized (synObject) {
			if(pathManager == null){
				pathManager = new StoragePathManager();
			}
		}
		return pathManager;
	}	
	
	private StoragePathManager(){
		resetRootPath();
	}
	
	public void resetRootPath(){
		try {
			init();
			initDiretory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		//1、使用系统api判断是否有外部存储，这里的外部存储理解为“可插拔的sdcard”和“不可插拔的并【可移除】的内置存储”；
		rootPath = null;
		if(existExternalStroage()){
			storageVolumeState = Environment.MEDIA_MOUNTED;
			rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		//2、部分手机通过系统的api无法获取外部存储,针对Android4.0的系统可通过反射【尝试获取】。
		try {
			do{
				String path = null;
				String state = null;
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (TextUtils.isEmpty(rootPath)
						// 前提是Android4.0以上
						&& currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					StorageManager storageMan = null;
					if(ApplicationEx.app != null){
						storageMan = (StorageManager) ApplicationEx.app
								.getSystemService(Context.STORAGE_SERVICE);
					}
					if (storageMan == null) {
						break;
					}
				
					Object[] r = (Object[]) ReflectionHelper.invokeMethod(storageMan, "getVolumeList", null);
					
					//SDcard
					StorageVolumeWrapper storageVolumeWrapperSD = null;
					//手机内存
					StorageVolumeWrapper storageVolumeWrapperInner = null;
					
					int count = r.length;
					for(int i=0;i<count;i++) {
						StorageVolumeWrapper storageVolumeWrapper = new StorageVolumeWrapper(r[i]);
						if(storageVolumeWrapper.isRemovable()) {
							storageVolumeWrapperSD = storageVolumeWrapper;
							if(storageVolumeWrapperSD!=null){
								path = storageVolumeWrapperSD.getPath();
								System.out.println("removeable storageVolume path : "+path);
								Object[] arg = new Object[]{path};
								state = (String)ReflectionHelper.invokeMethod(storageMan, "getVolumeState", arg);
								System.out.println("removeable storageVolume path state : "+state.toString());
								if(state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)){
									storageVolumeState = Environment.MEDIA_MOUNTED;
								}else if(state.equalsIgnoreCase(Environment.MEDIA_SHARED)){
									storageVolumeState = Environment.MEDIA_SHARED;
								}else if(state.equalsIgnoreCase(Environment.MEDIA_REMOVED)){
									storageVolumeState = Environment.MEDIA_REMOVED;
								}
								if(path != null && state != null && state.equals(Environment.MEDIA_MOUNTED) ) {
									rootPath = path;
									//如有多个存储，只获取第一个
									break;
								}
							}
						}
						else {
							storageVolumeWrapperInner = storageVolumeWrapper;
						}
					}
					
					if (storageVolumeWrapperInner != null) {
						path = storageVolumeWrapperInner.getPath();
						System.out.println("unremoveable storageVolume path : "+path);
						Object[] argInner = new Object[] { path };
						state = (String) ReflectionHelper.invokeMethod(storageMan,
								"getVolumeState", argInner);
						System.out.println("unremoveable storageVolume path : "+state.toString());
						if(state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)){
							storageVolumeState = Environment.MEDIA_MOUNTED;
						}else if(state.equalsIgnoreCase(Environment.MEDIA_SHARED)){
							storageVolumeState = Environment.MEDIA_SHARED;
						}else if(state.equalsIgnoreCase(Environment.MEDIA_REMOVED)){
							storageVolumeState = Environment.MEDIA_REMOVED;
						}
						if (path != null && state != null
								&& state.equals(Environment.MEDIA_MOUNTED)) {
							rootPath = path;
						}
					}
					
				}
			}while (false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//3、externalStorageDirectory如果还是为空，那么将使用内存/data/data/xxx/cache作为根目录;
		if(TextUtils.isEmpty(rootPath)){
			if(ApplicationEx.app !=  null){
				rootPath = ApplicationEx.app.getCacheDir().getAbsolutePath();
			}
			storageVolumeState = Environment.MEDIA_REMOVED;
			useInternalStore = true;
		}
		if(TextUtils.isEmpty(storageVolumeState)){
			storageVolumeState = Environment.MEDIA_REMOVED;
		}
		DLog.i(TAG,"storageVolumeState : "+storageVolumeState);
		DLog.i(TAG,"RootPath : "+getRootPath());
	}
	
	/**
	 * 
	 * @return 根目录，如‘/mnt/sdcard’或“/mnt/storage”或“/data/data/xxx/cache”
	 */
	public String getRootPath() {
		if(rootPath == null){
			DLog.e(TAG, "rootPath is null ");
		}
		return rootPath;
	}
	
	/**
	 * 
	 * @return 程序运行时文件存储的主目录
	 */
	public String getMainPath(){
		return getRootPath()+"/com.andy/";
	}
	/**initDiretory()时userName未必有值，因此每次获取是需要判断目录是否有创建*/
	public String getCollectedPath(){
		String retVal = getRootPath()+"/com.andy/ecloud/";
		if(ApplicationEx.app != null){
//			String userName = SharePreferencesUtils.getLastLoginUserName(ApplicationEx.app);
			String userName = "";
			if(!TextUtils.isEmpty(userName)){
				retVal =  getRootPath()+"/com.andy/ecloud/"+userName+"/";
			}
		}
		File collectedDir = new File(retVal);
		if(!collectedDir.exists()){
			collectedDir.mkdirs();
		}
		return retVal;
	}
	
	public String getTransferTmpdir(){
		return getMainPath()+"txtemp/";
	}
	
	public String getFinishedPath(){
		return getMainPath()+"file/download/finished/";
	}
	
	public String getUnfinishedPath(){
		return getMainPath()+"file/download/unfinished/";
	}
	
	public String getUplaodFinishedPath(){
		return getMainPath()+"file/upload/finished/";
	}
	
	public String getUplaodUnfinishedPath(){
		return getMainPath()+"file/upload/unfinished/";
	}
	
	public String getMusicCachePath(){
		return getMainPath()+"file/cache/music/";
	}
	
	public String getImgCachePath(){
		return getMainPath()+"file/cache/images/";
	}
	
	public String getSplashCachePath(){
		return getMainPath()+"file/splashs/";
	}
	
	public String getCamImgCachePath(){
		return getMainPath()+"file/camera/";
	}
	
	public String getContactsCachePath(){
		return getMainPath()+"file/contacts/";
	}
	
	public String getLogPath(){
		return getMainPath()+"file/logs/";
	}
	
	public String getReportPath(){
		return getMainPath() + "file/reports/";
	}
	
	public String getPluginPath(){
		return getMainPath() + "file/plugins/";
	}
	
	public String getPluginIconPath(){
		return getMainPath() + "file/plugins/icons/";
	}
	
	private boolean existExternalStroage() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 创建目录
	 */
	private void initDiretory() {
		File file = new File(getMainPath());
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(getCollectedPath());
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(getTransferTmpdir());
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(getFinishedPath());
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(getUnfinishedPath());
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(getUplaodFinishedPath());
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(getUplaodUnfinishedPath());
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(getMusicCachePath());
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(getImgCachePath());
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(getContactsCachePath());
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(getCamImgCachePath());
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(getLogPath());
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(getReportPath());
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(getPluginPath());
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(getPluginIconPath());
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	/**
	 * 判断是否使用内存存储/data/data/xxx/cache作为程序运行的主目录
	 * @return
	 */
	public boolean isUseInternalStore() {
		return useInternalStore;
	}
	
	public String getStorageVolumeState() {
		return storageVolumeState;
	}

	public void setStorageVolumeState(String storageVolumeState) {
		this.storageVolumeState = storageVolumeState;
		resetRootPath();
	}
}
