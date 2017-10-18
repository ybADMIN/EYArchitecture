package com.yb.btcinfo.common;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.gyf.barlibrary.OSUtils;
import com.yb.btcinfo.R;
import com.yb.btcinfo.common.manager.ActivitManager;
import com.yb.btcinfo.common.manager.ResourceManager;
import com.yb.btcinfo.common.navigation.Navigator;
import com.yb.btcinfo.common.widget.SimpleToolBar;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import mvp.presenter.Presenter;
import mvp.presenter.PresenterBulide;
import mvp.view.BaseView;


/**
 * Base {@link android.app.Activity} class for every Activity in this application.
 */
public abstract class BaseActivity<P extends Presenter> extends AppCompatActivity implements SimpleToolBar.OnActionBarClick {
    P mBasePresenter;
    Navigator navigator = ResourceManager.getInstance().getNavigator();
    private boolean mFirstLoad = true;
    private SimpleToolBar mSimpleToolBar;
    public ImmersionBar mImmersionBar;
    private static final String NAVIGATIONBAR_IS_MIN = "navigationbar_is_min";
    private Unbinder mUnbinder;

    public boolean isImmersionBarEnabled() {
        return true;
    }

    /**
     * 华为手机隐藏导航栏监听
     */
    private ContentObserver mNavigationStatusObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            int navigationBarIsMin = Settings.System.getInt(getContentResolver(),
                    NAVIGATIONBAR_IS_MIN, 0);
            if (navigationBarIsMin == 1) {
                //导航键隐藏了
                mImmersionBar.transparentNavigationBar().init();
            } else {
                //导航键显示了
                mImmersionBar.navigationBarColor(android.R.color.black) //隐藏前导航栏的颜色
                        .fullScreen(false)
                        .init();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitManager.getAppManager().addActivity(this);
        if (0 != getLayoutId()) {
            setContentView(getLayoutId());
        }
        setPresenter();
        mUnbinder = ButterKnife.bind(this);
        if (OSUtils.isEMUI3_1() && isImmersionBarEnabled()) {
            //第一种
            getContentResolver().registerContentObserver(Settings.System.getUriFor
                    (NAVIGATIONBAR_IS_MIN), true, mNavigationStatusObserver);
            //第二种,禁止对导航栏的设置
            //mImmersionBar.navigationBarEnable(false).init();
        }
    }

    protected abstract int getLayoutId();


    /**
     * 初始化界面
     */
    protected void initView() {
    }
    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * 初始化配置信息
     */
    protected void initConfig() {
        if (isSimpleToolbarEnable()){
            mSimpleToolBar = new SimpleToolBar(this);
            mSimpleToolBar.setClick(this);
        }

        if (isImmersionBarEnabled()) {
            statusBarConfig();
        }
    }

    protected ImmersionBar defaultBarConfig() {
        return ImmersionBar.with(this)
                .transparentStatusBar()
                .navigationBarEnable(false)
                .statusBarColor(R.color.colorPrimaryDark);
    }
    protected View getTitlebarView(){
        if (mSimpleToolBar!=null){
            return mSimpleToolBar.getToolbarContent();
        }
        return null;
    }
    protected void statusBarConfig() {
        mImmersionBar = defaultBarConfig();
        if (getTitlebarView() != null) {
            mImmersionBar.titleBar(getTitlebarView());
        }
        mImmersionBar.init();
    }


    public SimpleToolBar getSimpleToolBar() {
        return mSimpleToolBar;
    }

    public P getBasePresenter() {
        return mBasePresenter;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivitManager.getAppManager().removeActivity(this);
        if (mUnbinder != null)
            mUnbinder.unbind();
        if (mImmersionBar != null)
            mImmersionBar.destroy();

        if (getBasePresenter() != null) {//回收Presenter
            getBasePresenter().destroy();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getBasePresenter() != null) {
            getBasePresenter().resume();
        }
        if (mFirstLoad) {
            mFirstLoad = false;
            initConfig();
            initView();
            initData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getBasePresenter() != null) {
            getBasePresenter().pause();
        }
    }

    @SuppressWarnings("unchecked")
    public void setPresenter() {

        try {
            mBasePresenter = PresenterBulide.newPresenterInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (mBasePresenter!=null){
                mBasePresenter.setView((BaseView) this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSimpleToolbarEnable() {
        return true;
    }
}
