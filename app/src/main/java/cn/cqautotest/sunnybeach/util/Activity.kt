@file:JvmName("ActivityUtils")

package cn.cqautotest.sunnybeach.util

import android.app.Activity
import com.blankj.utilcode.util.KeyboardUtils

fun Activity.hideKeyboard() {
    KeyboardUtils.hideSoftInput(this)
}