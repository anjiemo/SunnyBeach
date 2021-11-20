package cn.android52.sunnybeach.skin.util

import androidx.appcompat.app.AppCompatActivity
import cn.android52.sunnybeach.skin.callback.ISkinChangedListener
import cn.android52.sunnybeach.skin.manager.SkinManager

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/19
 *    desc   : AppCompatActivity 支持换肤的扩展函数
 */

/**
 * 劫持 AppCompatActivity ，使其拥有换肤功能，必须在 AppCompatActivity 的 onCreate 方法之前调用
 */
fun AppCompatActivity.hookActivity(action: ISkinChangedListener) {
    val manager = SkinManager.instance
    manager.registerListener(action)
    SkinManager.hookActivity(this, action)
}

/**
 * 取消 AppCompatActivity 的监听，避免内存泄漏，请在 AppCompatActivity 的 onDestroy 方法中调用
 */
fun AppCompatActivity.removeActivityHook(action: ISkinChangedListener) {
    val manager = SkinManager.instance
    manager.unRegisterListener(action)
}