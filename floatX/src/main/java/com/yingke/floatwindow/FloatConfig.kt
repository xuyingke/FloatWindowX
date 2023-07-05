package com.yingke.floatwindow

import android.app.Activity
import android.view.View
import java.io.Serializable

/**
 * 单个悬浮窗的配置信息
 */
class FloatConfig(floatView: View, val tag: String) : Serializable {
    var floatView: View? = null
        private set
    var rawX = 0
        private set
    var rawY = 0
        private set
    var floatViewWidth = 0
        private set
    var floatViewHeight = 0
        private set
    private var notDisplayActivities = listOf<Class<*>>()
    var closeActivities = listOf<Class<*>>()
        private set
    var touchActionUpListener: TouchActionListener? = null

    // 是否可见。isHidden == true 或者 view 被 remove 都是不可见状态。
    var isShow = false

    // 是否是隐藏。view 被设置为 gone 才是 true。
    var isHidden = false

    // 是否在桌面也显示
    var isDesktopShow = false

    // 无权限模式是否开启
    var isOpenNotPermissionMode = false

    // 无权限模式下需要单独显示的页面
    var notPermissionDisplayActivities = listOf<Class<*>>()

    init {
        setFloatView(floatView)
    }

    private fun setFloatView(floatView: View) {
        this.floatView = floatView
    }

    fun setRawX(rawx: Int): FloatConfig {
        rawX = rawx
        return this
    }

    fun setRawY(rawY: Int): FloatConfig {
        this.rawY = rawY
        return this
    }

    fun setNotDisplayActivities(notDisplayActivities: List<Class<*>>): FloatConfig {
        this.notDisplayActivities = notDisplayActivities as ArrayList<Class<*>>
        return this
    }

    fun setCloseActivities(closeActivities: List<Class<*>>): FloatConfig {
        this.closeActivities = closeActivities
        return this
    }

    fun setFloatViewWidth(floatViewWidth: Int): FloatConfig {
        this.floatViewWidth = floatViewWidth
        return this
    }

    fun setFloatViewHeight(floatViewHeight: Int): FloatConfig {
        this.floatViewHeight = floatViewHeight
        return this
    }

    fun isNeedHidden(activity: Activity): Boolean {
        val notDisplayActivities = notDisplayActivities
        if (notDisplayActivities.isEmpty()) {
            return false
        }
        synchronized(this) {
            notDisplayActivities.forEach {
                if (activity.javaClass == it) {
                    return true
                }
            }
        }
        return false
    }

    fun isNeedClose(activity: Activity): Boolean {
        val closeActivities = closeActivities
        if (closeActivities.isEmpty()) {
            return false
        }
        synchronized(this) {
            closeActivities.forEach {
                if (activity.javaClass == it) {
                    return true
                }
            }
        }
        return false
    }

}