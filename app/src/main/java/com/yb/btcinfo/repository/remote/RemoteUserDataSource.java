package com.yb.btcinfo.repository.remote;


import com.yb.btcinfo.repository.UseDataSource;
import com.yb.btcinfo.repository.bean.User;
import com.yb.btcinfo.repository.mapper.UserEntityDataMapper;
import com.yb.btcinfo.repository.net.UserApiService;

import java.util.List;

import io.reactivex.Observable;
import mvp.data.dispose.exception.UserNotFoundException;

/**
 * Created by ericYang on 2017/5/18.
 * Email:eric.yang@huanmedia.com
 * what?
 */
public class RemoteUserDataSource implements UseDataSource {
    private final UserApiService mRemoteApiService;
    private final UserEntityDataMapper mMapper;

    public RemoteUserDataSource(UserApiService remoteApiService) {
        this.mRemoteApiService=remoteApiService;
        this.mMapper = new UserEntityDataMapper();
    }

    public Observable<List<User>> users() {
        return mRemoteApiService.getUsers().map(listDataResponse -> {
            if (listDataResponse==null || listDataResponse.getResult()==null){
                throw new UserNotFoundException();
            }else {
               return mMapper.transform(listDataResponse.getResult());
            }
        });
    }

    public Observable<User> user(int userId) {
        return mRemoteApiService.getUser(userId).map(userEntityDataResponse -> {
            if (userEntityDataResponse==null || userEntityDataResponse.getResult()==null){
                throw  new UserNotFoundException();
            }else {
               return mMapper.transform(userEntityDataResponse.getResult());
            }
        });
    }
}
