package com.yb.btcinfo.repository.datasouce;

import com.yb.btcinfo.main.datamodel.IndexDataModel;
import com.yb.btcinfo.main.datamodel.PlatformModel;
import com.yb.btcinfo.platform.datamode.PlatformListMode;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ericYang on 2017/8/24.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public interface MainSource extends DataSource {
    Observable<List<IndexDataModel>> getIndexNews(String number);
    Observable<List<PlatformModel>> getSupportPlatform();
    Observable<List<PlatformListMode>> getPlatformNews(String platformId, String pageSize);
}
