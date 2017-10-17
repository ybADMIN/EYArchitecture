package com.yb.btcinfo.common.widget;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yb.btcinfo.R;
import com.yb.ilibray.utils.TextViewDrawableUtils;

/**
 * Created by yb(yb498869020@hotmail.com) on 2016/6/4.
 */
public class SimpleToolBar implements View.OnClickListener {
    /**
     * Created by yb123 on 2015/11/22.
     */
    private Context mContext;
    private  Toolbar toolbar;
    private TextView actionLiftBtn;//左边actionbar按钮
    private TextView actionRightBtn;//右边actionbar按钮
    private TextView actionCenterBtn;//中间actionbar标题
    private OnActionBarClick click;
    private View mToolbarContent;

    public SimpleToolBar(AppCompatActivity context) {
        toolbar =(Toolbar) context.findViewById(R.id.toolbar);
        if (toolbar==null)return;
        context.setSupportActionBar(toolbar);
        this.mContext = context;
        init();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public SimpleToolBar(ViewGroup view) {
        toolbar =(Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar==null)return;
        this.mContext = view.getContext();
        init();
    }


    private void init() {
        if (toolbar != null) {
            mToolbarContent=toolbar.findViewById(R.id.toolbar_content);
            actionLiftBtn = (TextView) toolbar.findViewById(R.id.actionbar_lift);
            actionRightBtn = (TextView) toolbar.findViewById(R.id.actionbar_right);
            actionCenterBtn = (TextView) toolbar.findViewById(R.id.actionbar_center);
        }
    }

    public void setActionBarIcon(@DrawableRes int drawableid, int actionbarID) {
        switch (actionbarID) {
            case R.id.actionbar_lift:
                TextViewDrawableUtils.setDrawable(actionLiftBtn, ContextCompat.getDrawable(mContext,drawableid), null, null, null);
                break;
            case R.id.actionbar_right:
                TextViewDrawableUtils.setDrawable(actionRightBtn, null, null, ContextCompat.getDrawable(mContext,drawableid), null);
                break;
            case R.id.actionbar_center:
                TextViewDrawableUtils.setDrawable(actionCenterBtn,ContextCompat.getDrawable(mContext,drawableid), null, null, null);
                break;
        }
    }

    public SimpleToolBar setToolbarBackground(@DrawableRes int background){
        toolbar.setBackgroundResource(background);
        return this;
    }

    /**
     * 获得左边按钮
     * @return
     */
    public TextView getLiftBtn() {
        showView(actionLiftBtn);
        return actionLiftBtn;
    }

    /**
     * 显示并设置左边按钮Title
     * @param title
     * @return
     */
    public SimpleToolBar liftBtn(String title) {
        return liftBtn(title,0,0);
    }

    /**
     *
     * @param title
     * @param color 文本颜色
     * @return
     */
    public SimpleToolBar liftBtn(String title, @ColorInt int color) {
        return liftBtn(title,0,color);
    }
    /**
     * 显示并设置左边按钮title和icon
     * @param title
     * @param icon
     * @return
     */
    public SimpleToolBar liftBtn(String title, @DrawableRes int icon, @ColorInt int color) {
        showView(actionLiftBtn);
        if (title!=null)
            actionLiftBtn.setText(title);
        if (icon!=0)
            setActionBarIcon(icon,actionLiftBtn.getId());
        if (color!=0)
            actionLiftBtn.setTextColor(color);
        return this;
    }

    /**
     * 获得并显示Toobar右边控件
     * @return
     */
    public TextView getRightBtn(){
        showView(actionRightBtn);
        return actionRightBtn;
    }


    /**
     * 显示右边按钮并设置Title
     * @param title
     * @return
     */
    public SimpleToolBar rightBtn(String title) {
        return rightBtn(title,0,0);
    }

    /**
     * @param title
     * @param color 文本颜色
     * @return
     */
    public SimpleToolBar rightBtn(String title, @ColorInt int color) {
        return rightBtn(title,0,color);
    }


    /**
     * 显示右边按钮并设置title和icon
     * @param title
     * @param icon
     * @return
     */
    public SimpleToolBar rightBtn(String title, @DrawableRes int icon, @ColorInt int color) {
        showView(actionRightBtn);
        if (title!=null)
            actionRightBtn.setText(title);
        if (icon!=0)
            setActionBarIcon(icon,actionRightBtn.getId());
        if (color!=0)
            actionRightBtn.setTextColor(color);
        return this;
    }

    /**
     * 获得Toobar中间按钮
     * @return
     */
    public TextView getCenterBtn(){
        showView(actionCenterBtn);
        return actionCenterBtn;
    }

    /**
     * 设置中间Title
     * @param title
     * @return
     */
    public SimpleToolBar centerBtn(String title) {
        return  centerBtn(title,0,0);
    }

    /**
     * @param title
     * @param color 文本颜色
     * @return
     */
    public SimpleToolBar centerBtn(String title, @ColorInt int color) {
        return  centerBtn(title,0,color);
    }
    /**
     * 设置title和icon
     * @param title
     * @param icon
     * @return
     */
    public SimpleToolBar centerBtn(String title, @DrawableRes int icon, @ColorInt int color) {
        showView(actionCenterBtn);
        if (title!=null)
        actionCenterBtn.setText(title);
        if (icon!=0)
        setActionBarIcon(icon,actionCenterBtn.getId());
        if (color!=0)
            actionCenterBtn.setTextColor(color);
        return this;
    }

    private void showView(View view){
        if (view.getVisibility()==View.GONE){
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (click != null) {
            click.onActionBarClick(v);
        }
    }

    public void setClick(OnActionBarClick click) {
        this.click = click;
        if (toolbar!=null){
            if (actionLiftBtn!=null)
            actionLiftBtn.setOnClickListener(this);
            if (actionRightBtn!=null)
            actionRightBtn.setOnClickListener(this);
            if (actionCenterBtn!=null)
            actionCenterBtn.setOnClickListener(this);
        }
    }

    public View getToolbarContent() {
        return mToolbarContent;
    }

    public interface OnActionBarClick {
        void onActionBarClick(View view);
    }
}
