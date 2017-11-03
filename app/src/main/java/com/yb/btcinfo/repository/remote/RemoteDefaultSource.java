package com.yb.btcinfo.repository.remote;

import com.yb.btcinfo.main.datamodel.IndexDataModel;
import com.yb.btcinfo.main.datamodel.PlatformModel;
import com.yb.btcinfo.main.datamodel.mapper.IndexModelDataMapper;
import com.yb.btcinfo.main.datamodel.mapper.PlatformModelDataMapper;
import com.yb.btcinfo.platform.datamode.PlatformListMode;
import com.yb.btcinfo.platform.datamode.mapper.PlatformListModelDataMapper;
import com.yb.btcinfo.repository.datasouce.MainSource;
import com.yb.btcinfo.repository.net.RemoteApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ericYang on 2017/8/24.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class RemoteDefaultSource implements MainSource {
    private final RemoteApiService mRemoteApiService;

    public RemoteDefaultSource(RemoteApiService remoteApiService) {
        mRemoteApiService = remoteApiService;
    }

    @Override
    public Observable<List<IndexDataModel>> getIndexNews(String number) {
        return mRemoteApiService.getIndexNews(number).map(userEntityDataResponse -> {
            if (userEntityDataResponse==null || userEntityDataResponse.getResult()==null){
                return new ArrayList<IndexDataModel>();
            }else {
                return new IndexModelDataMapper().transform(userEntityDataResponse.getResult());
            }
        });
    }

    @Override
    public Observable<List<PlatformModel>> getSupportPlatform() {
        return mRemoteApiService.getSupportPlatform().map(listDataResponse -> {
            if (listDataResponse==null || listDataResponse.getResult()==null){
                return new ArrayList<PlatformModel>();
            }else {
                return new PlatformModelDataMapper().transform(listDataResponse.getResult());
            }
        });
    }

    @Override
    public Observable<List<PlatformListMode>> getPlatformNews(String platformId, String pageSize) {
        return mRemoteApiService.getPlatformNews(platformId,pageSize).map(listDataResponse -> {
            if (listDataResponse==null || listDataResponse.getResult()==null){
                return new ArrayList<PlatformListMode>();
            }else {
                return new PlatformListModelDataMapper().transform(listDataResponse.getResult());
            }
        });
    }

}
