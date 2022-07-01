package cn.cqautotest.sunnybeach.ktx

import android.content.res.ColorStateList
import android.widget.ProgressBar

fun ProgressBar.setTintColor(color: Int) {
    indeterminateTintList = ColorStateList.valueOf(color)
}