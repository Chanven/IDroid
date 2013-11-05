package com.andy.android.util;

import java.lang.reflect.Method;

public class ReflectionHelper {
	public static Object invokeMethod(Object owner, String methodName, Object[] args) {
		Class ownerClass = owner.getClass();
        Class[] argsClass = (args != null)? new Class[args.length]: null;
        if(argsClass != null) {
        	for (int i = 0, j = args.length; i < j; i++) {
        		argsClass[i] = args[i].getClass();
        	}
        }
        try{
        	Method method = ownerClass.getMethod(methodName, argsClass);
        	return method.invoke(owner, args);
        }catch(Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }
}
