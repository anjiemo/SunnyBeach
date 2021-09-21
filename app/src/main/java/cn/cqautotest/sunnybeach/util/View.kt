@file:JvmName("ViewUtils")

package cn.cqautotest.sunnybeach.util

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewConfiguration

@JvmOverloads
fun View.setRoundRectBg(color: Int = Color.WHITE, cornerRadius: Int = 15.dp) {
    background = GradientDrawable().apply {
        setColor(color)
        setCornerRadius(cornerRadius.toFloat())
    }
}

private var lastClickTime: Long = 0
private val jumpTapTimeout = ViewConfiguration.getJumpTapTimeout()

fun View.setFixOnClickListener(block: (v: View) -> Unit) {
    setOnClickListener {
        val currentClickTime = System.currentTimeMillis()
        val diffTime = currentClickTime - lastClickTime
        if (diffTime > jumpTapTimeout) {
            block.invoke(this)
        }
        lastClickTime = currentClickTime
    }
}