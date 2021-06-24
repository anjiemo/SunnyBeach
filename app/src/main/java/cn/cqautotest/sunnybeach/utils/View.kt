package cn.cqautotest.sunnybeach.utils

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View

fun View.setRoundRectBg(color: Int = Color.WHITE, cornerRadius: Int = 15.dp) {
    background = GradientDrawable().apply {
        setColor(color)
        setCornerRadius(cornerRadius.toFloat())
    }
}
