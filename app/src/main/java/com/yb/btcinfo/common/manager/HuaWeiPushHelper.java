package com.yb.btcinfo.common.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.yb.btcinfo.common.service.HuaweiPushRevicer;
import com.yb.ilibray.utils.data.assist.Check;

import java.lang.reflect.Method;

/**
 * Created by ericYang on 2017/9/13.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class HuaWeiPushHelper {
    private static final int REQUEST_HMS_RESOLVE_ERROR = 1000;
    public static final String EXTRA_RESULT = "intent.extra.RESULT";
    private final Activity mActivity;
    //华为推送
    private HuaweiApiClient mHuaweiClient;
    public HuaWeiPushHelper(Activity activity) {
        this.mActivity = activity;
        initHuaweiPush();
    }
    /**
     * 初始化华为推送
     */
    private void initHuaweiPush() {
        mHuaweiClient = new HuaweiApiClient.Builder(mActivity)
                .addApi(HuaweiPush.PUSH_API)
                .addConnectionCallbacks(new HuaweiApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected() {
                        Log.i(HuaweiPushRevicer.TAG, "HuaweiApiClient 连接成功");
                        getPushStatus();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (!mActivity.isDestroyed() && !mActivity.isFinishing()) {
                                mHuaweiClient.connect();
                            }
                        }
                    }
                })
                .addOnConnectionFailedListener(connectionResult -> {
                    if(HuaweiApiAvailability.getInstance().isUserResolvableError(connectionResult.getErrorCode())) {
                        final int errorCode = connectionResult.getErrorCode();
                        new Handler(mActivity.getMainLooper()).post(() -> {
                            // 此方法必须在主线程调用, xxxxxx.this 为当前界面的activity
                            HuaweiApiAvailability.getInstance().resolveError(mActivity, errorCode, REQUEST_HMS_RESOLVE_ERROR);
                        });
                    }
                })
                .build();
        mHuaweiClient.connect();
    }
    public static boolean suppertPush(){
        int emuiApiLevel = 0;
        try {
            Class cls = Class.forName("android.os.SystemProperties");
            Method method = cls.getDeclaredMethod("get", String.class);
            emuiApiLevel = Integer.parseInt((String) method.invoke(cls, "ro.build.hw_emui_api_level"));
        } catch (Exception e) {
            e.printStackTrace();
        }
       return emuiApiLevel>=9;
    }

    public void errorResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_HMS_RESOLVE_ERROR) {
            if(resultCode == Activity.RESULT_OK) {

                int result = data.getIntExtra(EXTRA_RESULT, 0);

                if(result == ConnectionResult.SUCCESS) {
                    Log.i(HuaweiPushRevicer.TAG, "错误成功解决");
                    if (!mHuaweiClient.isConnecting() && !mHuaweiClient.isConnected()) {
                        mHuaweiClient.connect();
                    }
                } else if(result == ConnectionResult.CANCELED) {
                    Log.i(HuaweiPushRevicer.TAG, "解决错误过程被用户取消");
                } else if(result == ConnectionResult.INTERNAL_ERROR) {
                    Log.i(HuaweiPushRevicer.TAG, "发生内部错误，重试可以解决");
                    //开发者可以在此处重试连接华为移动服务等操作，导致失败的原因可能是网络原因等
                } else {
                    Log.i(HuaweiPushRevicer.TAG, "未知返回码");
                }
            } else {
                Log.i(HuaweiPushRevicer.TAG, "调用解决方案发生错误");
            }
        }
    }
    public void getTokenAsyn() {
        if(!mHuaweiClient.isConnected()) {
            Log.i(HuaweiPushRevicer.TAG, "获取token失败，原因：HuaweiApiClient未连接");
            mHuaweiClient.connect();
            return;
        }

        Log.i(HuaweiPushRevicer.TAG, "异步接口获取push token");
        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(mHuaweiClient);
        tokenResult.setResultCallback(result ->
                Log.i(HuaweiPushRevicer.TAG, "异步接口Token:"+ Check.checkReplace(result.getTokenRes().getToken())));
    }
    private void getPushStatus() {
        if(!mHuaweiClient.isConnected()) {
            Log.i(HuaweiPushRevicer.TAG, "获取PUSH连接状态失败，原因：HuaweiApiClient未连接");
            mHuaweiClient.connect();
            return;
        }
        //需要在子线程中调用函数
        new Thread() {
            public void run() {
                Log.i(HuaweiPushRevicer.TAG, "开始获取PUSH连接状态");
                HuaweiPush.HuaweiPushApi.getPushState(mHuaweiClient);
                // 状态结果通过广播返回
            }
        }.start();
    }
    public void destory() {
        if (mHuaweiClient!=null)
        mHuaweiClient.disconnect();
    }
}
