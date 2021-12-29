package com.yingke.demo;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

public class FloatViewTouchListener implements View.OnTouchListener {

    private FloatViewController mViewController;
    private FloatConfig mFloatConfig;

    private float downX, downY, lastX, lastY, changeX, changeY;

    private float mFloatViewWidth, mFloatViewHeight;
    private View mFloatView;
    private boolean mClick = false;
    private float upX, upY;

    private float mLastViewX, mLastViewY;
    private TouchActionUpListener mTouchActionUpListener;

    public FloatViewTouchListener(FloatViewController viewController) {
        this.mViewController = viewController;
        mFloatConfig = mViewController.getFloatBuilder();
        mFloatViewWidth = mFloatConfig.getFloatViewWidth();
        mFloatViewHeight = mFloatConfig.getFloatViewHeight();
        mFloatView = mFloatConfig.getFloatView();
        mTouchActionUpListener = mFloatConfig.getTouchActionUpListener();

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
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
                mViewController.updateViewLocation(newX, newY);

                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getRawX();
                upY = event.getRawY();
                mClick = (Math.abs(upX - downX) < 0) || (Math.abs(upY - downY) < 0);
                handleAnimator(mClick, upX, upY);
                break;
            default:
                break;
        }
        return mClick;
    }

    /**
     * 处理抬手后的动画
     *
     * @param isClick 是否是点击事件
     * @param upX     抬手时的屏幕 x 坐标
     * @param upY     抬手时的屏幕 y 坐标
     */
    private void handleAnimator(boolean isClick, float upX, float upY) {
        if (isClick) {
            return;
        }
        if (mTouchActionUpListener != null && mTouchActionUpListener.actionUp(mViewController, upX, upY)) {
            return;
        }


    }
}
