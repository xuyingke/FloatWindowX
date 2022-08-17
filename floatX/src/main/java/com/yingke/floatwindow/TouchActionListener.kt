package com.yingke.floatwindow

import android.view.MotionEvent

/**
 * 对浮窗的手势行为进行回调，以方便业务层做一些类似埋点的处理。
 */
interface TouchActionListener {

    /**
     * 开始滑动
     */
    fun onTouchStart(floatConfig: FloatConfig?, event: MotionEvent)

    /**
     * 滑动中
     */
    fun onTouchMove(floatConfig: FloatConfig?, event: MotionEvent)

    /**
     * 松手
     *
     * 拥有 controller 就几乎可以操作这个浮窗的所有了。但是我只建议你操作位置，其他的不要动。
     *
     * @param controller  包含浮窗所有的信息了。
     * @param event MotionEvent
     * @return 是否自己处理  返回 true 我就不管了。
     */
    fun onTouchUp(controller: FloatViewController?, event: MotionEvent?): Boolean

    /**
     * 松手后，动画执行结束
     */
    fun onAnimatorEnd(floatConfig: FloatConfig?)

}