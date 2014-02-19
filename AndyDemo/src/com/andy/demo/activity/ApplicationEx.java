package com.andy.demo.activity;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.andy.android.util.DLog;
import com.andy.demo.BuildConfig;
import com.andy.demo.base.Constant;

public class ApplicationEx extends Application {
	private ActivityManager activityManager = null;
    
	public static Application app = null;
	
    public ApplicationEx() {
    	
    }
    
    public ActivityManager getActivityManager() {
        return activityManager;
    }
    
    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        //强制预加载
        try {
			Class.forName("android.os.AsyncTask");
			Class.forName("com.andy.android.util.AsyncFramework");
			Class.forName("com.andy.android.util.AutoCancelFramework");
			Class.forName("com.andy.demo.utils.AutoCancelServiceFramework");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        
        app = this;
        DLog.setInDebugMode(BuildConfig.DEBUG);
      //初始化自定义Activity管理器
        activityManager = ActivityManager.getScreenManager();
		try {
			PackageInfo pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			if(pkgInfo.versionName != null) {
				Constant.VERSION = pkgInfo.versionName;
			} else {
				throw new IllegalStateException("应用程序版本号为空");
			}
			
			ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			
			if(applicationInfo != null && applicationInfo.metaData != null){
				Object channelId = applicationInfo.metaData.get("UMENG_CHANNEL");
				if(channelId != null){
					Constant.CHANNELID = channelId.toString();
				}
			}
			
		} catch (NameNotFoundException e) {
			throw new IllegalStateException("找不到应用程序包名", e);
		}catch(Exception e){
			e.printStackTrace();
		}
		
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    
    public void setInternalActivityParam(String key, Object object) {
    	mActivityParamsMap.put(key, object);
    }
    public Object receiveInternalActivityParam(String key) {
    	return mActivityParamsMap.remove(key);
    }
    
    public Executor getMainExecutor() {
    	return mMainExecutor;
    }
    
    public Executor getSerialExecutor() {
    	return mSerialExecutor;
    }
    
    public Executor getTransferExecutor(){
    	return mTransferExecutor;
    }
    
    public Executor getNoTransferExcutor(){
    	return mNoTransferExcutor;
    }
    
    public Executor getPicExcutor(){
    	return mPicExecutor;
    }

    
    private HashMap<String, Object> mActivityParamsMap = new HashMap<String, Object>();
    private final static Executor mMainExecutor = Executors.newFixedThreadPool(2);
    private final static Executor mSerialExecutor = Executors.newFixedThreadPool(1);
    private final static Executor mTransferExecutor = Executors.newFixedThreadPool(1);
    private final static Executor mNoTransferExcutor = Executors.newFixedThreadPool(1);
    private final static Executor mPicExecutor = Executors.newFixedThreadPool(5);
    
}
