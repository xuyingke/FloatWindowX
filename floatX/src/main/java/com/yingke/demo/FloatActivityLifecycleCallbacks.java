package com.yingke.demo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FloatActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private int mActivityAmount = 0;

    public FloatActivityLifecycleCallbacks() {

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        FloatXLog.d("onActivityCreated" + " mActivityAmount= "
                + mActivityAmount + " 页面 " + activity.getClass().getName());
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        FloatXLog.d("onActivityStarted" + " mActivityAmount= "
                + mActivityAmount + " 页面 " + activity.getClass().getName());

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        mActivityAmount++;
        FloatXLog.d("onActivityResumed" + " mActivityAmount= "
                + mActivityAmount + " 页面 " + activity.getClass().getName());
        if (mActivityAmount == 1) {
            FloatX.get().desktopBack(activity);
        } else {
            FloatX.get().onResumed(activity);
        }

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        FloatXLog.d("onActivityPaused" + " mActivityAmount= "
                + mActivityAmount + " 页面 " + activity.getClass().getName());
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        mActivityAmount--;
        FloatXLog.d("onActivityStopped" + " mActivityAmount= "
                + mActivityAmount + " 页面 " + activity.getClass().getName());
        try {
            // 应用退到后台
            if (mActivityAmount == 0) {
                FloatX.get().desktop();
            } else {
                FloatX.get().onActivityStopped(activity);
            }
        } catch (Exception e) {
            if (FloatX.get().isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        FloatXLog.d("onActivitySaveInstanceState" + " mActivityAmount= "
                + mActivityAmount + " 页面 " + activity.getClass().getName());

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        FloatXLog.d("onActivityDestroyed" + " mActivityAmount= "
                + mActivityAmount + " 页面 " + activity.getClass().getName());

    }
}
