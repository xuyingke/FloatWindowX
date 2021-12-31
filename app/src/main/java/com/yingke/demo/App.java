package com.yingke.demo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class App extends Application {

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        // 建议这种方式初始化，其实只是初始化俩变量和一个监听。不会影响性能。
        // 初始化方式 1
        FloatX.get().init(App.get());

        // 初始化方式 2
        // 相比第一种是去除了 lifecycle 的注册。
        // FloatX.get().initSimple(this);
        // registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
        //     private int mActivityAmount = 0;
        //
        //     @Override
        //     public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        //         FloatX.get().onActivityCreated(activity, savedInstanceState);
        //     }
        //
        //     @Override
        //     public void onActivityStarted(@NonNull Activity activity) {
        //
        //     }
        //
        //     @Override
        //     public void onActivityResumed(@NonNull Activity activity) {
        //         mActivityAmount++;
        //         if (mActivityAmount == 1) {
        //             FloatX.get().desktopBack(activity);
        //         } else {
        //             FloatX.get().onResumed(activity);
        //         }
        //     }
        //
        //     @Override
        //     public void onActivityPaused(@NonNull Activity activity) {
        //
        //     }
        //
        //     @Override
        //     public void onActivityStopped(@NonNull Activity activity) {
        //         mActivityAmount--;
        //         try {
        //             // 应用退到后台
        //             if (mActivityAmount == 0) {
        //                 FloatX.get().desktop();
        //             } else {
        //                 FloatX.get().onActivityStopped(activity);
        //             }
        //         } catch (Exception e) {
        //             if (FloatX.get().isDebugEnabled()) {
        //                 e.printStackTrace();
        //             }
        //         }
        //     }
        //
        //     @Override
        //     public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        //
        //     }
        //
        //     @Override
        //     public void onActivityDestroyed(@NonNull Activity activity) {
        //
        //     }
        // });
    }

    public static App get() {
        return mInstance;
    }
}
