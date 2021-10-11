package cn.cqautotest.sunnybeach.util

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

class SimpleGesture(
    private val minDistance: Int,
    private val listener: OnSlideListener
) :
    GestureDetector.SimpleOnGestureListener() {

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        // 判断是触发左右还是上下
        val horizontalDistance = abs(e1.x - e2.x)
        val verticalDistance = abs(e1.y - e2.y)
        // 如果横向滑动距离大于纵向滑动距离，则在横向方向上继续判断，否则在纵向方向上继续判断
        if (horizontalDistance > verticalDistance) {
            // 如果横向的滑动距离大于最小滑动距离
            if ((horizontalDistance > minDistance)) {
                if (e1.x - e2.x > minDistance) {
                    // 向左滑动
                    listener.onSwipeLeft()
                } else if (e1.x - e2.x < minDistance) {
                    // 向右滑动
                    listener.onSwipeRight()
                }
            }
        } else {
            // 如果纵向的滑动距离大于最小滑动距离
            if (verticalDistance > minDistance) {
                if (e1.y - e2.y > minDistance) {
                    // 向上滑动
                    listener.onSwipeUp()
                } else if (e1.y - e2.y < minDistance) {
                    // 向下滑动
                    listener.onSwipeDown()
                }
            }
        }
        return false
    }

    interface OnSlideListener {
        fun onSwipeLeft() {}

        fun onSwipeRight() {}

        fun onSwipeUp() {}

        fun onSwipeDown() {}
    }
}