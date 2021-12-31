package com.yingke.floatwindow;

import android.view.MotionEvent;

import androidx.annotation.Nullable;

public interface TouchActionUpListener {

    /**
     * 用户在悬浮窗上松手。
     * 拥有 controller 就几乎可以操作这个浮窗的所有了。但是我只建议你操作位置，其他的不要动。
     *
     * @param controller  包含浮窗所有的信息了。
     * @param event MotionEvent
     * @return 是否自己处理  返回 true 我就不管了。
     */
    boolean actionUp(@Nullable FloatViewController controller, MotionEvent event);

}
