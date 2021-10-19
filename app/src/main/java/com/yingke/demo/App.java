package com.yingke.demo;

import android.app.Application;
import android.view.View;

public class App extends Application {

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FloatX.get().init(App.get());
    }

    public static App get() {
        return mInstance;
    }
}
