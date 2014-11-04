package com.andy.demo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.andy.demo.ApplicationEx;
import com.andy.demo.netapi.exception.XResponseException;

public class NetworkUtils {

	/**
     * 检查网络设置
     * @param context
     * @throws XResponseException
     */
    public static void checkNetWorkConnected() throws XResponseException {
        if (ApplicationEx.app != null) {
            Context context = ApplicationEx.app.getApplicationContext();
            if (!isNetWorkConnected(context)) {
                throw new XResponseException(XResponseException.ERRORCODE_NETWORK_UNAVAILABLE);
            }
        }
    }
	
	/**
     * 无网络提示并返回false，有网络返回true
     * @param context
     * @return 是否有网络连接
     */
    public static boolean isNetWorkConnected(Context context) {
        if (getAvailableNetWorkInfo(context) == null) {
            return false;
        } else {
            return true;
        }
    }
	
	/**
	 * 用于获取网络标识（WIFI、CTWap、CTNet）
	 * @param context
	 * @return  当前网络信息
	 */
	public static NetworkInfo getAvailableNetWorkInfo(Context context) {
		if (context == null) {
			return null;
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.isConnected()) {
			return activeNetInfo;
		} else {
			return null;
		}
	}
}
