package com.yb.btcinfo.common;

import android.app.Application;
import android.content.Context;

/**
 * Created by ericYang on 2017/5/18.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class FApplication  extends Application{
    private static Application mApplication;
    public static final String TAG = "FApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication=this;
    }
    public static Application getApplication() {
        return mApplication;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

}
