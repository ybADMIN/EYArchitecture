package com.yb.btcinfo.platform;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yb.btcinfo.R;
import com.yb.btcinfo.common.BaseActivity;
import com.yb.btcinfo.main.datamodel.PlatformModel;
import com.yb.btcinfo.platform.datamode.PlatformListMode;
import com.yb.ilibray.utils.DisplayUtil;
import com.yb.ilibray.utils.DividerLinearDecorationItme;
import com.yb.ilibray.utils.data.assist.Check;

import java.util.List;

import butterknife.BindView;

public class PlatformMsgActivity extends BaseActivity<PlatformMsgPresenter> implements PlatformMsgView{

    @BindView(R.id.platform_rv)
    RecyclerView mPlatformRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_platform_msg;
    }


    @Override
    protected void initView() {
        mPlatformRv.addItemDecoration(new DividerLinearDecorationItme(this, LinearLayoutManager.HORIZONTAL, DisplayUtil.dip2px(this,0.5f), ContextCompat.getColor(this,R.color.gray_B7C8CF)));
        mPlatformRv.setAdapter(new BaseQuickAdapter<PlatformListMode, BaseViewHolder>(R.layout.item_platform_list) {
            @Override
            protected void convert(BaseViewHolder helper, PlatformListMode item) {
                helper.setText(R.id.item_platform_title_tv, Check.checkReplace(item.getTitle()))
                        .setText(R.id.item_platform_time_tv, Check.checkReplace(item.getTime()))
                        .setText(R.id.item_platform_url_tv,Check.checkReplace(item.getUrl()));
            }
        });
    }

    @Override
    protected void initData() {
        PlatformModel model =getIntent().getParcelableExtra("platform");
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(model.getName());
        }
        getBasePresenter().getPlatformNews(model.getId()+"");
    }

    @Override
    public void onActionBarClick(View view) {

    }

    @Override
    protected View getTitlebarView() {
        return findViewById(R.id.toolbar_content);
    }

    @Override
    protected void initConfig() {
        super.initConfig();
        getSimpleToolBar().getToolbar().setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showRetry() {

    }

    @Override
    public void hideRetry() {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public Context context() {
        return this;
    }


    @Override
    public void showPlatformNews(List<PlatformListMode> models) {
        if (models!=null){
            BaseQuickAdapter adapter = ((BaseQuickAdapter) mPlatformRv.getAdapter());
            if (adapter!=null)
            adapter.setNewData(models);
        }
    }
}
