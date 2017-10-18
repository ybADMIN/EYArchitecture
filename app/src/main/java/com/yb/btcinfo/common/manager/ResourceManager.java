package com.yb.btcinfo.common.manager;

import android.content.Context;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.yb.btcinfo.BuildConfig;
import com.yb.btcinfo.common.FApplication;
import com.yb.btcinfo.common.navigation.Navigator;
import com.yb.btcinfo.repository.net.CacheProviders;
import com.yb.btcinfo.repository.net.RemoteApiService;
import com.yb.btcinfo.repository.net.UserApiService;
import com.yb.btcinfo.repository.net.UserCacheProviders;

import mvp.data.dispose.interactor.DefaultThreadProvider;

/**
 * Created by ericYang on 2017/5/24.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class ResourceManager {
    private static ResourceManager instance;
    private DefaultThreadProvider mDefaultThreadProvider;
    private RemoteApiService mApiService;
    private Navigator navigator;
    private CacheProviders mCacheProviders;
    private UserApiService mUserApiService;
    private UserCacheProviders mUserCacheProviders;

    private ResourceManager(Context context) {
        init(context);
    }

    private void init(Context context) {
        mDefaultThreadProvider = DefaultThreadProvider.getInstance();
        //创建API服务 及 API 缓存服务

        mApiService = RemoteApiService.Factory.createService();
        mUserApiService = UserApiService.Factory.createService();
        mCacheProviders = CacheProviders.Factory.createProviders(context);
        mUserCacheProviders = UserCacheProviders.Factory.createProviders(context);

        navigator = new Navigator();
        Logger.init("EYArch").logLevel(BuildConfig.DEBUG?LogLevel.FULL:LogLevel.NONE).hideThreadInfo().methodCount(0);
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            synchronized (ResourceManager.class) {
                if (instance == null) {
                    instance = new ResourceManager(FApplication.getApplication());
                }
            }
        }
        return instance;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    public DefaultThreadProvider getDefaultThreadProvider() {
        return mDefaultThreadProvider;
    }

    public RemoteApiService getDefaultApiService() {
        return mApiService;
    }
    public UserApiService getUserApiService() {
        return mUserApiService;
    }

    public CacheProviders getCacheProviders() {
        return mCacheProviders;
    }

    public UserCacheProviders getUserCacheProviders() {
        return mUserCacheProviders;
    }
}
