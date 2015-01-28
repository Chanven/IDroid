package com.andy.demo.base;

public class Constant {
	/** 连接超时时间*/
	public static final int CONNECTION_TIMEOUT = 20 * 1000;
	/** 读取数据超时时间*/
	public static final int READ_TIMEOUT = 30 * 1000;
	public static String format = "json";
	public static String clientType = "7";
	
	public static String VERSION = "";
	public static String ID = "";
	public static String IMEI = "";
	public static String IP = "";
	public static String MAC = "";
	public static String CONNECTTYPE = "";
	public static String SIGN = "";
	/**渠道id，在application初始化*/
	public static String CHANNELID = "";

	public static int m_screenW, m_screenH;
	/** 屏幕的逻辑密度 */
    public static float DENSITY = 1.0f;
    
    /**
     * Account type string.
     */
    public static final String ACCOUNT_TYPE = "com.andy.demo";
    /**
     * Authtoken type string.
     */
    public static final String AUTHTOKEN_TYPE ="com.andy.demo.authtoken";
    
    public static String WX_APP_ID = "wxf9caa6f564d72c8c";
}
