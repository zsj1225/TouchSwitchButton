package com.benny.library.tsbutton;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benny on 17/11/2016.
 */

public class TouchSwitchButton extends RelativeLayout {
    public static final int TWO_WAY = 0;
    public static final int TO_LEFT = 1;
    public static final int TO_RIGHT = 2;
    private static final String TAG = "TouchSwitchButton";

    private View thumbView;
    private OnActionSelectedListener onToLeftSelectedListener;
    private OnActionSelectedListener onToRightSelectedListener;

    private Drawable mThumb;
    private int thumbId;
    private int startColor;
    private int toLeftEndColor;
    private int toRightEndColor;
    private float radius;
    private int direction = TWO_WAY;
    private ObjectAnimator mTouchZoomScaleY;
    private ObjectAnimator mTouchZoomScaleX;
    private ObjectAnimator mTouchZoomOutScaleY;
    private ObjectAnimator mTouchZoomOutScaleX;

    private float mTouchZoomValue = 1.25f;
    private long mTouchZoomTime = 100;
    private ImageView mImageViewLeft;
    private ImageView mImageViewRight;
    private int mImageViewRightWidth;
    private int mImageViewLeftWidth;
    private ImageView mIvCenterPoint;
    private AnimatorSet mCenterPointAnimSet;
    private ImageView mIvLeftPointOne;
    private AnimatorSet LeftPointOneAnimSet;
    private ImageView mIvLeftPointTwo;
    private List<AnimatorSet> mLeftRightAnimSets = new ArrayList<>();
    private ImageView mIvRightPointOne;
    private ImageView mIvRightPointTwo;
    private AnimatorSet mAlphaAnimatorSet;
    private AnimatorSet mTouchZoomAnimatorSet;

    public TouchSwitchButton(Context context) {
        this(context, null);
    }

    public TouchSwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchSwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttributes(context, attrs);

        initView();
    }

    public void setOnToLeftSelectedListener(OnActionSelectedListener toLeftSelectedListener) {
        this.onToLeftSelectedListener = toLeftSelectedListener;
    }

    public void setOnToRightSelectedListener(OnActionSelectedListener toRightSelectedListener) {
        this.onToRightSelectedListener = toRightSelectedListener;
    }

    void resolveAttributes(Context context, AttributeSet attrs) {
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.TouchSwitchButton);
        mThumb = t.getDrawable(R.styleable.TouchSwitchButton_tsb_thumb);
        thumbId = t.getResourceId(R.styleable.TouchSwitchButton_tsb_thumbId, 0);
        direction = t.getInt(R.styleable.TouchSwitchButton_tsb_direction, TWO_WAY);
        startColor = t.getColor(R.styleable.TouchSwitchButton_tsb_startColor, Color.TRANSPARENT);
        toLeftEndColor = t.getColor(R.styleable.TouchSwitchButton_tsb_toLeftEndColor, Color.TRANSPARENT);
        toRightEndColor = t.getColor(R.styleable.TouchSwitchButton_tsb_toRightEndColor, Color.TRANSPARENT);
        radius = t.getDimension(R.styleable.TouchSwitchButton_tsb_radius, 10000);
        t.recycle();
    }

    private Drawable createBackground() {
        if (getBackground() == null) {
            //if not set background in layout xml, than create default round rect background
            float[] outerRadii = {radius, radius, radius, radius, radius, radius, radius, radius};
            RoundRectShape roundRectShape = new RoundRectShape(outerRadii, null, null);
            ShapeDrawable drawable = new ShapeDrawable(roundRectShape);
            drawable.getPaint().setColor(startColor);
            drawable.getPaint().setStyle(Paint.Style.FILL);
            return drawable;
        }
        return getBackground();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(createBackground());
        } else {
            setBackgroundDrawable(createBackground());
        }
        seLeftImage(R.drawable.ts_hangup);
        setRightImage(R.drawable.ts_answer);
        mIvLeftPointOne = getPoint(R.drawable.ts_left_point_one);
        mIvLeftPointTwo =  getPoint(R.drawable.ts_left_point_two);
        mIvCenterPoint = getPoint(R.drawable.ts_center_point);
        mIvRightPointOne = getPoint(R.drawable.ts_right_point_one);
        mIvRightPointTwo = getPoint(R.drawable.ts_right_point_two);

        playPointAni();
    }

    public ImageView getPoint(int resId){
        ImageView point = new ImageView(getContext());
        point.setImageResource(resId);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(point, layoutParams);
        return point;
    }


    public void setRightImage(int resId) {
        if (mImageViewRight != null) {
            removeView(mImageViewRight);
        }
        mImageViewRight = new ImageView(getContext());
        mImageViewRight.setImageResource(resId);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.rightMargin = dip2px(getContext(), 35);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(mImageViewRight, layoutParams);
    }

    public void seLeftImage(int resID) {
        if (mImageViewLeft != null) {
            removeView(mImageViewLeft);
        }
        mImageViewLeft = new ImageView(getContext());
        mImageViewLeft.setImageResource(resID);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = dip2px(getContext(), 35);
        ;
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(mImageViewLeft, layoutParams);
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


    private void setupThumb() {
        if (mThumb != null) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(mThumb);
            addView(imageView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            thumbView = imageView;
        } else if (thumbId != 0) {
            thumbView = findViewById(thumbId);
        }

        if (thumbView != null) {
            thumbView.setOnTouchListener(new OnThumbTouchListener());
        }
    }

    void setThumbSize(int thumbWidth, int thumbHeight) {
        ViewGroup.LayoutParams lp = thumbView.getLayoutParams();
        lp.width = thumbWidth;
        lp.height = thumbHeight;
        thumbView.setLayoutParams(lp);
    }

    private float getThumbPosition() {
        return thumbView.getX() - getPaddingLeft();
    }

    private float getThumbInitialPosition(float containerWidth, float thumbWidth) {
        float position = 0;
        switch (direction) {
            case TWO_WAY:
                position = (containerWidth - getPaddingLeft() - getPaddingRight() - thumbWidth) / 2;
                break;
            case TO_LEFT:
                position = getThumbMaxPosition(containerWidth, thumbWidth);
                break;

            default:
                break;
        }
        return position;
    }

    private float getThumbInitialPosition() {
        return getThumbInitialPosition(getWidth(), thumbView.getWidth());
    }


    private float getThumbMaxPosition(float containerWidth, float thumbWidth) {
        return containerWidth - getPaddingRight() - getPaddingLeft() - thumbWidth;
    }

    private float getThumbMaxPosition() {
        return getWidth() - getPaddingRight() - getPaddingLeft() - thumbView.getWidth();
    }

    float getOffsetPercentage(float delta) {
        double percent;
        switch (direction) {
            case TWO_WAY:
                percent = Math.abs(delta) / (0.5 * getThumbMaxPosition());
                break;
            default:
                percent = Math.abs(delta) / getThumbMaxPosition();
        }
        float offset = (float) (percent > 1 ? 1f : percent);
        return offset;
    }

    public void changeColor(float delta, int startColor) {
        float offsetPercentage = getOffsetPercentage(delta);
        float startOffsetPercentage = offsetPercentage - 0.55f;
        float offset = startOffsetPercentage / (1 - 0.55f);
        if (offset < 0) {
            offset = 0;
        }
        if (offset > 1) {
            offset = 1;
        }

        if (delta > 0) {
            mImageViewRight.setScaleX(1+offset);
            mImageViewRight.setScaleY(1+offset);
        } else {
            mImageViewLeft.setScaleX(1+offset);
            mImageViewLeft.setScaleY(1+offset);
        }

        int currentColor = (int) new ArgbEvaluator().evaluate(offsetPercentage, startColor, delta > 0 ? Color.GREEN : Color.RED);
        ((RingView) thumbView).getPaint().setColor(currentColor);
        ((RingView) thumbView).invalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setupThumb();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int receivedHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (mThumb != null) {
            if (receivedHeight == 0 && mThumb.getIntrinsicHeight() != 0) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(mThumb.getIntrinsicHeight() + getPaddingBottom() + getPaddingTop(), MeasureSpec.EXACTLY);
            } else if (receivedHeight != 0) {
                if (mThumb.getIntrinsicHeight() == 0) {
                    int thumbWidth = receivedHeight - getPaddingBottom() - getPaddingTop();
                    setThumbSize(thumbWidth, thumbWidth);
                } else {
                    float heightWithoutPadding = receivedHeight - getPaddingBottom() - getPaddingTop();
                    double ratio = heightWithoutPadding / mThumb.getIntrinsicHeight();
                    setThumbSize((int) (mThumb.getIntrinsicWidth() * ratio), (int) heightWithoutPadding);
                }
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 移到Layout方法中.
//        thumbView.setTranslationX(getThumbInitialPosition());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        thumbView.setTranslationX(getThumbInitialPosition());

        mImageViewRightWidth = mImageViewRight.getMeasuredWidth();
        mImageViewLeftWidth = mImageViewLeft.getMeasuredWidth();


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void playCenterPointAni() {
        mCenterPointAnimSet = new AnimatorSet();
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mIvCenterPoint, "scaleY", 1.0f, 0.8f);
        scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleYAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scaleYAnimator.setDuration(650);

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mIvCenterPoint, "scaleX", 1.0f, 0.8f);
        scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleXAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scaleXAnimator.setDuration(650);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mIvCenterPoint, "alpha", 1, 0.4f);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnimator.setDuration(650);

        mCenterPointAnimSet.play(scaleYAnimator).with(scaleXAnimator).with(alphaAnimator);
        mCenterPointAnimSet.setInterpolator(new PathInterpolator(0.35f, 0.0f, 0.2f, 1f));
        mCenterPointAnimSet.start();
    }

    /**
     * @param iv 作用于那个ImageView
     * @param translationX 负数：左移 正数：右移
     * @param startDelay 延时多久开始动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void playLeftPointAni(ImageView iv, int translationX, long startDelay) {
        AnimatorSet leftRightAnimSet = new AnimatorSet();
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(iv, "scaleY", 1.0f, 1.75f);
        scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleYAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scaleYAnimator.setDuration(650);
        scaleYAnimator.setStartDelay(startDelay);
        scaleYAnimator.setInterpolator(new PathInterpolator(0.35f, 0.0f, 0.2f, 1f));

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(iv, "scaleX", 1.0f, 1.75f);
        scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleXAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scaleXAnimator.setDuration(650);
        scaleXAnimator.setStartDelay(startDelay);
        scaleXAnimator.setInterpolator(new PathInterpolator(0.35f, 0.0f, 0.2f, 1f));

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(iv, "alpha", 0f, 1f);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnimator.setDuration(650);
        alphaAnimator.setStartDelay(startDelay);
        alphaAnimator.setInterpolator(new PathInterpolator(0.35f, 0.0f, 0.2f, 1f));

        ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(iv, "translationX",
                0,  dip2px(getContext(),translationX));
        translationXAnimator.setDuration(1300);
        translationXAnimator.setRepeatCount(ValueAnimator.INFINITE);
        translationXAnimator.setRepeatMode(ValueAnimator.RESTART);
        translationXAnimator.setInterpolator(new PathInterpolator(0.3f, 0.0f, 0.4f, 1f));
        translationXAnimator.setStartDelay(startDelay);

        leftRightAnimSet.play(scaleYAnimator).with(scaleXAnimator).with(alphaAnimator).with(translationXAnimator);
        leftRightAnimSet.start();

        mLeftRightAnimSets.add(leftRightAnimSet);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)

    public interface OnActionSelectedListener {
        int onSelected();
    }

    /**
     * 上次
     *
     * @param touchZoomValue
     * @param touchZoomTime
     */
    private void touchZoomOut(final float touchZoomValue, final long touchZoomTime) {
        if (mTouchZoomScaleX != null) {
            mTouchZoomScaleX.cancel();
        }
        if (mTouchZoomScaleY != null) {
            mTouchZoomScaleY.cancel();
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mTouchZoomOutScaleY = ObjectAnimator.ofFloat(thumbView, "scaleY",
                        touchZoomValue, 1.0f).setDuration(touchZoomTime);
                mTouchZoomOutScaleY.start();
                mTouchZoomOutScaleX = ObjectAnimator.ofFloat(thumbView, "scaleX",
                        touchZoomValue, 1.0f).setDuration(touchZoomTime);
                mTouchZoomOutScaleX.start();

                ValueAnimator animation = ValueAnimator.ofFloat(touchZoomValue, 1.0f).setDuration(touchZoomTime);
                animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float animatedValue = (float) animation.getAnimatedValue();
                        float offset = 1.0f - (animatedValue - 1.0f) / (touchZoomValue - 1.0f);
                        int currentColor = (int) new ArgbEvaluator().evaluate(offset, Color.WHITE,
                                getResources().getColor(R.color.ts_transparent_white));
                        ((RingView) thumbView).getPaint().setColor(currentColor);
                        ((RingView) thumbView).invalidate();
                    }
                });
                animation.start();
            }
        }, 0);
    }

    private class OnThumbTouchListener implements OnTouchListener {
        private static final int THRESHOLD = 20;

        private float initialX = 0;
        private float initialTouchX = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float delta, targetX;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    requestDisallowInterceptTouchEvent(true);
                    initialX = getThumbInitialPosition();
                    initialTouchX = event.getRawX();
                    //
                    touchZoom();
                    cancelAniSet();
                    return true;
                case MotionEvent.ACTION_UP:
                    playPointAni();
                    touchZoomOut(mTouchZoomValue, mTouchZoomTime);
                    requestDisallowInterceptTouchEvent(false);
                    delta = event.getRawX() - initialTouchX;
                    targetX = initialX + delta;

                    if (targetX < 0) {
                        targetX = 0;
                    }
                    if (targetX > getThumbMaxPosition()) {
                        targetX = getThumbMaxPosition();
                    }

                    int delay = 0;
                    if (targetX < THRESHOLD && direction != TO_RIGHT) {
                        if (onToLeftSelectedListener != null) {
                            delay = onToLeftSelectedListener.onSelected();
                        }
                    } else if (Math.abs(targetX - getThumbMaxPosition()) < THRESHOLD && direction != TO_LEFT) {
                        if (onToRightSelectedListener != null) {
                            delay = onToRightSelectedListener.onSelected();
                        }
                    }
                    restoreState(delta, delay);

                    return true;
                case MotionEvent.ACTION_MOVE:
                    delta = event.getRawX() - initialTouchX;
                    targetX = initialX + delta;

                    if (targetX < 0) {
                        targetX = 0;
                    }
                    if (targetX > getThumbMaxPosition()) {
                        targetX = getThumbMaxPosition();
                    }

                    thumbView.setTranslationX(targetX);
                    changeColor(delta, Color.WHITE);
                    return true;

                case MotionEvent.ACTION_CANCEL:
                    //
                    touchZoomOut(mTouchZoomValue, mTouchZoomTime);
                    requestDisallowInterceptTouchEvent(false);
                    restoreState(event.getRawX() - initialTouchX, 0);
                default:
                    break;

            }
            return false;
        }

        private void restoreState(final float delta, int delay) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator.ofFloat(thumbView, "translationX", getThumbPosition(), initialX).setDuration(200).start();

                    ValueAnimator animation = ValueAnimator.ofFloat(delta, 0).setDuration(200);
                    animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            changeColor((float) animation.getAnimatedValue(), getResources().getColor(R.color.ts_transparent_white));
                        }
                    });
                    animation.start();
                }
            }, delay);
        }

    }


    private void playPointAni(){
        if (mAlphaAnimatorSet != null) {
            mAlphaAnimatorSet.cancel();
        }

        mIvCenterPoint.setVisibility(VISIBLE);
        mIvLeftPointOne.setVisibility(VISIBLE);
        mIvLeftPointTwo.setVisibility(VISIBLE);
        mIvRightPointOne.setVisibility(VISIBLE);
        mIvRightPointTwo.setVisibility(VISIBLE);

        playCenterPointAni();
        playLeftPointAni(mIvLeftPointOne,-110,0);
        playLeftPointAni(mIvLeftPointTwo,-110,200);
        playLeftPointAni(mIvRightPointOne,110,0);
        playLeftPointAni(mIvRightPointTwo,110,200);
    }



    private ObjectAnimator getAlphaAnimator(View iv, long startDelay,long duration,float... values){
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(iv, "alpha", values);
        alphaAnimator.setDuration(duration);
        alphaAnimator.setStartDelay(startDelay);
        return alphaAnimator;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void cancelAniSet() {
        mAlphaAnimatorSet = new AnimatorSet();
        mAlphaAnimatorSet.playTogether(getAlphaAnimator(mIvCenterPoint, 0, 100,1.0f, 0.0f),
                getAlphaAnimator(mIvLeftPointOne, 0, 100,1.0f, 0.0f),
                getAlphaAnimator(mIvLeftPointTwo, 0, 100,1.0f, 0.0f),
                getAlphaAnimator(mIvRightPointOne, 0, 100,1.0f, 0.0f),
                getAlphaAnimator(mIvRightPointTwo, 0,100, 1.0f, 0.0f));
        mAlphaAnimatorSet.setInterpolator(new PathInterpolator(0.35f, 0.0f, 0.2f, 1f));
        mAlphaAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                resetPointAni();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                resetPointAni();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAlphaAnimatorSet.start();
    }

    private void resetPointAni() {
        for (AnimatorSet leftRightAnimSet : mLeftRightAnimSets) {
            leftRightAnimSet.cancel();
        }
<<<<<<< HEAD

=======
>>>>>>> 触发条件：下击
        mCenterPointAnimSet.cancel();

        mIvCenterPoint.setVisibility(GONE);
        mIvLeftPointOne.setVisibility(GONE);
        mIvLeftPointOne.setScaleX(1);
        mIvLeftPointOne.setScaleY(1);
        mIvLeftPointOne.setTranslationX(0);


        mIvLeftPointTwo.setVisibility(GONE);
        mIvLeftPointTwo.setScaleX(1);
        mIvLeftPointTwo.setScaleY(1);
        mIvLeftPointTwo.setTranslationX(0);

        mIvRightPointOne.setVisibility(GONE);
        mIvRightPointOne.setScaleX(1);
        mIvRightPointOne.setScaleY(1);
        mIvRightPointOne.setTranslationX(0);

        mIvRightPointTwo.setVisibility(GONE);
        mIvRightPointTwo.setScaleX(1);
        mIvRightPointTwo.setScaleY(1);
        mIvRightPointTwo.setTranslationX(0);
    }


    /**
     * 点击放大的动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void touchZoom() {
        if (mTouchZoomAnimatorSet != null) {
            mTouchZoomAnimatorSet.cancel();
        }
        final int duration = 100;
        final float startValue = 1.0f;
        final float endValue = 1.25f;
        postDelayed(new Runnable() {


            @Override
            public void run() {
                mTouchZoomAnimatorSet = new AnimatorSet();
                ObjectAnimator touchZoomScaleY = ObjectAnimator.ofFloat(thumbView, "scaleY", startValue, endValue).setDuration(duration);
                ObjectAnimator touchZoomScaleX = ObjectAnimator.ofFloat(thumbView, "scaleX", startValue, endValue).setDuration(duration);
                ObjectAnimator alphaAnimator = getAlphaAnimator(thumbView, 0, 100, 0.4f, 1f);
                mTouchZoomAnimatorSet.playTogether(touchZoomScaleY,touchZoomScaleX,alphaAnimator);
                mTouchZoomAnimatorSet.setInterpolator(new PathInterpolator(0.35f,0f,0.2f,1f));

                ValueAnimator scaleAnimation = ValueAnimator.ofFloat(startValue, endValue).setDuration(duration);
                scaleAnimation.setInterpolator(new PathInterpolator(0.35f,0f,0.2f,1f));
                scaleAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float animatedValue = (float) animation.getAnimatedValue();
                        float offset = (animatedValue - startValue) / (endValue - startValue);
                        long touchZoomTime = (long) (offset * duration);
                        mTouchZoomValue = animatedValue;
                        mTouchZoomTime = touchZoomTime;
                    }
                });
                scaleAnimation.start();

                ValueAnimator alphaValueAnimator = ValueAnimator.ofFloat(0.4f, 1f).setDuration(100);
                alphaValueAnimator.setInterpolator(new PathInterpolator(0.35f,0f,0.2f,1f));
                scaleAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        //TODO 松手 上次的透明度
                        float animatedValue = (float) animation.getAnimatedValue();
                    }
                });
                alphaValueAnimator.start();

                mTouchZoomAnimatorSet.start();
            }
        }, 0);
    }


}
