package com.andy.demo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class FileUtils {
    
    /**
     * 获取文件的MD5检验码
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        String value = "null";
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = in.read(bytes, 0, 1024)) != -1) {
                md5.update(bytes, 0, len);
            }
            // BigInteger bi = new BigInteger(1, md5.digest());
            // value = bi.toString(16).toUpperCase();
            value = byteArrayToHexString(md5.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
    
    /**
     * 转换字节数组为16进制字串
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString().toUpperCase();
    }

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
                    "E", "F"};

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

}
