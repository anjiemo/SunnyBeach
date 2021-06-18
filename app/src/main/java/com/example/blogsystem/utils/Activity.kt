package com.example.blogsystem.utils

import android.app.Activity
import android.graphics.Color
import android.view.View

fun Activity.fullWindow(isBlack: Boolean = true) {
    val decorView = window.decorView
    decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.statusBarColor = Color.TRANSPARENT
    var ui = decorView.systemUiVisibility
    ui = if (isBlack) {
        // 设置状态栏中字体的颜色为黑色
        ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        // 设置状态栏中字体的颜色为白色
        ui or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
    decorView.systemUiVisibility = ui
}