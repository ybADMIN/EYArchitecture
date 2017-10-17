package com.yb.ilibray.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.List;

/**
 * @author MaTianyu
 * @date 2014-12-10
 */
public class AppUtil {

    /**
     * 调用系统分享
     */
    public static void shareToOtherApp(Context context, String title, String content, String dialogTitle ) {
        Intent intentItem = new Intent(Intent.ACTION_SEND);
        intentItem.setType("text/plain");
        intentItem.putExtra(Intent.EXTRA_SUBJECT, title);
        intentItem.putExtra(Intent.EXTRA_TEXT, content);
        intentItem.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intentItem, dialogTitle));
    }
    /**
     * need < uses-permission android:name =“android.permission.GET_TASKS” />
     * 判断是否前台运行
     */
    public static boolean isRunningForeground(Context context) {
      if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
          ActivityManager am = (ActivityManager) context
                  .getSystemService(Context.ACTIVITY_SERVICE);
          List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
          String packegName = context.getPackageName();
          if (appProcesses != null) {
              for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
                  if (runningAppProcessInfo.processName.equals(packegName)) {
                      int status = runningAppProcessInfo.importance;
//                      status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE//应用是否可见
                      if ( status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                          return true;
                      }
                  }
              }
          }
          return false;
      }else {
          ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
          List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
          if (taskList != null && !taskList.isEmpty()) {
              ComponentName componentName = taskList.get(0).topActivity;
              if (componentName != null && componentName.getPackageName().equals(context.getPackageName())) {
                  return true;
              }
          }
          return false;
      }
    }
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if(serviceList == null || serviceList.isEmpty())
            return false;
        for(int i = 0; i < serviceList.size(); i++) {
            if(serviceList.get(i).service.getClassName().equals(className) && TextUtils.equals(
                    serviceList.get(i).service.getPackageName(), context.getPackageName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
    /**
     * 获取App包 信息版本号
     * @param context
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }
    public static String getChannelInfo(Context context) {
        String metaValue = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo( context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = null;
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                metaValue = metaData.getString("UMENG_CHANNEL");
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return metaValue;
    }

}
