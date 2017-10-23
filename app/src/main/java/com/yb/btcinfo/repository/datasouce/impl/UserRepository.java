package com.yb.btcinfo.repository.datasouce.impl;


import com.yb.btcinfo.common.FApplication;
import com.yb.btcinfo.common.manager.ResourceManager;
import com.yb.btcinfo.repository.bean.User;
import com.yb.btcinfo.repository.datasouce.UseDataSource;
import com.yb.btcinfo.repository.local.LocalUserDataSource;
import com.yb.btcinfo.repository.net.UserCacheProviders;
import com.yb.btcinfo.repository.remote.RemoteUserDataSource;

import java.util.List;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.Reply;
import mvp.data.dispose.interactor.DefaultThreadProvider;
import mvp.data.dispose.interactor.ThreadExecutorHandler;

/**
 * Created by ericYang on 2017/5/18.
 * Email:eric.yang@huanmedia.com
 * what?
 */
@SuppressWarnings("unchecked")
public class UserRepository implements UseDataSource {
    private final DefaultThreadProvider mThreadHandler;
    private final UserCacheProviders mCacheProviders;
    private UseDataSource mRemoteDataSource;
    private UseDataSource mLocalDataSource;

    /**
     * create user repository
     *
     */
    public UserRepository() {
        this.mThreadHandler = ResourceManager.getInstance().getDefaultThreadProvider();
        this.mRemoteDataSource = new RemoteUserDataSource( ResourceManager.getInstance().getUserApiService());
        this.mLocalDataSource = new LocalUserDataSource(FApplication.getApplication());
        this.mCacheProviders = ResourceManager.getInstance().getUserCacheProviders();
    }

    public Observable<List<User>> users() {
        return mCacheProviders.getUsers(mRemoteDataSource.users(), new EvictProvider(false))
                .compose(ThreadExecutorHandler.toMain(mThreadHandler))
                .map(Reply::getData);
    }

    public Observable<User> user(int userId) {
        return mCacheProviders.getUser(mRemoteDataSource.user(userId), new DynamicKey(userId), new EvictDynamicKey(false))
                .compose(ThreadExecutorHandler.toMain(mThreadHandler))
                .map(Reply::getData);
    }
}
