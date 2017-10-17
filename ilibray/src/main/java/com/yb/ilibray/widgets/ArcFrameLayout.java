package com.yb.ilibray.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Created by ericYang on 2017/10/16.
 * Email:eric.yang@huanmedia.com
 * 圆弧形状头部 ，很多App个人中心流行效果
 * 还未完善功能（关闭效果，设置圆弧方向和高度，纯色圆弧等）
 */

public class ArcFrameLayout extends FrameLayout {

    private Paint mPaint;
    private PointF mStartPoint, mEndPoint, mControlPoint;
    private int mWidth;
    private int mHeight;
    private Path mPath = new Path();
    private int mArcHeight;// 圆弧高度
    private Bitmap mBitmap;
    private Shader mShader;
    private Canvas mCapCanvas;

    public ArcFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public ArcFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArcFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private void init() {
        mPaint = new Paint();
//        mPaint.setColor(Color.parseColor("#37B99F"));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.FILL);

        mStartPoint = new PointF(0, 0);
        mEndPoint = new PointF(0, 0);
        mControlPoint = new PointF(0, 0);
        mArcHeight = dip2px(30);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        int rectHight = mHeight - mArcHeight;

        mPath.reset();

        mPath.moveTo(0, 0);
        mPath.addRect(0, 0, mWidth, rectHight, Path.Direction.CCW);

        mStartPoint.x = 0;
        mStartPoint.y = rectHight;

        mEndPoint.x = mWidth;
        mEndPoint.y = rectHight;

        mControlPoint.x = mWidth / 2 - mArcHeight/2;
        mControlPoint.y = mHeight+mArcHeight;


        ///SweepGradient sweepGradient = new SweepGradient(mEndPoint.x / 2,mEndPoint.y / 2,mStartColor,mEndColor);
        //mPaint.setShader(sweepGradient);


        invalidate();

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.i(VIEW_LOG_TAG,"dispatchDraw");
        if (getChildCount()>0){
            if (mBitmap==null)
                mBitmap= Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            if (mShader==null)
                mShader =new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            if (mCapCanvas==null){
                mCapCanvas=new Canvas(mBitmap);
            }
            super.dispatchDraw(mCapCanvas);
            BitmapShader shader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(shader);
            mPath.moveTo(mStartPoint.x, mStartPoint.y);
            mPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y);
            canvas.drawPath(mPath, mPaint);
        }else {
            super.dispatchDraw(canvas);
        }
    }
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
