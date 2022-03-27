@file:JvmName("ViewUtils")

package cn.cqautotest.sunnybeach.util

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.blankj.utilcode.util.TouchUtils

/**
 * 设置哀悼风格
 */
fun View.setMourningStyle() {
    val paint = Paint()
    val cm = ColorMatrix()
    cm.setSaturation(0f)
    paint.colorFilter = ColorMatrixColorFilter(cm)
    setLayerType(View.LAYER_TYPE_HARDWARE, paint)
}

/**
 * 清除哀悼风格
 */
fun View.removeMourningStyle() {
    setLayerType(View.LAYER_TYPE_NONE, Paint())
}

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

fun View.setDoubleClickListener(block: (v: View) -> Unit) {
    setOnClickListener {
        val currentClickTime = System.currentTimeMillis()
        val diffTime = currentClickTime - lastClickTime
        if (diffTime < jumpTapTimeout) {
            block.invoke(this)
        }
        lastClickTime = currentClickTime
    }
}

fun View.setSlidingUpListener(block: (v: View) -> Unit) {
    object : TouchUtils.OnTouchUtilsListener() {
        override fun onDown(view: View?, x: Int, y: Int, event: MotionEvent?): Boolean {
            return true
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
