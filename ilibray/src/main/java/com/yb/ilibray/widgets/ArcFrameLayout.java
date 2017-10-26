package com.yb.ilibray.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.yb.ilibray.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ericYang on 2017/10/16.
 * Email:eric.yang@huanmedia.com
 * 圆弧形状头部 ，很多App个人中心流行效果
 * 1.关闭效果，设置圆弧方向和高度，纯色圆弧等
 * 2.XML属性设置
 */

public class ArcFrameLayout extends FrameLayout {

    private Paint mPaint;
    private PointF mStartPoint, mEndPoint, mControlPoint;
    private int mWidth;
    private int mHeight;
    private Path mPath = new Path();
    private int mArcHeight=dip2px(10);// 圆弧高度
    private int mEndColor,mStartColor;//渐变颜色
    private boolean mSuppertBackground;//是否支持渐变背景
    public final static int Buttom=0;
    public final static int Top=1;
    private int mDirection=Buttom;
    private Path mClipPath;
    private Paint mClipPaint;
    private int mClipColor;

    @IntDef({Top, Buttom})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction{
    }
    public ArcFrameLayout(@NonNull Context context) {
        super(context);
        init(null);
    }

    public ArcFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ArcFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ArcFrameLayout);
            mArcHeight = typedArray.getDimensionPixelSize(R.styleable.ArcFrameLayout_arc_hight,dip2px(10));
            mStartColor = typedArray.getColor(R.styleable.ArcFrameLayout_arc_startColor,Color.parseColor("#209cff"));
            mEndColor = typedArray.getColor(R.styleable.ArcFrameLayout_arc_endColor,Color.parseColor("#68e0cf"));
            mSuppertBackground = typedArray.getBoolean(R.styleable.ArcFrameLayout_arc_suppert_background,false);
            mDirection = typedArray.getInt(R.styleable.ArcFrameLayout_arc_direction,Buttom);
            mClipColor = typedArray.getColor(R.styleable.ArcFrameLayout_arc_clip_color,-1);
            typedArray.recycle();
        }
        if (mClipColor==-1){
            TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[] {
                    android.R.attr.colorBackground
            });
            mClipColor = array.getColor(0, 0xFF00FF);
            array.recycle();
        }

        mPaint = new Paint();
//        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
//        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);

        mClipPaint = new Paint();
        mClipPaint.setAntiAlias(true);
        mClipPaint.setColor(mClipColor);
        mClipPaint.setStyle(Paint.Style.FILL);
        mClipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        mStartPoint = new PointF(0, 0);
        mEndPoint = new PointF(0, 0);
        mControlPoint = new PointF(0, 0);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initParms(mDirection,w,h);
    }

    private void initParms(int direction, int width, int height) {
        int rectHight;
            switch (direction){
                case Buttom:
                    rectHight = height - mArcHeight;
                    mStartPoint.x = 0;
                    mStartPoint.y = rectHight;

                    mEndPoint.x = width;
                    mEndPoint.y = rectHight;

                    mControlPoint.x = width / 2 - mArcHeight / 2;
                    mControlPoint.y = height + mArcHeight;

                    mPath.reset();

                    mPath.moveTo(0, 0);
                    mPath.addRect(0, 0, width, rectHight, Path.Direction.CCW);
                    mPath.moveTo(mStartPoint.x, mStartPoint.y);
                    mPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y);

                    if (mSuppertBackground){
                        LinearGradient sweepGradient = new LinearGradient(width/2,0,width/2,height,mStartColor,mEndColor, Shader.TileMode.CLAMP);
                        mPaint.setShader(sweepGradient);
                    }
                    mClipPath = new Path();
                    mClipPath.moveTo(mStartPoint.x, mStartPoint.y);
                    mClipPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y);
                    mClipPath.lineTo(mEndPoint.x,mEndPoint.y+mArcHeight);
                    mClipPath.lineTo(mStartPoint.x,mStartPoint.y+mArcHeight);
                    mClipPath.close();
                    break;
                case Top:
                    rectHight = height - mArcHeight;
                    mStartPoint.x = 0;
                    mStartPoint.y = mArcHeight;

                    mEndPoint.x = width;
                    mEndPoint.y = mArcHeight;

                    mControlPoint.x = width / 2 - mArcHeight / 2;
                    mControlPoint.y = -mArcHeight;

                    mPath.reset();

                    mPath.moveTo(0, 0);
                    mPath.addRect(0, mArcHeight, width, mArcHeight+rectHight, Path.Direction.CCW);
                    mPath.moveTo(mStartPoint.x, mStartPoint.y);
                    mPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y);

                    if (mSuppertBackground){
                        LinearGradient sweepGradient = new LinearGradient(width/2,height,width/2,0,mStartColor,mEndColor, Shader.TileMode.CLAMP);
                        mPaint.setShader(sweepGradient);
                    }
                    mClipPath = new Path();
                    mClipPath.moveTo(mStartPoint.x, mStartPoint.y);
                    mClipPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y);
                    mClipPath.lineTo(mEndPoint.x,mEndPoint.y-mArcHeight);
                    mClipPath.lineTo(mStartPoint.x,mStartPoint.y-mArcHeight);
                    mClipPath.close();
                    break;
            }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.i(VIEW_LOG_TAG, "dispatchDraw");
//-------------------方法1：会有重复调用dispatchDraw方法问题
//        if (getChildCount()>0){
//            isChangeState=false;
//        if (mBitmap == null)
//            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//        if (mShader == null)
//            mShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//        if (mCapCanvas == null) {
//            mCapCanvas = new Canvas(mBitmap);
//        }
//            super.dispatchDraw(mCapCanvas);
//            mPaint.setShader(mShader);
//            mPath.moveTo(mStartPoint.x, mStartPoint.y);
//            mPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y);
//            canvas.drawPath(mPath, mPaint);
//        }else {
//            super.dispatchDraw(canvas);
//        }
//--------------------方法2 无法去掉锯齿
//        if (getChildCount()>0){
//            canvas.save();
//            mPath.moveTo(mStartPoint.x, mStartPoint.y);
//            mPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y);
//            canvas.clipPath(mPath);
//            super.dispatchDraw(canvas);
//            canvas.restore();
//        }else {
//            super.dispatchDraw(canvas);
//        }
//---------------------方法3
        canvas.saveLayer(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), null, Canvas
                .ALL_SAVE_FLAG);
        if (mSuppertBackground){
            canvas.drawPath(mPath,mPaint);
        }
        super.dispatchDraw(canvas);
        //裁剪
        canvas.drawPath(mClipPath, mClipPaint);
        canvas.restore();
        mClipPaint.setXfermode(null);

    }

    public void setArcHeight(int arcHeight) {
        mArcHeight = arcHeight;
    }

    public void setEndColor(int endColor) {
        mEndColor = endColor;
    }

    public void setStartColor(int startColor) {
        mStartColor = startColor;
    }

    public void setSuppertBackground(boolean suppertBackground) {
        mSuppertBackground = suppertBackground;
    }

    public void setDirection(int direction) {
        mDirection = direction;
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
