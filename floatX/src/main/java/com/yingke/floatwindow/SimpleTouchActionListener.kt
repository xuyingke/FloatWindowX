package com.yingke.floatwindow

import android.view.MotionEvent

open class SimpleTouchActionListener : TouchActionListener {

    override fun onTouchStart(floatConfig: FloatConfig?, event: MotionEvent) {

    }

    override fun onTouchMove(floatConfig: FloatConfig?, event: MotionEvent) {
    }

    override fun onTouchUp(controller: FloatViewController?, event: MotionEvent?): Boolean {
        return false
    }

    override fun onAnimatorEnd(floatConfig: FloatConfig?) {
    }


}