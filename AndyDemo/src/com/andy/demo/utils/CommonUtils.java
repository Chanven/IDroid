package com.andy.demo.utils;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class CommonUtils {
    
    /**
     * 显示软键盘
     * @param context
     * @param view
     */
    public static void showKeyBoard(Context context, View view) {
        try {
            InputMethodManager imm = ((InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE));
            imm.showSoftInput(view, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏软键盘
     * @param context
     */
    public static void hideKeyboard(Context context) {
        try {
            InputMethodManager imm = ((InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE));
            if (((Activity) context).getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
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
	
	/**构建唯一标识*/
	public static String buildTransaction(final String type) {
        return (type == null)? String.valueOf(System.currentTimeMillis()): type + System.currentTimeMillis();
    }
	
	/**获取“账号与同步”中指定类型的账号数据*/
    public static String getAccount(Context context,String accountType){
        AccountManager mAccountManager = AccountManager.get(context);
        Account[] account = mAccountManager.getAccountsByType(accountType);
        String accountString="";
        for (int i = 0; i < account.length; i++) {
            accountString = accountString+"    "+account[i] +mAccountManager.getPassword(account[i]);
        }
        return accountString;
    }
    
    /**
     * 获取字符串的MD5检验码
     * @param str
     * @return
     */
    public static String getStringMD5(String str) {
        String value = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes());
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    
    // 获取AndroidManifest.xml的<meta-data>节点数据
    public static String getMetaDataNodeString(Context c, String nodeString) {
        String result = "";
        try {
            ApplicationInfo aInfo =
                            c.getPackageManager().getApplicationInfo(c.getPackageName(), PackageManager.GET_META_DATA);
            result = aInfo.metaData.getString(nodeString);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            result = "";
        }
        return result;
    }

    // 获取AndroidManifest.xml的<meta-data>节点数据（Int类型）
    public static int getMetaDataNodeInt(Context c, String nodeString) {
        int result = 1;
        try {
            ApplicationInfo aInfo =
                            c.getPackageManager().getApplicationInfo(c.getPackageName(), PackageManager.GET_META_DATA);
            Object object = aInfo.metaData.get(nodeString);
            if (object instanceof Integer) {
                result = (Integer) object;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 匹配手机号码
     * @param num 传入号码
     * @return
     */
    public static boolean matchMobilNo(String num) {
        if (TextUtils.isEmpty(num)) {
            return false;
        }
        String reg = "^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[0,6,7])|(18[0-9]))\\d{8}$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(num);
        boolean flag = matcher.matches();
        return flag;
    }
    
    /**
     * 去分隔符与前缀
     * @param num 传入号码
     * @return
     */
    public static String normalizationPhoneNum(String num) {
        String phoneNum = "";
        if (!TextUtils.isEmpty(num)) {
            phoneNum = num.trim().replaceAll(" ", "");
            phoneNum = phoneNum.replaceAll("-", "");
            if (phoneNum.startsWith("+86")) {
                phoneNum = phoneNum.replaceFirst("[+]{1}86", "");
            }
            if (phoneNum.startsWith("12593") || phoneNum.startsWith("17900") || phoneNum.startsWith("17909") ||
                phoneNum.startsWith("17911") || phoneNum.startsWith("17951")) {
                phoneNum = phoneNum.replaceFirst("12593|17900|17909|17911|17951", "");
            }
        }
        return phoneNum;
    }
    
    /**
     * 手机号码判断
     * @param num 传入号码
     * @return
     */
    public static boolean isCellPhoneNumber(String num) {
        String str = normalizationPhoneNum(num);
        boolean flag = matchMobilNo(str);
        return flag;
    }
    
    /**
     * 根据手机号查询本地联系人姓名
     * @param context
     * @param phone
     * @return
     */
    public static String getNameByPhone(Context context, String phone) {
        String name = phone;
        if (!TextUtils.isEmpty(phone) && CommonUtils.isCellPhoneNumber(phone)) {
            try {
                ContentResolver resolver = context.getContentResolver();
                Uri uri = Uri.withAppendedPath(Phone.CONTENT_FILTER_URI, phone);
                Cursor cursor = resolver.query(uri, null, null, null, Phone.DISPLAY_NAME + " desc");
                if (cursor != null) {
                    cursor.moveToFirst();
                    if (cursor.getCount() > 0) {
                        try {
                            name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
                        } catch (Exception e) {
                        }
                    }
                    cursor.close();
                }
            } catch (Exception e) {
            }
        } else {
            if (name == null) {
                name = "";
            }
        }
        return name;
    }
    
    /**
     * 判断应用是否安装
     * @param context
     * @param appPkgName
     * @return
     */
    public static boolean appIsInstalled(Context context, String appPkgName) {
        PackageInfo pInfo = null;
        boolean flag = true;
        try {
            pInfo = context.getPackageManager().getPackageInfo(appPkgName, 0);
        } catch (NameNotFoundException e) {
            flag = false;
        }
        return flag & (pInfo != null);
    }
    
    /**
     * 安装APK文件
     * @return
     */
    public static void installApk(Context context, String path, String name) {
        if (path == null || name == null) {
            return;
        }
        File apkfile = new File(path, name);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        context.startActivity(i);
    }
    
    /**
     * 将浮点型数据转换为百分数
     * @param numberFloat 需要转换的浮点型数据
     * @return
     */
    public static String transformFloatToPercent(float numberFloat) {
        NumberFormat numberFormat = java.text.NumberFormat.getPercentInstance();
        numberFormat.setMaximumIntegerDigits(2);// 小数点前保留2位
        numberFormat.setMinimumFractionDigits(0);// 小数点后保留0位
        String percentResult = numberFormat.format(numberFloat);
        return percentResult;
    }

    /**
     * 将百分数转换为浮点型数据
     * @param formatString 需要转换的百分数
     * @return
     */
    public static float transformPercentToFloat(String formatString) {
        float floatResult = Float.valueOf((formatString.substring(0, formatString.indexOf("%")))) / 100;
        return floatResult;
    }
}
