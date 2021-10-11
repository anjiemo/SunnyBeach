@file:JvmName("ViewUtils")

package cn.cqautotest.sunnybeach.util

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.blankj.utilcode.util.TouchUtils

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

fun View.setSlidingUpListener(block: (v: View) -> Unit) {
    object : TouchUtils.OnTouchUtilsListener() {
        override fun onDown(view: View?, x: Int, y: Int, event: MotionEvent?): Boolean {
            return false
        }

        override fun onMove(
            view: View?,
            direction: Int,
            x: Int,
            y: Int,
            dx: Int,
            dy: Int,
            totalX: Int,
            totalY: Int,
            event: MotionEvent?
        ): Boolean {
            return false
        }

        override fun onStop(
            view: View,
            direction: Int,
            x: Int,
            y: Int,
            totalX: Int,
            totalY: Int,
            vx: Int,
            vy: Int,
            event: MotionEvent?
        ): Boolean {
            block.invoke(view)
            return true
        }
    }
}
