@file:JvmName("ActivityUtils")

package cn.cqautotest.sunnybeach.ktx

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.KeyboardUtils
import org.lsposed.hiddenapibypass.HiddenApiBypass

fun AppCompatActivity.hideSupportActionBar() = supportActionBar?.hide()

fun Activity.hideKeyboard() {
    KeyboardUtils.hideSoftInput(this)
}

val Activity.decorView
    get() = window.decorView

fun Activity.resetConfiguration() = resources.resetConfiguration()

/**
 * 重置界面配置，解决系统字体过大或者过小时导致的界面错乱问题
 */
fun Resources.resetConfiguration() {
    val config = Configuration()
    config.setToDefaults()
    updateConfiguration(config, displayMetrics)
}

/**
 * 备份系统由于进入后台（onStop）而被销毁的 EnterTransitionCoordinator
 * 用于修复由于 Android Framework 官方陈年 Bug 导致的退场共享动画丢失问题
 *
 * 源码链接：[ActivityTransitionState.java](https://cs.android.com/android/platform/superproject/+/android-latest-release:frameworks/base/core/java/android/app/ActivityTransitionState.java?q=ActivityTransitionState)
 *
 * 实现思路：
 * 1. 当 Activity 触发 [Activity.onStop] 时，系统内部的 `ActivityTransitionState` 会强制清除 `mEnterTransitionCoordinator` 引用，导致退场共享动画所需的状态丢失。
 * 2. 为了解决这个问题，我们在 `onStop` 生命周期前，通过反射提前将这个 Coordinator 对象拦截并备份下来。
 * 3. 由于 `mEnterTransitionCoordinator` 从 Android 9 开始被列入深灰名单（访问会直接抛出 `NoSuchFieldException`），这里使用 `HiddenApiBypass` 库强行绕过底层的反射拦截机制。
 */
@SuppressLint("DiscouragedPrivateApi")
fun Activity.backupEnterTransitionCoordinator(): Any? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        HiddenApiBypass.addHiddenApiExemptions("Landroid/app/ActivityTransitionState;")
    }
    return try {
        val stateField = Activity::class.java.getDeclaredField("mActivityTransitionState")
        stateField.isAccessible = true
        val state = stateField.get(this)
        if (state != null) {
            val coordinatorField = state.javaClass.getDeclaredField("mEnterTransitionCoordinator")
            coordinatorField.isAccessible = true
            coordinatorField.get(state)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 恢复系统由于进入后台（onStop）而被销毁的 EnterTransitionCoordinator
 *
 * 实现思路：
 * 1. 在 Activity 重新恢复到前台（[Activity.onStart]）时，将我们之前备份好的“僵尸” Coordinator 对象，重新反射注入回 `ActivityTransitionState` 中。
 * 2. 此举能成功骗过系统底层的拦截判定，使 `startExitBackTransition` 返回 true，从而能够顺利唤起 `HomeActivity` 重新执行退出时的共享元素返回动画。
 */
@SuppressLint("DiscouragedPrivateApi")
fun Activity.restoreEnterTransitionCoordinator(savedCoordinator: Any?) {
    if (savedCoordinator == null) return
    try {
        val stateField = Activity::class.java.getDeclaredField("mActivityTransitionState")
        stateField.isAccessible = true
        val state = stateField.get(this)
        if (state != null) {
            val coordinatorField = state.javaClass.getDeclaredField("mEnterTransitionCoordinator")
            coordinatorField.isAccessible = true
            if (coordinatorField.get(state) == null) {
                coordinatorField.set(state, savedCoordinator)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
