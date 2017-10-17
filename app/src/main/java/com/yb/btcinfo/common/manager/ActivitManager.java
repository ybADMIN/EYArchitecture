package com.yb.btcinfo.common.manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * Created by yb on 2016/6/1.
 */
public class ActivitManager {

    /**
     * 应用程序Activity管理类：用于Activity管理和应用程序退出
     *
     * @author liux (http://my.oschina.net/liux)
     * @version 1.0
     * @created 2012-3-21
     */

    private static Stack<Activity> activityStack;
    private static ActivitManager instance;

    private ActivitManager() {
    }

    /**
     * 单一实例
     */
    public static ActivitManager getAppManager() {
        if (instance == null) {
            instance = new ActivitManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 重自定义堆栈中移除Acitvity但不销毁
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 结束指定类名的Activity
     */
    @SuppressWarnings("unchecked")
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 关闭所有相同的
     *
     * @param cls 需要关闭的Activity
     */
    @SuppressWarnings("unchecked")
    public void finishAlikeActivity(Class<?> cls) {
        Stack<Activity> temp = (Stack<Activity>) activityStack.clone();
        for (Activity activity : temp) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
        temp.clear();
    }

    /**
     * 结束指定类名的Activity
     */
    public boolean existsActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public void finishNotSpecifiedActivity(Class<?> cls) {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i) && !activityStack.get(i).getClass().equals(cls)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public void finishNotSpecifiedActivity(Class<?>[] cls) {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) continue;
            boolean canFinish = true;

            for (Class<?> c : cls) {
                if (activityStack.get(i).getClass().equals(c)) {
                    canFinish = false;
                }
            }
            if (canFinish)
                activityStack.get(i).finish();
        }
        activityStack.clear();
    }

    public int activietyCounts() {
        if (null != activityStack) {
            return activityStack.size();
        }
        return 0;
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }
}
