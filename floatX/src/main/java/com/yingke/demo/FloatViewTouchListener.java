package com.yingke.demo;

import android.view.MotionEvent;
import android.view.View;

public class FloatViewTouchListener implements View.OnTouchListener {

    private FloatViewController mViewController;
    private FloatConfig mFloatConfig;

    private float downX, downY, lastX, lastY, changeX, changeY;

    private float mFloatViewWidth, mFloatViewHeight;
    private View mFloatView;
    private boolean mClick;
    private float upX, upY;

    private float mLastViewX, mLastViewY;

    public FloatViewTouchListener(FloatViewController viewController) {
        this.mViewController = viewController;
        mFloatConfig = mViewController.getFloatBuilder();
        mFloatViewWidth = mFloatConfig.getFloatViewWidth();
        mFloatViewHeight = mFloatConfig.getFloatViewHeight();
        mFloatView = mFloatConfig.getFloatView();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();

                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                changeX = event.getRawX() - lastX;
                changeY = event.getRawY() - lastY;
                mViewController.updateViewLocation(event.getX() - mFloatConfig.getRawX()
                        , event.getY() - mFloatConfig.getRawY());
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getRawX();
                upY = event.getRawY();
                mClick = (Math.abs(upX - downX) > 0) || (Math.abs(upY - downY) > 0);
                break;
            default:
                break;
        }
        return mClick;
    }
}
