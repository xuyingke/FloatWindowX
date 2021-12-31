package com.yingke.floatwindow;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FloatActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private int mActivityAmount = 0;

    public FloatActivityLifecycleCallbacks() {

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        FloatX.get()
                .onActivityCreated(activity, savedInstanceState);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        mActivityAmount++;
        FloatX.get()
                .onActivityResumed(mActivityAmount, activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        mActivityAmount--;
        FloatX.get()
                .onActivityStopped(mActivityAmount, activity);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
