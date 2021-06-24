package cn.cqautotest.sunnybeach.utils

import android.content.res.ColorStateList
import android.widget.ProgressBar

fun ProgressBar.setTintColor(color: Int) {
    indeterminateTintList = ColorStateList.valueOf(color)
}