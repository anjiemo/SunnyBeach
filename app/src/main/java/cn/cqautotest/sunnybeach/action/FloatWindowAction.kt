package cn.cqautotest.sunnybeach.action

import android.app.Activity
import android.view.View
import cn.cqautotest.sunnybeach.util.FloatWindowHelper

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/09/25
 * desc   : 悬浮窗意图
 */
interface FloatWindowAction {

    /**
     * 附加到指定 Activity，action 为按钮点击的回调
     */
    fun Activity.attachFloatWindow(action: (View) -> Unit) {
        FloatWindowHelper.attachTo(this, action)
    }

    /**
     * 显示来自 Activity 的悬浮窗
     */
    fun Activity.showFloatWindow() {
        FloatWindowHelper.showFrom(this)
    }

    /**
     * 隐藏发布摸鱼动态悬浮窗
     */
    fun Activity.hideFloatWindow() {
        FloatWindowHelper.hideFrom(this)
    }

    /**
     * 移除发布摸鱼动态悬浮窗
     */
    fun Activity.deathFloatWindow() {
        FloatWindowHelper.deathFrom(this)
    }
}