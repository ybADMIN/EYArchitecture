package com.yb.btcinfo.repository.local;

import android.content.Context;

import com.yb.btcinfo.repository.datasouce.UseDataSource;
import com.yb.btcinfo.repository.bean.User;

import java.util.List;

import io.reactivex.Observable;
import mvp.data.download.down.entity.DownloadEntity;

/**
 * Created by ericYang on 2017/5/18.
 * Email:eric.yang@huanmedia.com
 * 用户本地资源库 // FIXME: 2017/8/4 数据库功能没有加入
 */
public class LocalUserDataSource implements UseDataSource{
    private final Context mContext;

    public LocalUserDataSource(Context context) {
        this.mContext=context;
    }

    public Observable<List<User>> users() {
        return null;
    }

    public Observable<User> user(int userId) {
        return null;
    }

    public Observable<List<DownloadEntity>> getFilelist() {
        //no use
        return null;
    }
}
