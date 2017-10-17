package com.yb.btcinfo.main.fragment;

import com.yb.btcinfo.main.model.IndexDataModel;
import mvp.view.LoadDataView;

import java.util.List;

/**
 * Created by ericYang on 2017/5/19.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public interface HomeView extends LoadDataView {
    void showIndexNewList(List<IndexDataModel> models);
}
