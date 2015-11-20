package com.andy.demo.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Camera;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebView;

import com.andy.demo.ApplicationEx;
import com.andy.demo.R;

public class DeviceUtils {

    public static long GetCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取程序自身的签名
     * @param context
     * @return
     */
    public static String getSign(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageinfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            String signResult = CommonUtils.getStringMD5(packageinfo.signatures[0].toCharsString());
            return signResult;
        } catch (NameNotFoundException e) {
        }
        return null;
    }

    /**
     * 取得手机的IMEI
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tmIMEI = tm.getDeviceId();
        if (tmIMEI == null)
            tmIMEI = "";
        return tmIMEI;
    }

    /**
     * 取得手机的IMSI
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tmIMSI = tm.getSubscriberId();
        if (tmIMSI == null)
            tmIMSI = "";
        return tmIMSI;
    }

    /**
     * 取得手机Mac地址
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取设备序列号
     * @return
     */
    public static String getSerialnum() {
        String serialnum = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
        } catch (Exception ignored) {
        }
        return serialnum;
    }

    /**
     * 获取ANDROID_ID
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        return "" +
               android.provider.Settings.Secure.getString(context.getContentResolver(),
                   android.provider.Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取手机上网类型 0 未知，1 wifi, 2 2G/3G
     * @param context
     * @return
     */
    public static String getConnectType(Context context) {
        int type = NetworkUtils.getNetworkType(context);
        String typeStr = "0";
        if (type == 2) {
            typeStr = "1";
        } else if (type == 1 || type == 0) {
            typeStr = "2";
        } else {
            typeStr = "0";
        }
        return typeStr;
    }

    /**
     * 取得手机本地IP地址
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() &&
                        InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }

        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest((getSign(ApplicationEx.app)).getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b: hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 获取userAgent
     * @param context
     * @return
     */
    public static String getUserAgent(Context context) {
        WebView wv = new WebView(context);
        return wv.getSettings().getUserAgentString();
    }

    /***
     * 获取手机型号
     * @param context
     * @return
     */
    public static String getModleType(Context context) {
        String mobileType = Build.MODEL;
        if (TextUtils.isEmpty(mobileType)) {
            return "";
        }
        if (!mobileType.contains("unknown")) {
            return mobileType;
        }
        String userAgent = getUserAgent(context);
        if (!TextUtils.isEmpty(userAgent)) {
            try {
                String resu = userAgent.substring(userAgent.indexOf("(") + 1, userAgent.indexOf(")"));
                String models = resu.substring(resu.lastIndexOf(";") + 2, resu.indexOf(" Build"));
                mobileType = models;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mobileType;
    }

    /***
     * 获取版本号
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode; // 版本号
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本名称
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        String version = "";
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName; // 版本名称
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 判断是否存在Sim卡
     * @return
     */
    public static boolean isExistSimCard(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = tm.getSimState();
        int netWorkType = tm.getNetworkType();
        if ((simState == TelephonyManager.SIM_STATE_ABSENT || simState == TelephonyManager.SIM_STATE_UNKNOWN) ||
            ((simState == TelephonyManager.SIM_STATE_READY) && netWorkType == 0)) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否存在SD卡
     * @return
     */
    public static boolean existSDcard() {
        String sdStatus = android.os.Environment.getExternalStorageState();
        if (sdStatus.equals(android.os.Environment.getExternalStorageState())) {
            return true;
        } else
            return false;
    }

    /**
     * 判断是否模拟器。如果返回TRUE，则当前是模拟器
     * 
     * @param context
     * @return
     */
    public static boolean isEmulator(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (imei != null && imei.equals("000000000000000")) {
                return true;
            }
            String host = android.os.Build.HOST;
            if (host != null && host.contains(context.getResources().getString(R.string.build_host))) {
                return true;
            }
            String brand = android.os.Build.BRAND;
            if (brand != null && brand.contains(context.getResources().getString(R.string.build_brand))) {
                return true;
            }
            return (Build.MODEL.equals(context.getResources().getString(R.string.sdk))) ||
                   (Build.MODEL.equals(context.getResources().getString(R.string.google_sdk)));
        } catch (Exception ioe) {
        }
        return false;
    }
    
    /**
     * new UUID
     * @return
     */
    public String getFlowPayClientInstallUUID(Context context) {
        String tmDevice, androidId;
        tmDevice = getIMEI(context);
        String fingerpaint = android.os.Build.FINGERPRINT;
        androidId =
                        "" +
                                        android.provider.Settings.Secure.getString(context.getContentResolver(),
                                            android.provider.Settings.Secure.ANDROID_ID);
        if (!TextUtils.isEmpty(tmDevice) && !TextUtils.isEmpty(fingerpaint)) {
            return new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) + fingerpaint.hashCode())
                            .toString();
        }
        if (!TextUtils.isEmpty(tmDevice)) {
            // create uuid by tmDevice
            return new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32)).toString();
        } else {
            if (!TextUtils.isEmpty(fingerpaint)) {
                // create uuid by fingerpaint
                return new UUID(androidId.hashCode(), ((long) fingerpaint.hashCode() << 32)).toString();
            } else {
                // create uuid by appsignature
                return new UUID(androidId.hashCode(), ((long) getSign(context).hashCode() << 32)).toString();
            }
        }
    }
    
    /**
     * 是否root
     * @return
     */
    public boolean isRoot() {
        boolean isRoot = false;
        try {
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())) {
                isRoot = false;
            } else {
                isRoot = true;
            }
        } catch (Exception e) {
            isRoot = false;
        }
        return isRoot;
    }
    
    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     * 
     * @param command 命令：String apkRoot="chmod 777 "+getPackageCodePath(); RootCommand(apkRoot);
     * @return 应用程序是/否获取Root权限
     */
    public boolean rootCommand(Context ctx, String command) {
        if (TextUtils.isEmpty(command)) {
            command = "chmod 777 " + ctx.getPackageName();
        }
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }
    
    /**
     * 判断是否有摄像头
     * @param cameraFacing Camera.CameraInfo.CAMERA_FACING_*
     * @return boolean
     */
    public boolean hasCameraFacing(int cameraFacing) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == cameraFacing) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCameraFront() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return hasCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else {
            return false;
        }
    }

    public boolean hasCameraBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return hasCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
        } else {
            return true;
        }
    }

    /* make a http_post */
    public static HttpPost makePost(String url, String header, List<NameValuePair> listParams) {
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            if (header != null && header.trim().length() > 0)
                httpPost.addHeader("Authorization", "Basic " + header);
            if (listParams != null && listParams.size() > 0)
                httpPost.setEntity(new UrlEncodedFormEntity(listParams, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            httpPost = null;
        }
        return httpPost;
    }

    /* get the response after post a httppost request */
    public static HttpResponse getResponse(HttpPost hp) throws IOException {
        HttpResponse hr = null;
        HttpParams params = new BasicHttpParams();
        // 设置连接和请求响应超时时间
        HttpConnectionParams.setConnectionTimeout(params, 20000);
        HttpConnectionParams.setSoTimeout(params, 20000);
        try {
            hr = new DefaultHttpClient(params).execute(hp);
        } catch (Exception e) {
            try {
                hr = new DefaultHttpClient(params).execute(hp);
            } catch (ClientProtocolException e1) {
                throw e1;
            } catch (IOException e1) {
                throw e1;
            }
        }
        return hr;
    }

}
