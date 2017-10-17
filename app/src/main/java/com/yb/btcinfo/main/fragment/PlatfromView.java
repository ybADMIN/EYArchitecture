package com.yb.btcinfo.main.fragment;

import com.yb.btcinfo.main.model.PlatformModel;
import mvp.view.LoadDataView;

import java.util.List;

/**
 * Created by ericYang on 2017/5/19.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public interface PlatfromView extends LoadDataView {
    void showPlatfromList(List<PlatformModel> models);
}
