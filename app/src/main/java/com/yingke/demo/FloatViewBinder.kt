package com.yingke.demo

import android.content.Context
import android.view.View
import android.widget.TextView

class FloatViewBinder(context: Context, var tag: String) {

    val floatView: View by lazy {
        View.inflate(context, R.layout.layout_float_view, null)
    }

    var num: Int = 0
        set(value) {
            field = value
            floatView.findViewById<TextView>(R.id.tv_text)
                ?.text = "$tag\n点了$num 次"
        }

    init {
        num = 0
    }

    fun incNum() {
        num++
    }
}