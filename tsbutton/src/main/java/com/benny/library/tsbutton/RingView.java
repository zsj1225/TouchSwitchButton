package com.benny.library.tsbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lixiao on 2017/9/4 09:13.
 */
public class RingView extends View {

    private Paint mPaint;
    private final float ring_width;
    private int ringColor;
    private final TypedArray array;

    public RingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //通过该方法，可以取出attrs中的RingView属性。RingView就是我们在attrs.xml文件中declare-styleable标签名称。
        array = context.obtainStyledAttributes(attrs, R.styleable.RingView);
        //取出属性
        ring_width = array.getDimension(R.styleable.RingView_ring_width, 5);
        ringColor = array.getColor(R.styleable.RingView_ring_color, Color.BLACK);
        //最后需要将TypedArray对象回收
        array.recycle();

        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ring_width);
        mPaint.setColor(ringColor);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    public Paint getPaint() {
        return mPaint;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int cx = getMeasuredWidth() / 2;
        int cy = getMeasuredHeight() / 2;
        int radius = (getMeasuredWidth() - dip2px(getContext(),1))/2;
        canvas.drawCircle(cx, cy, radius, mPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getSize(dip2px(getContext(), 100), widthMeasureSpec);
        int height = getSize(dip2px(getContext(), 100), heightMeasureSpec);
        if (width > height) {
            width = height;
        } else {
            height = width;
        }
        setMeasuredDimension(width, height);
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpValue
     * @return
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private int getSize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                mySize = defaultSize;
                break;
            case MeasureSpec.EXACTLY:
                mySize = size;
                break;
            case MeasureSpec.AT_MOST:
                mySize = defaultSize;
                break;
            default:
                break;
        }
        return mySize;

    }
}
