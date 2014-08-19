package com.andy.demo.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {
	private static Gson mGson = new Gson();
	
	public static synchronized <T> String toJsonString(Object src, TypeToken<T> typeToken){
		try {
			return mGson.toJson(src, typeToken.getType());
		} catch(Exception e) {
			return null;
		}
	}
	
	public static synchronized String toJsonString(Object src) {
		try {
			return mGson.toJson(src);
		} catch(Exception e) {
			return null;
		}
	}
	
	public static synchronized <T> T fromJsonString(String jsonString, TypeToken<T> typeToken) {
		try {
			return (T)mGson.fromJson(jsonString, typeToken.getType());
		} catch(Exception e) {
			return null;
		}
	}
	
	public static synchronized <T> T fromJsonString(String jsonString, Class<T> objClass) {
		try {
			return mGson.fromJson(jsonString, objClass);
		} catch(Exception e) {
			return null;
		}
	}
	public static synchronized <T> T fromJsonString2(String jsonString, Class<T> objClass) throws Exception {
		try {
			return mGson.fromJson(jsonString, objClass);
		} catch(Exception e) {
			throw e;
		}
	}
}
