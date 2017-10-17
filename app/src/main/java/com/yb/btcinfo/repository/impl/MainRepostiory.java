package com.yb.btcinfo.repository.impl;

import com.yb.btcinfo.main.model.IndexDataModel;
import com.yb.btcinfo.main.model.PlatformModel;
import com.yb.btcinfo.platform.mode.PlatformListMode;
import com.yb.btcinfo.repository.DataSource;
import com.yb.btcinfo.repository.net.CacheProviders;
import com.yb.btcinfo.repository.net.RemoteApiService;
import com.yb.btcinfo.repository.remote.RemoteDefaultSource;

import java.util.List;

import io.reactivex.Observable;
import mvp.data.dispose.interactor.DefaultThreadProvider;
import mvp.data.dispose.interactor.ThreadExecutorHandler;

/**
 * Created by ericYang on 2017/8/24.
 * Email:eric.yang@huanmedia.com
 * 默认资源库没有数据库缓存
 */

public class MainRepostiory implements DataSource {
    private final DefaultThreadProvider mThreadProvider;
    private final CacheProviders mCacheProviders;
    private RemoteDefaultSource mRemoteDataSource;

    /**
     * create user repository
     *
     * @param apiService     remote api
     * @param cacheProviders cache providers
     * @param threadProvider  default thread dispatch
     */
    public MainRepostiory(RemoteApiService apiService, CacheProviders cacheProviders, DefaultThreadProvider threadProvider) {
        this.mThreadProvider = threadProvider;
        this.mRemoteDataSource = new RemoteDefaultSource(apiService);
        this.mCacheProviders = cacheProviders;
    }

    /**
     * 获取首页列表数据
     * @param number
     * @return
     */
    public Observable<List<IndexDataModel>> getIndexNews(String number) {
        return mRemoteDataSource.getIndexNews(number)
                .compose(ThreadExecutorHandler.toMain(mThreadProvider))
                .map(indexDataModels -> indexDataModels);

    }

    /**
     * 获取平台列表
     * @return
     */
    public Observable<List<PlatformModel>> getSupportPlatform(){
        return mRemoteDataSource.getSupportPlatform().compose(ThreadExecutorHandler.toMain(mThreadProvider))
                .map(indexDataModels -> indexDataModels);
    } 

    /**
     * 获取单个平台数据
     * @return
     */
    public Observable<List<PlatformListMode>> getPlatformNews(String platformId,String pageSize){
        return mRemoteDataSource.getPlatformNews(platformId,pageSize)
                .compose(ThreadExecutorHandler.toMain(mThreadProvider))
                .map(indexDataModels -> indexDataModels);
    }

}
