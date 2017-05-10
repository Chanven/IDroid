package com.andy.demo.activity;

import android.app.Activity;

import com.andy.android.util.DLog;

import java.util.Stack;

/**
 * Activity管理器
 */
public class ActivityManager {
    private static Stack<Activity> activityStack;
    private static ActivityManager instance;

    private ActivityManager() {
    }

    public static ActivityManager getScreenManager() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    // 退出栈顶Activity
    public void popActivity(Activity activity) {
        if (activity != null) {
            // 在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
            activityStack.remove(activity);
        }
    }

    // 退出栈顶Activity，并关闭
    public void endActivity(Activity activity) {
        if (activity != null) {
            // 在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
            DLog.d(getClass().getSimpleName(), "end:" + activity);
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    // 获得当前栈顶Activity
    public Activity currentActivity() {
        Activity activity = null;
        if (null != activityStack && !activityStack.empty())
            activity = activityStack.lastElement();
        return activity;
    }

    // 将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    // 退出栈中所有Activity
    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }

    // 退出栈中所有Activity
    public void finishAllActivityExceptOne(Class cls) {
        while (!activityStack.empty()) {
            Activity activity = currentActivity();
            if (activity.getClass().equals(cls)) {
                popActivity(activity);
            } else {
                endActivity(activity);
            }
        }
    }

    public int getActivityCount() {
        int count = 0;
        if (activityStack != null) {
            count = activityStack.size();
        }
        return count;
    }

}
