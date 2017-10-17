package com.yb.ilibray.utils.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo()!=null){
            if (NetworkInfo.State.CONNECTED==cm.getActiveNetworkInfo().getState()){//已连接到网络
                if (cm.getActiveNetworkInfo().getType()== ConnectivityManager.TYPE_WIFI){
//                    Intent intent1 = new Intent("com.huanmedia.yourchum.autoLogin");
//                    EventBus.getDefault().post(intent1);
                }else if (cm.getActiveNetworkInfo().getType()== ConnectivityManager.TYPE_MOBILE){
                    // 手机网络连接成功
//                    Intent intent1 = new Intent("com.huanmedia.yourchum.autoLogin");
//                    EventBus.getDefault().post(intent1);
                }
            }
        }else {//未连接到网络
            // 手机没有任何的网络
//            ToastUtil.cToast(context,"手机没有任何的网络");
        }
    }
}
