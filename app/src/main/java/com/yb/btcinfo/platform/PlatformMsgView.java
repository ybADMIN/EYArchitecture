package com.yb.btcinfo.platform;

import com.yb.btcinfo.platform.datamode.PlatformListMode;
import mvp.view.LoadDataView;

import java.util.List;

/**
 * Created by ericYang on 2017/5/19.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public interface PlatformMsgView extends LoadDataView {
    void showPlatformNews(List<PlatformListMode> models);
}
