package com.yb.ilibray.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.yb.ilibray.R;

/**
 * Created by Administrator on 2016/11/30.
 * 还未完善功能
 * 1.设置振幅，平率 ，高度等
 * 2.XML属性设置
 */

public class WaveView extends View {

    private Path  mAbovePath,mBelowWavePath;
    private Paint mAboveWavePaint,mBelowWavePaint;

    private DrawFilter mDrawFilter;

    private float φ;

    private OnWaveAnimationListener mWaveAnimationListener;
    private boolean mShowWaveAnim;//动画控制开关
    private double mA=40;
    private double mK1=48;
    private double mK2=40;
    private float mWaveHight;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    public WaveView(Context context) {
        super(context);
        init(context,null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs!=null){
            TypedArray typedArray =context.obtainStyledAttributes(attrs,R.styleable.WaveView);
            mA=typedArray.getFloat(R.styleable.WaveView_wave_swing,40);
            mK1=typedArray.getFloat(R.styleable.WaveView_wave_k1,48);
            mK2=typedArray.getFloat(R.styleable.WaveView_wave_k2,40);
            mWaveHight=typedArray.getDimensionPixelSize(R.styleable.WaveView_wave_hight,0);
            typedArray.recycle();
        }

        //初始化路径
        mAbovePath = new Path();
        mBelowWavePath = new Path();
        //初始化画笔
        mAboveWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAboveWavePaint.setAntiAlias(true);
        mAboveWavePaint.setStyle(Paint.Style.FILL);
        mAboveWavePaint.setColor(Color.parseColor("#88D7FFFE"));

        mBelowWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBelowWavePaint.setAntiAlias(true);
        mBelowWavePaint.setStyle(Paint.Style.FILL);
        mBelowWavePaint.setColor(Color.parseColor("#88dfe9f3"));
        mBelowWavePaint.setAlpha(80);
        //画布抗锯齿
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(mDrawFilter);
        mAbovePath.reset();
        mBelowWavePath.reset();


        float y,y2;

        double ω = 2*Math.PI / getWidth();

        mAbovePath.moveTo(getLeft(),getBottom());
        mBelowWavePath.moveTo(getLeft(),getBottom());
        for (float x = 0; x <= getWidth()+20; x += 20) {
            /**
             *  y=Asin(ωx+φ)+k
             *  A—振幅越大，波形在y轴上最大与最小值的差值越大
             *  ω—角速度， 控制正弦周期(单位角度内震动的次数)
             *  φ—初相，反映在坐标系上则为图像的左右移动。这里通过不断改变φ,达到波浪移动效果
             *  k—偏距，反映在坐标系上则为图像的上移或下移。
             */
            y = (float) (mA * Math.cos(ω * x + φ) +mK1)+mWaveHight;
            y2 = (float) (mA * Math.sin(ω * x + φ)+mK2)+mWaveHight;
            mAbovePath.lineTo(x, y);
            mBelowWavePath.lineTo(x, y2);
            //回调 把y坐标的值传出去(在activity里面接收让小机器人随波浪一起摇摆)
            if (mWaveAnimationListener!=null)
            mWaveAnimationListener.OnWaveAnimation((float) (y-mK1));
        }
        mAbovePath.lineTo(getRight(),getBottom());
        mBelowWavePath.lineTo(getRight(),getBottom());

        canvas.drawPath(mAbovePath,mAboveWavePaint);
        canvas.drawPath(mBelowWavePath,mBelowWavePaint);

        if (mShowWaveAnim){
            φ-=0.1f;
            postInvalidateDelayed(60);
        }
    }
    public void showWaveAnim(){
        mShowWaveAnim=true;
        postInvalidate();
    }
    public void hideWaveAndim(){
        mShowWaveAnim=false;
    }

    public boolean isShowWaveAnim() {
        return mShowWaveAnim;
    }

    public void setOnWaveAnimationListener(OnWaveAnimationListener l){
        this.mWaveAnimationListener = l;
    }

    public interface OnWaveAnimationListener{
        void OnWaveAnimation(float y);
    }


    /**
     *  y=Asin(ωx+φ)+k
     *  A—振幅越大，波形在y轴上最大与最小值的差值越大
     */
    public void setA(double a) {
        mA = a;
    }
    /**
     *  y=Asin(ωx+φ)+k
     *  k—偏距，反映在坐标系上则为图像的上移或下移。
     */
    public void setK1(double k1) {
        mK1 = k1;
    }
    /**
     *  y=Asin(ωx+φ)+k
     *  k—偏距，反映在坐标系上则为图像的上移或下移。
     */
    public void setK2(double k2) {
        mK2 = k2;
    }
    /**
     *  y=Asin(ωx+φ)+k
     *  k—偏距，反映在坐标系上则为图像的上移或下移。
     */
    public void setWaveHight(float waveHight) {
        mWaveHight = waveHight;
    }
}