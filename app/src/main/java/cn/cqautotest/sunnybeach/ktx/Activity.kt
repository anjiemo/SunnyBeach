@file:JvmName("ActivityUtils")

package cn.cqautotest.sunnybeach.ktx

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import com.blankj.utilcode.util.KeyboardUtils

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
