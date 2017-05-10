package com.andy.android.util;

import android.content.Context;
import java.lang.reflect.Field;

/**
 * 利用反射获取资源
 * @author chanven
 *
 */
public class ResourceReflect {
	private static final String TAG = "ResourceReflect";
	private static ResourceReflect mInstance;
	private Context mContext;
	private static Class idClass = null;
	private static Class drawableClass = null;
	private static Class layoutClass = null;
	private static Class animClass = null;
	private static Class styleClass = null;
	private static Class stringClass = null;
	private static Class arrayClass = null;

	private ResourceReflect(Context paramContext) {
		this.mContext = paramContext;
		try {
			drawableClass = Class.forName(this.mContext.getPackageName() + ".R$drawable");
		} catch (ClassNotFoundException localClassNotFoundException1) {
			DLog.e(TAG, localClassNotFoundException1.getMessage());
		}
		try {
			layoutClass = Class.forName(this.mContext.getPackageName() + ".R$layout");
		} catch (ClassNotFoundException localClassNotFoundException2) {
			DLog.e(TAG, localClassNotFoundException2.getMessage());
		}
		try {
			idClass = Class.forName(this.mContext.getPackageName() + ".R$id");
		} catch (ClassNotFoundException localClassNotFoundException3) {
			DLog.e(TAG, localClassNotFoundException3.getMessage());
		}
		try {
			animClass = Class.forName(this.mContext.getPackageName() + ".R$anim");
		} catch (ClassNotFoundException localClassNotFoundException4) {
			DLog.e(TAG, localClassNotFoundException4.getMessage());
		}
		try {
			styleClass = Class.forName(this.mContext.getPackageName() + ".R$style");
		} catch (ClassNotFoundException localClassNotFoundException5) {
			DLog.e(TAG, localClassNotFoundException5.getMessage());
		}
		try {
			stringClass = Class.forName(this.mContext.getPackageName() + ".R$string");
		} catch (ClassNotFoundException localClassNotFoundException6) {
			DLog.e(TAG, localClassNotFoundException6.getMessage());
		}
		try {
			arrayClass = Class.forName(this.mContext.getPackageName() + ".R$array");
		} catch (ClassNotFoundException localClassNotFoundException7) {
			DLog.e(TAG, localClassNotFoundException7.getMessage());
		}
	}

	public static ResourceReflect getInstance(Context paramContext) {
		if (mInstance == null)
			mInstance = new ResourceReflect(paramContext);
		return mInstance;
	}

	public int getAnimRes(String paramString) {
		return getRes(animClass, paramString);
	}

	public int getIdRes(String paramString) {
		return getRes(idClass, paramString);
	}

	public int getDrawbleRes(String paramString) {
		return getRes(drawableClass, paramString);
	}

	public int getLayoutRes(String paramString) {
		return getRes(layoutClass, paramString);
	}

	public int getStyleRes(String paramString) {
		return getRes(styleClass, paramString);
	}

	public int getStringRes(String paramString) {
		return getRes(stringClass, paramString);
	}

	public int getArrayRes(String paramString) {
		return getRes(arrayClass, paramString);
	}

	private int getRes(Class<?> paramClass, String paramString) {
		if (paramClass == null) {
			DLog.e(TAG, "getRes(null," + paramString + ")");
			throw new IllegalArgumentException("ResClass is not initialized.");
		}
		try {
			Field localField = paramClass.getField(paramString);
			int resId = localField.getInt(null);
			return resId;
		} catch (Exception localException) {
			DLog.e(TAG, "getRes(" + paramClass.getName() + ", " + paramString + ")");
			DLog.e(TAG,	"Error getting resource. Make sure you have copied all resources (res/) from SDK to your project. ");
			DLog.e(TAG, localException.getMessage());
		}
		return -1;
	}
}
