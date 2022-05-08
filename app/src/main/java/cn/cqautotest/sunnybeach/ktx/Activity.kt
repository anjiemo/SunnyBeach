@file:JvmName("ActivityUtils")

package cn.cqautotest.sunnybeach.ktx

import android.app.Activity
import com.blankj.utilcode.util.KeyboardUtils

fun Activity.hideKeyboard() {
    KeyboardUtils.hideSoftInput(this)
}

val Activity.decorView
    get() = window.decorView