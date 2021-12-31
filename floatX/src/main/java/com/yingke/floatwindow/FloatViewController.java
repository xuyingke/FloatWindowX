package com.yingke.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;


import androidx.annotation.NonNull;

/**
 * 对 view 的一些操作
 */
public class FloatViewController {

    private FloatConfig mFloatConfig;
    private final WindowManager mWindowManager;
    private final WindowManager.LayoutParams mLayoutParams;

    public FloatViewController(@NonNull Context applicationContext) {
        mWindowManager = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParams.windowAnimations = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        mLayoutParams.gravity = Gravity.TOP | Gravity.START;
    }

    private void addView(@NonNull FloatConfig floatConfig) {
        mFloatConfig = floatConfig;
        View floatView = floatConfig.getFloatView();
        if (floatView == null || floatView.getParent() != null) {
            return;
        }
        mLayoutParams.x = mFloatConfig.getRawX();
        mLayoutParams.y = mFloatConfig.getRawY();
        mLayoutParams.width = mFloatConfig.getFloatViewWidth();
        mLayoutParams.height = mFloatConfig.getFloatViewHeight();
        try {
            mWindowManager.addView(floatView, mLayoutParams);
            floatView.setOnTouchListener(new FloatViewTouchListener(this));
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
                FloatXLog.e("添加 view 错误，" + e.getMessage());
            }
        }
    }

    public void show() {
        if (mFloatConfig == null) {
            return;
        }
        View floatView = mFloatConfig.getFloatView();
        if (floatView == null) {
            return;
        }
        if (floatView.getParent() == null) {
            addView(mFloatConfig);
        } else {
            floatView.setVisibility(View.VISIBLE);
        }
    }


    public void hidden() {
        if (mFloatConfig == null) {
            return;
        }
        View floatView = mFloatConfig.getFloatView();
        if (floatView == null) {
            return;
        }
        floatView.setVisibility(View.GONE);
    }


    private void updateViewLocation(View view, int x, int y) {
        mLayoutParams.x = x;
        mLayoutParams.y = y;
        mFloatConfig.setRawX(x)
                .setRawY(y);
        mWindowManager.updateViewLayout(view, mLayoutParams);
    }

    public void updateViewLocation(float x, float y) {
        updateViewLocation(mFloatConfig.getFloatView(), (int) x, (int) y);
    }

    public FloatConfig getFloatBuilder() {
        return mFloatConfig;
    }

    public void setFloatBuilder(@NonNull FloatConfig floatConfig) {
        this.mFloatConfig = floatConfig;
    }


    public void close() {
        View view = null;
        if (mFloatConfig != null) {
            mFloatConfig.setShow(false);
            view = mFloatConfig.getFloatView();
        }
        if (mWindowManager != null && view != null) {
            mWindowManager.removeView(view);
        }
    }

}
