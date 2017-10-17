package com.yb.btcinfo.repository.impl;


import android.content.Context;

import com.yb.btcinfo.repository.UseDataSource;
import com.yb.btcinfo.repository.bean.User;
import com.yb.btcinfo.repository.local.LocalUserDataSource;
import com.yb.btcinfo.repository.net.UserApiService;
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
     * @param context        context
     * @param apiService     remote api
     * @param cacheProviders cache providers
     * @param threadHandler  default thread dispatch
     */
    public UserRepository(Context context, UserApiService apiService, UserCacheProviders cacheProviders, DefaultThreadProvider threadHandler) {
        this.mThreadHandler = threadHandler;
        this.mRemoteDataSource = new RemoteUserDataSource(apiService);
        this.mLocalDataSource = new LocalUserDataSource(context);
        this.mCacheProviders = cacheProviders;
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
