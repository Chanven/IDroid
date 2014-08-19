package com.andy.demo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.text.TextUtils;
import android.util.Log;

import com.andy.demo.BuildConfig;

public class CodeUtils {
	/**
	 * MD5算法名称
	 */
	private final static String ALGORITHM_MD5 = "MD5";

	/**
	 * SHA1算法名称
	 */
	private final static String ALGORITHM_SHA1 = "SHA1";

	/**
	 * HAMC-SHA1算法名称
	 */
	private final static String ALGORITHM_HMACSHA1 = "HmacSHA1";
	
	/**
	 * 使用MD5算法计算Hash
	 * @param source 源字符串
	 * @param charset 源字符串字符集
	 * @return MD5 Hash
	 */
	public static String md5Hash(String source, String charset) {
		if (TextUtils.isEmpty(source)) {
			return "";
		}
		try {
			return ByteFormat.toHex(md5Hash(source.getBytes(charset)));
		}
		catch (UnsupportedEncodingException ex) {
			return "";
		}
	}
	
	/**
	 * 使用MD5算法计算Hash
	 * @param source 源数据
	 * @return MD5 Hash
	 */
	public static byte[] md5Hash(byte[] source) {
		return digest(source, ALGORITHM_MD5);
	}

	/**
	 * 使用MD5算法计算文件Hash
	 * @param sourceFile 要计算的文件
	 * @return MD5 Hash
	 */
	public static String md5Hash(File sourceFile) {
		return ByteFormat.toHex(fileDigest(sourceFile, ALGORITHM_MD5));
	}
	
	/**
	 * 使用SHA1算法计算Hash
	 * @param source 源字符串
	 * @param charset 源字符串字符集
	 * @return SHA1 Hash
	 */
	public static String sha1Hash(String source, String charset) {
		if (TextUtils.isEmpty(source)) {
			return "";
		}
		try {
			return ByteFormat.toHex(sha1Hash(source.getBytes(charset)));
		}
		catch (UnsupportedEncodingException ex) {
			if (BuildConfig.DEBUG) {
			Log.d("sha1Hash", "sha1Hash([" + source + "], [" + charset + "])", ex);
			}
			return "";
		}
	}

	/**
	 * 使用SHA1算法计算Hash
	 * @param source 源数据
	 * @return SHA1 Hash
	 */
	public static byte[] sha1Hash(byte[] source) {
		return digest(source, ALGORITHM_SHA1);
	}

	/**
	 * 使用HMAC-SHA1算法计算签名
	 * @param source 源字符串
	 * @param key 使用的key字符串
	 * @param charset 源字符串和key字符串的字符集
	 * @return HMAC-SHA1 签名
	 */
	public static String hmacsha1(String source, String key, String charset) {
		if (TextUtils.isEmpty(source) || TextUtils.isEmpty(key)) {
			return "";
		}
		try {
			return ByteFormat.toHex(
					hmacsha1(source.getBytes(charset), key.getBytes(charset)));
		}
		catch (UnsupportedEncodingException ex) {
			if (BuildConfig.DEBUG) {
			Log.d("hmacsha1", "hmacsha1Hash([" + source + "], [" + charset + "])", ex);
			}
			return "";
		}
	}
	
    public static String hmacsha1(String data, String key) {
    	if (BuildConfig.DEBUG) {
    	Log.d("hmacsha1", "hmacsha1 data[" + data + "]");
    	}
        byte[] byteHMAC = null;
        try {
            Mac mac = Mac.getInstance(ALGORITHM_HMACSHA1);
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(), ALGORITHM_HMACSHA1);
            mac.init(spec);
            byteHMAC = mac.doFinal(data.getBytes());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException ignore) {
            // should never happen
        }
        if (BuildConfig.DEBUG) {
    	Log.d("hmacsha1", "hmacsha1 return[" + ByteFormat.toHex(byteHMAC) + "]");
        }
        return ByteFormat.toHex(byteHMAC);
    }
	
	/**
	 * 使用HMAC-SHA1算法计算签名
	 * @param source 源数据
	 * @param key 使用的key
	 * @return HMAC-SHA1 签名
	 */
	public static byte[] hmacsha1(byte[] source, byte[] key) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(key, ALGORITHM_HMACSHA1);
			Mac mac = Mac.getInstance(ALGORITHM_HMACSHA1);
			mac.init(signingKey);
			return mac.doFinal(source);
		}
		catch (NoSuchAlgorithmException ex) {
			if (BuildConfig.DEBUG) {
			Log.d("hmacsha1", "digest() - No such algorithm[" + ALGORITHM_HMACSHA1 + "]!", ex);
			}
			return new byte[0];
		}
		catch (InvalidKeyException ex) {
			if (BuildConfig.DEBUG) {
			Log.d("hmacsha1", "digest() - InvalidKey!", ex);
			}
			return new byte[0];
		}

	}
	
	
	/**
	 * 使用SHA1算法计算文件Hash
	 * @param sourceFile 要计算的文件
	 * @return SHA1 Hash
	 */
	public static String sha1Hash(File sourceFile) {
		return ByteFormat.toHex(fileDigest(sourceFile, ALGORITHM_SHA1));
	}
	
	/**
	 * BASE64 encode
	 * @param source 源字符串
	 * @param charset 源字符串字符集
	 * @return Base64编码结果
	 */
	public static String base64Encode(String source, String charset) {
		if (TextUtils.isEmpty(source)) {
			return "";
		}
		try {
			return base64Encode(source.getBytes(charset));
		}
		catch (UnsupportedEncodingException ex) {
			if (BuildConfig.DEBUG) {
			Log.d("base64Encode", "base64Encode([" + source + "], [" + charset + "])", ex);
			}
			return "";
		}
	}

	/**
	 * BASE64 encode
	 * @param data 要编码的数据
	 * @return Base64编码结果
	 */
	public static String base64Encode(byte[] data) {
		if (data == null) {
			return "";
		}	
		return new BASE64Encoder().encode(data);
	}


	/**
	 * url encode
	 * @param s 要编码的字符串
	 * @param charset 字符集
	 * @return 编码后的字符串
	 */
	public static String urlEncode(String s, String charset) {
		if (TextUtils.isEmpty(s)) {
			return "";
		}
		
		try {
			return URLEncoder.encode(s, charset);
		}
		catch (UnsupportedEncodingException ex) {
			if (BuildConfig.DEBUG) {
			Log.d("urlEncode", "urlEncode([" + s + "], [" + charset + "])", ex);
			}
		}
		return "";
	}
	
	/**
	 * url decode
	 * @param s 要解码的字符串
	 * @param charset 字符集
	 * @return 解码后的字符串
	 */
	public static String urlDecode(String s, String charset) {
		if (TextUtils.isEmpty(s)) {
			return "";
		}
		
		try {
			return URLDecoder.decode(s, charset);
		}
		catch (UnsupportedEncodingException ex) {
			if (BuildConfig.DEBUG) {
			Log.d("urlDecode", "urlDecode([" + s + "], [" + charset + "])", ex);
			}
		}
		return "";
	}

	/**
	 * 使用指定算法计算摘要
	 * @param source 源数据
	 * @param algorithm 摘要算法
	 * @return 摘要结果
	 */
	private static byte[] digest(byte[] source, String algorithm) {
		if (source == null) {
			return new byte[0];
		}
		
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(source);
			return md.digest();
		}
		catch (NoSuchAlgorithmException ex) {
			if (BuildConfig.DEBUG) {
			Log.d("digest", "digest() - No Such Algorithm[" + algorithm + "]", ex);
			}
			return new byte[0];
		}
	}

	/**
	 * 使用指定算法计算摘要
	 * @param source 源数据
	 * @param algorithm 摘要算法
	 * @return 摘要结果
	 */
	private static byte[] fileDigest(File file, String algorithm) {
		if (BuildConfig.DEBUG) {
		Log.d("fileDigest", "fileDigest() - file[" + file + "], algorithm[" + algorithm + "]");
		}

		if (file == null || file.length() == 0) {
			if (BuildConfig.DEBUG) {
			Log.d("fileDigest", "fileDigest() - file[" + file + "] is null or zero length, return byte[0]!");
			}
			return new byte[0];
		}

		long maxBufferSize = 1024 * 1024 * 256;
		try {
			FileInputStream in = new FileInputStream(file);
			FileChannel ch = in.getChannel();
			long fileLen = file.length();
			int digestUpdateCount = fileLen % maxBufferSize == 0 ? 
					(int)(fileLen / maxBufferSize) :
					(int)(fileLen / maxBufferSize) + 1;

			MessageDigest md = MessageDigest.getInstance(algorithm);
			if (BuildConfig.DEBUG) {
			Log.d("fileDigest", "fileDigest() - file.length=" + fileLen +
					  ", digestUpdateCount=" + digestUpdateCount);
			}

			long position = 0;
			for (int i = 0; i < digestUpdateCount; i++) {
				long size = file.length() - position;
				if (size > maxBufferSize) {
					size = maxBufferSize;
				}
				if (BuildConfig.DEBUG) {
				Log.d("fileDigest", "fileDigest() - map buffer[" + i + "] position=" +
						  position + ", size=" + size);
				}
				md.update(ch.map(FileChannel.MapMode.READ_ONLY, position, size));
				position += size;
			}

			byte[] digest = md.digest();

			if (BuildConfig.DEBUG) {
				Log.d("fileDigest", "fileDigest() - file[" + file + "], algorithm[" + algorithm +
						  "], digest[" + ByteFormat.toHex(digest) + "]");
			}
			
			return digest;
		}
		catch (NoSuchAlgorithmException ex) {
			if (BuildConfig.DEBUG) {
			Log.d("fileDigest", "digest() - No such algorithm[" + algorithm + "]!", ex);
			}
			return new byte[0];
		}
		catch (FileNotFoundException ex) {
			if (BuildConfig.DEBUG) {
			Log.d("fileDigest", "digest() - File[" + file.getAbsolutePath() + "] not found!", ex);
			}
			return new byte[0];
		}
		catch (IOException ex) {
			if (BuildConfig.DEBUG) {
			Log.d("fileDigest", "digest() - File[" + file.getAbsolutePath() + "] read error!", ex);
			}
			return new byte[0];
		}
	}
}
