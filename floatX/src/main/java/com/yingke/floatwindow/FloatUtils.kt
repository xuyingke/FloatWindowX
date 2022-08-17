package com.yingke.floatwindow

import android.content.Context
import android.graphics.Point
import android.util.*
import androidx.collection.SimpleArrayMap
import android.view.WindowManager
import android.view.Display
import java.lang.reflect.Array

object FloatUtils {
    @JvmStatic
    fun isEmpty(obj: Any?): Boolean {
        if (obj == null) {
            return true
        }
        if (obj.javaClass.isArray && Array.getLength(obj) == 0) {
            return true
        }
        if (obj is CharSequence && obj.toString().isEmpty()) {
            return true
        }
        if (obj is Collection<*> && obj.isEmpty()) {
            return true
        }
        if (obj is Map<*, *> && obj.isEmpty()) {
            return true
        }
        if (obj is SimpleArrayMap<*, *> && obj.isEmpty) {
            return true
        }
        if (obj is SparseArray<*> && obj.size() == 0) {
            return true
        }
        if (obj is SparseBooleanArray && obj.size() == 0) {
            return true
        }
        if (obj is SparseIntArray && obj.size() == 0) {
            return true
        }
        if (obj is SparseLongArray && obj.size() == 0) {
            return true
        }
        if (obj is LongSparseArray<*> && obj.size() == 0) {
            return true
        }
        return (obj is LongSparseArray<*>
                && obj.size() == 0)
    }

    @JvmStatic
    fun getWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val defaultDisplay = wm.defaultDisplay
        val point = Point()
        defaultDisplay.getSize(point)
        return point.x
    }

    @JvmStatic
    fun getHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val defaultDisplay = wm.defaultDisplay
        val point = Point()
        defaultDisplay.getSize(point)
        return point.y
    }

    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}