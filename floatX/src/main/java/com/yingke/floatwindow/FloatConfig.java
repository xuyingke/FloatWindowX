package com.yingke.floatwindow;

import android.app.Activity;
import android.view.View;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * 单个悬浮窗的配置信息
 */
public final class FloatConfig implements Serializable {

    private String mTag;
    private View mFloatView;
    private int mRawX;
    private int mRawY;
    private int mFloatViewWidth;
    private int mFloatViewHeight;
    private List<Class<?>> mNotDisplayActivities;
    private List<Class<?>> mCloseActivities;
    private TouchActionUpListener mTouchActionUpListener;
    /**
     * 是否可见。isHidden == true 或者 view 被 remove 都是不可见状态。
     */
    private boolean isShow;

    /**
     * 是否是隐藏。view 被设置为 gone 才是 true。
     */
    private boolean isHidden;

    /**
     * 是否在桌面也显示
     */
    private boolean mDesktopShow;

    public FloatConfig(@NonNull View floatView, @NonNull String tag) {
        this.mTag = tag;
        setFloatView(floatView);
    }

    public String getTag() {
        return mTag;
    }


    public View getFloatView() {
        return mFloatView;
    }

    private void setFloatView(View mFloatView) {
        this.mFloatView = mFloatView;
    }

    public int getRawX() {
        return mRawX;
    }

    public FloatConfig setRawX(int rawx) {
        this.mRawX = rawx;
        return this;
    }

    public int getRawY() {
        return mRawY;
    }

    public FloatConfig setRawY(int rawY) {
        this.mRawY = rawY;
        return this;
    }

    public List<Class<?>> getNotDisplayActivities() {
        return mNotDisplayActivities;
    }

    public FloatConfig setNotDisplayActivities(List<Class<?>> mNotDisplayActivities) {
        this.mNotDisplayActivities = mNotDisplayActivities;
        return this;
    }

    public List<Class<?>> getCloseActivities() {
        return mCloseActivities;
    }

    public FloatConfig setCloseActivities(List<Class<?>> mCloseActivities) {
        this.mCloseActivities = mCloseActivities;
        return this;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isDesktopShow() {
        return mDesktopShow;
    }

    public void setDesktopShow(boolean desktopShow) {
        this.mDesktopShow = desktopShow;
    }

    public void setTouchActionUpListener(TouchActionUpListener listener) {
        this.mTouchActionUpListener = listener;
    }

    public TouchActionUpListener getTouchActionUpListener() {
        return mTouchActionUpListener;
    }

    public int getFloatViewWidth() {
        return mFloatViewWidth;
    }

    public FloatConfig setFloatViewWidth(int floatViewWidth) {
        this.mFloatViewWidth = floatViewWidth;
        return this;
    }

    public int getFloatViewHeight() {
        return mFloatViewHeight;
    }

    public FloatConfig setFloatViewHeight(int floatViewHeight) {
        this.mFloatViewHeight = floatViewHeight;
        return this;
    }

    public boolean isNeedHidden(@NonNull Activity activity) {
        List<Class<?>> notDisplayActivities = mNotDisplayActivities;
        if (notDisplayActivities == null
                || notDisplayActivities.size() == 0
                || notDisplayActivities.get(0) == null) {
            return false;
        }
        synchronized (this) {
            for (Class<?> clazz : notDisplayActivities) {
                if (activity.getClass() == clazz) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isNeedClose(@NonNull Activity activity) {
        List<Class<?>> closeActivities = mCloseActivities;
        if (closeActivities == null
                || closeActivities.size() == 0
                || closeActivities.get(0) == null) {
            return false;
        }
        synchronized (this) {
            for (Class<?> clazz : closeActivities) {
                if (activity.getClass() == clazz) {
                    return true;
                }
            }
        }
        return false;
    }
}
