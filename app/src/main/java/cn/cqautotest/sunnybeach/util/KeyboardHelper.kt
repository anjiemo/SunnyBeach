package cn.cqautotest.sunnybeach.util

import android.app.Activity
import android.graphics.Rect
import android.view.ViewTreeObserver

// 初始的 window 高度
private var mWindowHeight = 0

var softKeyboardHeight = 0

fun Activity.getSoftKeyboardListener(): ViewTreeObserver.OnGlobalLayoutListener {
    return ViewTreeObserver.OnGlobalLayoutListener {
        val r = Rect()
        // 获取当前窗口实际的可见性区域
        window.decorView.getWindowVisibleDisplayFrame(r)
        val height = r.height()
        if (mWindowHeight == 0) {
            mWindowHeight = height
        } else {
            if (mWindowHeight != height) {
                // 两次 window 的高度差即为软键盘的高度
                softKeyboardHeight = mWindowHeight - height
            }
        }
    }
}

private val Activity.decorView
    get() = window.decorView

private val Activity.viewTreeObserver
    get() = decorView.viewTreeObserver

fun Activity.registerSoftKeyboardListener(listener: ViewTreeObserver.OnGlobalLayoutListener) {
    viewTreeObserver.addOnGlobalLayoutListener(listener)
}

fun Activity.unRegisterSoftKeyboardListener(listener: ViewTreeObserver.OnGlobalLayoutListener) {
    viewTreeObserver.removeOnGlobalLayoutListener(listener)
}