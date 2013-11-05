/**
 * 
 */
package com.andy.android.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.andy.demo.utils.StoragePathManager;
import com.andy.demo.utils.TimeUtils;

import android.util.Log;

/**
 * 日志信息输出类
 * 
 * <p>该类可自动或手动配置不同等级日志在发布模式下是否允许输出，
 * 并使用android.util.Log输出日志内容</p>
 *
 */
public final class DLog {
	private static boolean mbDebugMode = true;
	private static boolean mbLogDInRelease = false;
	private static boolean mbLogVInRelease = false;
	private static boolean mbLogIInRelease = true;
	private static boolean mbLogWInRelease = true;
	private static boolean mbLogEInRelease = true;
	private static boolean mbNeedWrite2File = false;
	public static void setInDebugMode(boolean bDebug) {
		mbDebugMode = bDebug;
	}
	public static void setLogDInRelease(boolean bLog) {
		mbLogDInRelease = bLog;
	}
	public static void setLogVInRelease(boolean bLog) {
		mbLogVInRelease = bLog;
	}
	public static void setLogIInRelease(boolean bLog) {
		mbLogIInRelease = bLog;
	}
	public static void setLogWInRelease(boolean bLog) {
		mbLogWInRelease = bLog;
	}
	public static void setLogEInRelease(boolean bLog) {
		mbLogEInRelease = bLog;
	}
	public static boolean isInDebugMode() {
		return mbDebugMode;
	}
	public static int d(String tag, String msg) {
		if(mbDebugMode || mbLogDInRelease) {
			return Log.d(tag, msg);
		}
		return 0;
	}
	public static int d(String tag, String msg, Throwable tr) {
		if(mbDebugMode || mbLogDInRelease) {
			return Log.d(tag, msg, tr);
		}
		return 0;
	}
	public static int i(String tag, String msg) {
		if(mbDebugMode || mbLogIInRelease) {
			return Log.i(tag, msg);
		}
		return 0;
	}
	public static int i(String tag, String msg, Throwable tr) {
		if(mbDebugMode || mbLogIInRelease) {
			return Log.i(tag, msg, tr);
		}
		return 0;
	}
	public static int v(String tag, String msg) {
		if(mbDebugMode || mbLogVInRelease) {
			return Log.v(tag, msg);
		}
		return 0;
	}
	public static int v(String tag, String msg, Throwable tr) {
		if(mbDebugMode || mbLogVInRelease) {
			return Log.v(tag, msg, tr);
		}
		return 0;
	}
	public static int w(String tag, String msg) {
		if(mbDebugMode || mbLogWInRelease) {
			return Log.w(tag, msg);
		}
		return 0;
	}
	public static int w(String tag, String msg, Throwable tr) {
		if(mbDebugMode || mbLogWInRelease) {
			return Log.w(tag, msg, tr);
		}
		return 0;
	}
	public static int e(String tag, String msg) {
		if(mbDebugMode || mbLogEInRelease) {
			return Log.e(tag, msg);
		}
		return 0;
	}
	public static int e(String tag, String msg, Throwable tr) {
		if(mbDebugMode || mbLogEInRelease) {
			return Log.e(tag, msg, tr);
		}
		return 0;
	}
	public static void write2File(String tag, String msg){
		write2File(tag, msg, TimeUtils.getStringDateShort()+"_log.txt");
	}
	public static void write2File(String tag, String msg,String name){
		if(mbDebugMode || mbNeedWrite2File){
			
			String fileDir = StoragePathManager.get().getLogPath();
			String fileName = name;
			
			File file = new File(fileDir+fileName);
			FileWriter fw = null;
			try {
				fw = new FileWriter(file,true);
				StringBuffer sb = new StringBuffer();
				sb.append(TimeUtils.getStringDateLongest());
				sb.append("	");
				sb.append(tag);
				sb.append("	");
				sb.append(msg);
				sb.append("\n\t");
				fw.write(sb.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(fw != null){
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
