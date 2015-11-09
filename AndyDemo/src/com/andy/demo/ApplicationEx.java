package com.andy.demo;

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
import com.andy.demo.activity.ActivityManager;
import com.andy.demo.base.Constant;

public class ApplicationEx extends Application {
	private ActivityManager activityManager = null;
    
	public static ApplicationEx app = null;
	
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
			Class.forName("com.andy.demo.netapi.AutoCancelServiceFramework");
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
				Constant.PACKAGE_NAME = pkgInfo.packageName;
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
		    e.printStackTrace();
//			throw new IllegalStateException("找不到应用程序包名", e);
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
    
    /**
     * 获取额外的运行线程池，2。(主要供Activiy处理后台业务逻辑，如AsyncFramework)
     */
    public Executor getMainExecutor() {
    	return mMainExecutor;
    }
    
    /**
     * 获取空闲任务类的运行线程池，1。（主要用于运行时间不敏感的运行时间可能较长的如清理无用缓存文件、流量上报等任务）
     */
    public Executor getSerialExecutor() {
    	return mSerialExecutor;
    }
    
    /**
     * 获取图片下载线程池，5。(主要供Activity动态下载图片等资源，避免阻塞主要运行线程)
     */
    public Executor getPicExcutor(){
    	return mPicExecutor;
    }

    
    private HashMap<String, Object> mActivityParamsMap = new HashMap<String, Object>();
    private final static Executor mMainExecutor = Executors.newFixedThreadPool(2);
    private final static Executor mSerialExecutor = Executors.newFixedThreadPool(1);
    private final static Executor mPicExecutor = Executors.newFixedThreadPool(5);
    
}
