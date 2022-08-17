package com.yingke.floatwindow

import android.app.Application.ActivityLifecycleCallbacks
import android.app.Activity
import android.os.Bundle

class FloatActivityLifecycleCallbacks : ActivityLifecycleCallbacks {

    private var activityAmount = 0

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        FloatX.get()
            .onActivityCreated(activity, savedInstanceState)
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        activityAmount = activityAmount.inc()
        FloatX.get()
            .onActivityResumed(activityAmount, activity)
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {
        activityAmount = activityAmount.dec()
        FloatX.get()
            .onActivityStopped(activityAmount, activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}
}