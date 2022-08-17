package com.yingke.floatwindow

import android.util.Log

object FloatXLog {

    @JvmStatic
    fun d(msg: String?) {
        if (FloatX.get().isDebugEnabled) {
            msg?.let {
                Log.d("FloatXLog", msg)
            }
        }
    }

    @JvmStatic
    fun e(msg: String?) {
        if (FloatX.get().isDebugEnabled) {
            msg?.let {
                Log.e("FloatXLog", msg)
            }
        }
    }

    @JvmStatic
    fun w(msg: String?) {
        if (FloatX.get().isDebugEnabled) {
            msg?.let {
                Log.w("FloatXLog", msg)
            }
        }
    }
}