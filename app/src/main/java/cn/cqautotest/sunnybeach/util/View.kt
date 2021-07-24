@file:JvmName("ViewUtils")

package cn.cqautotest.sunnybeach.util

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View

@JvmOverloads
fun View.setRoundRectBg(color: Int = Color.WHITE, cornerRadius: Int = 15.dp) {
    background = GradientDrawable().apply {
        setColor(color)
        setCornerRadius(cornerRadius.toFloat())
    }
}
