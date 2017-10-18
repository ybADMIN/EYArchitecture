package com.yb.btcinfo.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yb.btcinfo.R;
import com.yb.btcinfo.common.BaseActivity;

public class AboutUsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
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
    public void onActionBarClick(View view) {

    }

    public static Intent getCallingIntent(Context context) {
        return new Intent(context,AboutUsActivity.class);
    }
}
