package com.andy.demo.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;

public class TimeUtils {
    public final static String LONGEST_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public final static String LONG_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String SHORT_FORMAT = "yyyy-MM-dd";
    public final static String TIME_FORMAT = "HH:mm:ss";
    public final static String TIME_SHORT_FORMAT = "HH:mm";
    private static SimpleDateFormat formatter = new SimpleDateFormat();

   
    /**
     * 获取现在时间
     * 
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss.SSS
     */
    public static Date getNowDateLongest() {
	return getNowDate(LONGEST_FORMAT);
    }

    /**
     * 获取现在时间
     * 
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
	return getNowDate(LONG_FORMAT);
    }

    /**
     * 获取现在时间
     * 
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static Date getNowDateShort() {
	return getNowDate(SHORT_FORMAT);
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     * 
     * @return
     */
    public static Date getNowTimeShort() {
	return getNowDate(TIME_FORMAT);
    }

    /**
     * 获取现在时间
     * 
     * @param timeFormat
     *            返回时间格式
     */
    public static Date getNowDate(String timeFormat) {
	Date currentTime = new Date();
	Date currentTime_2 = null;
	synchronized (formatter) {
	    formatter.applyPattern(timeFormat);
	    String dateString = formatter.format(currentTime);
	    ParsePosition pos = new ParsePosition(0);
	    currentTime_2 = formatter.parse(dateString, pos);
	}
	return currentTime_2;
    }

  
    /**
     * 获取现在时间
     * 
     * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss.SSS
     */
    public static String getStringDateLongest() {
	return getStringDate(LONGEST_FORMAT);
    }

    /**
     * 获取现在时间
     * 
     * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
	return getStringDate(LONG_FORMAT);
    }

    /**
     * 获取现在时间
     * 
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateShort() {
	return getStringDate(SHORT_FORMAT);
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     * 
     * @return
     */
    public static String getTimeShort() {
	return getStringDate(TIME_FORMAT);
    }
  
    /**
     * 获取现在时间
     * 
     * @param 返回字符串格式
     */
    public static String getStringDate(String timeFormat) {
	java.util.Date currentTime = new java.util.Date();
	String dateString = null;
	synchronized (formatter) {
	    formatter.applyPattern(timeFormat);
	    dateString = formatter.format(currentTime);
	}
	return dateString;
    }

   
    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss.SSS
     * 
     * @param strDate
     * @return
     */
    public static Date strToLongDateLongest(String strDate) {
	return strToDate(strDate, LONGEST_FORMAT);
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     * 
     * @param strDate
     * @return
     */
    public static Date strToLongDate(String strDate) {
	return strToDate(strDate, LONG_FORMAT);
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     * 
     * @param strDate
     * @return
     */
    public static Date strToShortDate(String strDate) {
	return strToDate(strDate, SHORT_FORMAT);
    }

    /**
     * 将时间格式字符串转换为时间 HH:mm:ss
     * 
     * @param strDate
     * @return
     */
    public static Date strToTimeDate(String strDate) {
	return strToDate(strDate, TIME_FORMAT);
    }
    /**
     * 获取时间 小时:分 HH:mm
     * @return
     */
    public static Date strTimeMoreShort(String strDate) {
    	return strToDate(strDate, TIME_SHORT_FORMAT);
        }
    /**
     * 按指定的时间格式字符串转换为时间
     * 
     * @param strDate
     * @param timeFormat
     * @return
     */
    public static Date strToDate(String strDate, String timeFormat) {
	Date strtodate = null;
	synchronized (formatter) {
	    formatter.applyPattern(timeFormat);
	    ParsePosition pos = new ParsePosition(0);
	    strtodate = formatter.parse(strDate, pos);
	}
	return strtodate;
    }

  
    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss.SSS
     * 
     * @param dateDate
     * @return
     */
    public static String dateToLongestStr(Date dateDate) {
	return dateToStr(dateDate, LONGEST_FORMAT);
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     * 
     * @param dateDate
     * @return
     */
    public static String dateToLongStr(Date dateDate) {
	return dateToStr(dateDate, LONG_FORMAT);
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     * 
     * @param strDate
     * @return
     */
    public static String dateToShortStr(Date dateDate) {
	return dateToStr(dateDate, SHORT_FORMAT);
    }

    /**
     * 将时间格式字符串转换为时间 HH:mm:ss
     * 
     * @param strDate
     * @return
     */
    public static String dateToTimeStr(Date dateDate) {
	return dateToStr(dateDate, TIME_FORMAT);
    }

    /**
     * 按指定的时间格式时间转换为字符串
     * 
     * @param dateDate
     * @param timeFormat
     * @return
     */
    public static String dateToStr(Date dateDate, String timeFormat) {
	String dateString = null;
	synchronized (formatter) {
	    formatter.applyPattern(timeFormat);
	    dateString = formatter.format(dateDate);
	}
	return dateString;
    }

    public static String LongToStr(long m, String timeFormat) {
	String dateString = null;
	synchronized (formatter) {
	    formatter.applyPattern(timeFormat);
	    dateString = formatter.format(new Date(m));
	}
	return dateString;
    }
    
    public final static int DAYTIME = 1000 * 60 * 60 * 24;
    /**
     * 获得今天是星期几
     * @param dt
     * @return
     */
    public static String getWeekOfDate(Context context ) {
    		Date dt=new Date();
    		 Calendar cal = Calendar.getInstance();
 	        cal.setTime(dt);

 	        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
 	        if (w < 0)
 	            w = 0;

    	 if(context.getResources().getConfiguration().locale.equals(Locale.CHINA) ||context.getResources().getConfiguration().locale.equals(Locale.TRADITIONAL_CHINESE))
    	 {
    		 String[] weekDays= {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    		 return weekDays[w];
    	 }
    	 else
    	 {
    		 String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    	        return weekDays[w];
    	 }
       
    } 
    
    
    public static String getMonth(Context context ) {
    	int d[] = TimeUtils.getDate();
    	String string = null;
    	 if(context.getResources().getConfiguration().locale.equals(Locale.CHINA) ||context.getResources().getConfiguration().locale.equals(Locale.TRADITIONAL_CHINESE))
    	 {
    		 string =String.valueOf(d[1])+ "月";
    	 }
    	 
    	 else {
    		 
    		 String[] month = {"January", "February","March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
			 string =String.valueOf(month[d[1]-1]);
		}
    	 
		return string;
    	
    }
    
 
    /**
     * 获取年月
     * @return
     */
    public static int [] getDate()
    {
    	Calendar   cal   =   Calendar.getInstance();
    	int   year   =   cal.get(Calendar.YEAR);
    	int   month   =   cal.get(Calendar.MONTH)   +   1;
    	int   day   =   cal.get(Calendar.DATE); 
    	return new int []{year,month,day};
    }
    
    /**
	 * 将长时间类型转换成yyyy-MM-dd HH:mm:ss时间类型
	 * @param time
	 * @return
	 */
	public static String longToTime(long time){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date dt = new Date(time);  
		String sDateTime = sdf.format(dt);  //得到精确到秒的表示：2006-08-31 21:08:00
		return sDateTime;
	}
	/**
	 * 获取格林威治时间*/
    public static String getGelinData(){
    	Date data = new Date();
    	return data.toGMTString();
    	
    }
    /**
     * 获取当时间时间，格式为yyyyMMddHHmmss
     * @return nowTime
     * @author guoch
     */
    public static String getNowTime(){
    	String nowTime = null;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    	nowTime = sdf.format(new Date());
    	return nowTime;
    }
    
    /**
     * long型转化为String型时间格式yyyyMMddHHmmss
     * @param time
     * @return String time
     * @author guoch
     */
    public static String getTimeFromLong(long time){
    	SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddHHmmss");
		java.util.Date dt = new Date(time);  
		String sDateTime = sdf.format(dt);  //得到精确到秒的表示：20060831210800
		return sDateTime;
    }
	 
    
   
}