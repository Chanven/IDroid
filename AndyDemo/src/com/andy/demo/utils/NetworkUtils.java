package com.andy.demo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;

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
	 * 用于获取网络标识信息（WIFI、CTWap、CTNet）
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
	
	public static final int CONNENCTION_CMNET = 0;// 网络类型是cmnet

    public static final int CONNENCTION_CMWAP = 1;// 网络类型是cmwap

    public static final int CONNENCTION_WIFI = 2;// 网络类型是wifi

    public static final int CONNENCTION_NO_NET = -1;// 无法连接到网络
	
	/**
     * 获取网络类型
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info == null ||
            (info.getState() != NetworkInfo.State.CONNECTING && info.getState() != NetworkInfo.State.CONNECTED)) {
            return CONNENCTION_NO_NET;
        }
        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            return CONNENCTION_WIFI;
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (Proxy.getDefaultHost() != null || Proxy.getHost(context) != null) {
                return CONNENCTION_CMWAP;
            } else {
                return CONNENCTION_CMNET;
            }
        }
        return CONNENCTION_NO_NET;
    }
}
