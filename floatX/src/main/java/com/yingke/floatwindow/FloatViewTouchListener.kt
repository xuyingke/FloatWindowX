package com.yingke.floatwindow

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.yingke.floatwindow.FloatUtils.getHeight
import com.yingke.floatwindow.FloatUtils.getWidth
import kotlin.math.abs

class FloatViewTouchListener(private val mViewController: FloatViewController) : View.OnTouchListener {

    private val TAG = "FloatViewTouchListener"

    private val floatConfig: FloatConfig? = mViewController.floatBuilder
    private var downX = 0f
    private var downY = 0f
    private var lastX = 0f
    private var lastY = 0f
    private var changeX = 0f
    private var changeY = 0f
    private var floatViewWidth = 0f
    private var floatViewHeight = 0f
    private val floatView: View? = floatConfig?.floatView
    private var click = true
    private var upX = 0f
    private var upY = 0f
    private val touchActionUpListener: TouchActionListener? = floatConfig?.touchActionUpListener
    private var displayWidth = -1
    private var displayHeight = -1


    init {
        if (floatConfig != null) {
            floatViewWidth = floatConfig.floatViewWidth.toFloat()
            floatViewHeight = floatConfig.floatViewHeight.toFloat()
        }
        if (floatView != null) {
            displayWidth = getWidth(floatView.context)
            displayHeight = getHeight(floatView.context)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchActionUpListener?.onTouchStart(floatConfig, event)
                downX = event.rawX
                downY = event.rawY
                lastX = event.rawX
                lastY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                touchActionUpListener?.onTouchMove(floatConfig, event)
                changeX = event.rawX - lastX
                changeY = event.rawY - lastY

                var newX = if (floatConfig == null) 0F else floatConfig.rawX + changeX
                var newY = if (floatConfig == null) 0F else floatConfig.rawY + changeY

                // 边缘处理
                if (displayWidth > 0 && displayHeight > 0) {
                    newX = if (newX < 0) 0F else newX
                    newX = if (newX + floatViewWidth > displayWidth) displayWidth - floatViewWidth else newX
                    newY = if (newY < 0) 0F else newY
                    newY = if (newY + floatViewHeight > displayHeight) displayHeight - floatViewHeight else newY
                }
                FloatXLog.d(
                    TAG + "newX= " + newX + " newY= " + newY
                            + " viewWidth= " + floatViewWidth + " viewHeight= " + floatViewHeight
                )
                mViewController.updateViewLocation(newX, newY)
                lastX = event.rawX
                lastY = event.rawY
            }
            MotionEvent.ACTION_UP -> {
                upX = event.rawX
                upY = event.rawY
                click = abs(upX - downX) <= 0 || abs(upY - downY) <= 0
                handleAnimator(click, v, event)
            }
            else -> {}
        }
        FloatXLog.d("FloatViewTouchListener" + "action= " + event.action + " mClick= " + click)
        return !click
    }

    /**
     * 处理抬手后的动画
     *
     * @param isClick 是否是点击事件
     */
    private fun handleAnimator(isClick: Boolean, view: View, event: MotionEvent) {
        if (isClick) {
            return
        }
        if (touchActionUpListener?.onTouchUp(mViewController, event) == true) {
            return
        }
        checkEdge()
    }

    fun checkEdge() {
        val displayWidth = displayWidth
        val floatViewWidth = floatViewWidth
        val rawX = mViewController.floatBuilder.rawX
        val rawY = mViewController.floatBuilder.rawY
        val width = if (mViewController.floatBuilder?.floatView?.width == null) 0
        else mViewController.floatBuilder?.floatView?.width
        val endX: Float = if (rawX * 2 + width!! >
            displayWidth
        ) displayWidth - floatViewWidth else 0F
        val mAnimator = ObjectAnimator.ofFloat(rawX.toFloat(), endX)
        mAnimator.duration = 300
        mAnimator.interpolator = DecelerateInterpolator()
        mAnimator.addUpdateListener { animation ->
            val x = animation.animatedValue as Float
            mViewController.updateViewLocation(x, rawY.toFloat())
        }
        mAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                floatView?.isClickable = false
            }

            override fun onAnimationEnd(animation: Animator) {
                floatView?.isClickable = true
                if (animation is ValueAnimator) {
                    animation.removeAllUpdateListeners()
                    animation.removeAllListeners()
                }
                touchActionUpListener?.onAnimatorEnd(floatConfig)
            }
        })
        mAnimator.start()
    }

}