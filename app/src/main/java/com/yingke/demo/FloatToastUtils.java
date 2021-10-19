package com.yingke.demo;

import android.widget.Toast;

public class FloatToastUtils {

    private static Toast mToast;

    public synchronized static void show(String msg) {
        check();
        mToast.setText(msg);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    private static void check() {
        if (mToast == null) {
            mToast = Toast.makeText(App.get(), "", Toast.LENGTH_SHORT);
        } else {
            mToast.cancel();
            mToast = null;
            mToast = Toast.makeText(App.get(), "", Toast.LENGTH_SHORT);
        }
    }

}
