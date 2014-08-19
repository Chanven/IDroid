package com.andy.demo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

public class CommonUtils {
	
	/**
	 * 更改飞行模式状态
	 * 只适用于4.2以下版本
	 * */
	public static void changeAirplaneStatus(Context context){
		ContentResolver cr = context.getContentResolver();
		//获取当前飞行模式状态，0为关闭，1为开启
		boolean isAirModeOn = false;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			isAirModeOn = Settings.System.getString(cr, Settings.System.AIRPLANE_MODE_ON).equals("1");
		}else {
			isAirModeOn = Settings.Global.getString(cr, Settings.Global.AIRPLANE_MODE_ON).equals("1");
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, isAirModeOn? "0":"1");
		}else {
			Settings.Global.putString(cr, Settings.Global.AIRPLANE_MODE_ON, isAirModeOn? "0":"1");
		}
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);     
		context.sendBroadcast(intent);  
	}
}
