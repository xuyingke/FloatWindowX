package com.yingke.floatwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

public class FloatViewTouchListener implements View.OnTouchListener {

    private final String TAG = "FloatViewTouchListener";

    private final FloatViewController mViewController;
    private final FloatConfig mFloatConfig;

    private float downX, downY, lastX, lastY, changeX, changeY;

    private float mFloatViewWidth, mFloatViewHeight;
    private View mFloatView;
    private boolean mClick = false;
    private float upX, upY;

    private float mLastViewX, mLastViewY;
    private TouchActionUpListener mTouchActionUpListener;

    private int mDisplayWidth = -1;
    private int mDisplayHeight = -1;

    /**
     * 松手后做动画时用来禁止事件的。
     * 除非动画很久，否则你手速也没那么快吧哈~
     */
    private boolean mBanTouch;

    public FloatViewTouchListener(@NonNull FloatViewController viewController) {
        this.mViewController = viewController;
        mFloatConfig = mViewController.getFloatBuilder();
        mFloatView = mFloatConfig.getFloatView();
        mTouchActionUpListener = mFloatConfig.getTouchActionUpListener();
        init();
    }

    public void init() {
        if (mFloatConfig != null) {
            mFloatViewWidth = mFloatConfig.getFloatViewWidth();
            mFloatViewHeight = mFloatConfig.getFloatViewHeight();
        }
        if (mFloatView != null) {
            mDisplayWidth = FloatUtils.getWidth(mFloatView.getContext());
            mDisplayHeight = FloatUtils.getHeight(mFloatView.getContext());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mBanTouch) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();

                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                changeX = event.getRawX() - lastX;
                changeY = event.getRawY() - lastY;

                float newX = mFloatConfig.getRawX() + changeX;
                float newY = mFloatConfig.getRawY() + changeY;

                // 边缘处理
                if (mDisplayWidth > 0 && mDisplayHeight > 0) {
                    newX = newX < 0 ? 0 : newX;
                    newX = (newX + mFloatViewWidth) > mDisplayWidth ? mDisplayWidth - mFloatViewWidth : newX;

                    newY = newY < 0 ? 0 : newY;
                    newY = (newY + mFloatViewHeight) > mDisplayHeight ? mDisplayHeight - mFloatViewHeight : newY;
                }
                FloatXLog.d(TAG + "newX= " + newX + " newY= " + newY
                        + " viewWidth= " + mFloatViewWidth + " viewHeight= " + mFloatViewHeight);
                mViewController.updateViewLocation(newX, newY);

                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getRawX();
                upY = event.getRawY();
                mClick = (Math.abs(upX - downX) <= 0) || (Math.abs(upY - downY) <= 0);
                handleAnimator(mClick, v, event);
                break;
            default:
                break;
        }
        FloatXLog.d("FloatViewTouchListener" + "action= " + event.getAction() + " mClick= " + mClick);
        return !mClick;
    }

    /**
     * 处理抬手后的动画
     *
     * @param isClick 是否是点击事件
     */
    private void handleAnimator(boolean isClick, View view, MotionEvent event) {
        if (isClick) {
            return;
        }
        if (mTouchActionUpListener != null && mTouchActionUpListener.actionUp(mViewController, event)) {
            return;
        }

        int displayWidth = this.mDisplayWidth;
        float floatViewWidth = this.mFloatViewWidth;

        int rawX = mViewController.getFloatBuilder().getRawX();
        int rawY = mViewController.getFloatBuilder().getRawY();

        float endX = (rawX * 2 + view.getWidth() >
                displayWidth) ?
                displayWidth - floatViewWidth : 0;

        ValueAnimator mAnimator = ObjectAnimator.ofFloat(rawX, endX);
        mAnimator.setDuration(300);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                mViewController.updateViewLocation(x, rawY);
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBanTouch = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBanTouch = false;
                if (animation instanceof ValueAnimator) {
                    ValueAnimator valueAnimator = (ValueAnimator) animation;
                    valueAnimator.removeAllUpdateListeners();
                    animation.removeAllListeners();
                }
            }
        });
        mAnimator.start();
    }
}
