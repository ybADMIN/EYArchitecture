package com.yb.btcinfo.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.widget.MsgView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yb.btcinfo.R;
import com.yb.btcinfo.common.BaseActivity;
import com.yb.btcinfo.common.manager.HuaWeiPushHelper;
import com.yb.btcinfo.common.manager.PushServiceManager;
import com.yb.btcinfo.main.fragment.HomeFragment;
import com.yb.btcinfo.main.fragment.HomeFragment.HomeFListener;
import com.yb.btcinfo.main.fragment.MyFragment;
import com.yb.btcinfo.main.fragment.PlatformFragment;
import com.yb.btcinfo.main.datamodel.TabMode;

import java.util.ArrayList;

import butterknife.BindView;


public class MainActivity extends BaseActivity implements HomeFListener, PlatformFragment.PlatfromFListener {
    private String[] titles = {"公告", "平台", "我的"};
    private int[] iconUnselectIds = {
            R.drawable.news_normal,
            R.drawable.order_normal, R.drawable.my_normal};
    private int[] iconSelectIds = {
            R.drawable.news_selected,
            R.drawable.order_selected, R.drawable.my_selected};
    @BindView(R.id.tl_1)
    CommonTabLayout mCommonTabLayout;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> tabs = new ArrayList<>();
    private RxPermissions mRxPrmissions;
    private HuaWeiPushHelper mHuaweiPushHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPushService();
//        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);//启动推送服务
        mRxPrmissions = new RxPermissions(this);
//        addFragmentAndShow(R.id.content, HomeFragment.newInstance(), "home");
    }

    private void initPushService() {
        if (PushServiceManager.getSystem().equals(PushServiceManager.SYS_EMUI) && HuaWeiPushHelper.suppertPush()){
                //创建华为移动服务client实例用以使用华为push服务
                //需要指定api为HuaweiId.PUSH_API
                //连接回调以及连接失败监听
            mHuaweiPushHelper=new HuaWeiPushHelper(this);

        }else {
            PushServiceManager.getInstance(getApplication()).initPushService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHuaweiPushHelper!=null){
            mHuaweiPushHelper.destory();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initConfig() {
        super.initConfig();
        getSimpleToolBar()
                .centerBtn("币先手")
                .getLiftBtn().setVisibility(View.GONE);
    }

    private void addFragmentAndShow(int content, Fragment home, String tag) {
        addFragmentAndShow(content, home, tag, false);
    }

    @SuppressLint("RestrictedApi")
    private void addFragmentAndShow(int content, Fragment home, String tag, boolean isAddBack) {
        FragmentTransaction fmanager = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
                Fragment oledFragment = getSupportFragmentManager().getFragments().get(i);
                if (oledFragment != null && !oledFragment.isHidden() && !oledFragment.equals(fragment))
                    fmanager.hide(oledFragment);
            }
        }
        if (isAddBack) {
            if (fragment != null) {
                if (fragment.isHidden())
                    fmanager.show(fragment).commit();
            } else {
                fmanager.add(content, home, tag).addToBackStack(tag).show(home).commit();
            }
        } else {
            if (fragment != null) {
                if (fragment.isHidden())
                    fmanager.show(fragment).commit();
            } else {
                fmanager.add(content, home, tag).show(home).commit();
            }
        }

    }
//    addFragmentAndShow(R.id.content, HomeFragment.newInstance(),"home");

    @Override
    public void homeAction(Uri uri) {

    }

    @Override
    public void platfromAction(Uri uri) {

    }

    @Override
    protected void initData() {
        for (String title : titles) {
            if (title.equals("公告")) {
                fragments.add(HomeFragment.newInstance());
            } else if (title.equals("平台")) {
                fragments.add(PlatformFragment.newInstance());
            } else
                fragments.add(MyFragment.newInstance());
        }

        for (int i = 0; i < titles.length; i++) {
            tabs.add(new TabMode(titles[i], iconSelectIds[i], iconUnselectIds[i]));
        }
        mCommonTabLayout.setTabData(tabs, this, R.id.content, fragments);
        mCommonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                for (int i = 0; i < fragments.size(); i++) {
                    if (fragments.get(i).getUserVisibleHint()) {
                        fragments.get(i).setUserVisibleHint(false);
                    }
                }
                fragments.get(position).setUserVisibleHint(true);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        //小红点颜色和边框设置
        MsgView msg1 = mCommonTabLayout.getMsgView(1);
//        setMsgViewMargin(msg1);
        MsgView msg2 = mCommonTabLayout.getMsgView(2);
//        setMsgViewMargin(msg2);
        MsgView msg3 = mCommonTabLayout.getMsgView(3);
//        setMsgViewMargin(msg3);

        msg1.setBackgroundColor(ContextCompat.getColor(this, R.color.red_FE2E2E));
        msg1.setStrokeWidth(0);
        msg1.setTextSize(10);
        msg1.setTypeface(Typeface.DEFAULT_BOLD);
        msg2.setBackgroundColor(ContextCompat.getColor(this, R.color.red_FE2E2E));
        msg2.setStrokeWidth(0);
        msg2.setTextSize(10);
        msg2.setTypeface(Typeface.DEFAULT_BOLD);
        msg3.setBackgroundColor(ContextCompat.getColor(this, R.color.red_FE2E2E));
        msg3.setStrokeWidth(0);
        msg3.setTextSize(10);
        msg3.setTypeface(Typeface.DEFAULT_BOLD);
        mCommonTabLayout.showDot(0);
    }

    @Override
    protected void initView() {
        StringBuffer prmissionContent = new StringBuffer();
        mRxPrmissions.requestEach(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET)
                .subscribe(permission -> {
                    if (permission.granted) {
                    } else if (permission.shouldShowRequestPermissionRationale) {
                    } else {
                        prmissionContent.append("\n");
                        prmissionContent.append(permission.name);
                    }
                    if (prmissionContent.length()>0 && permission.name.equals(Manifest.permission.INTERNET)){
                        prmissionContent.insert(0,"需要允许权限：");
                        new MaterialDialog.Builder(this)
                                .title("权限说明")
                                .content(prmissionContent)
                                .negativeText("确定")
                                .show();
                    }
                });


    }

    @Override
    public void onActionBarClick(View view) {
        switch (view.getId()) {
            case R.id.actionbar_lift:
                onBackPressed();
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //华为移动服务错误处理回调
        mHuaweiPushHelper.errorResult(requestCode, resultCode, data);
    }



}
