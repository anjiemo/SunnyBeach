package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import timber.log.Timber
import java.lang.reflect.Field
import kotlin.math.abs

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2023/03/06
 * desc   : 修复 AppBarLayout 导致的滑动抖动相关问题
 * 1. Fling 状态下反向滑动导致的抖动
 * 2. 吸顶状态拉出时的偏移突变
 * 3. 嵌套滑动过程中的物理打断冲突
 */
class AppBarLayoutBehavior(context: Context, attrs: AttributeSet?) : AppBarLayout.Behavior(context, attrs) {

    private var scrollerField: Field? = null
    private var isTouchInProgress = false
    private var lastAcceptedOffset = Int.MIN_VALUE
    private var lastTrend = TREND_NONE // 最近一次被接受的变化方向

    // 基于系统配置动态计算阈值
    private val mTouchSlop = android.view.ViewConfiguration.get(context).scaledTouchSlop
    private val mJitterThreshold = mTouchSlop / 4
    private val mSignificantMoveThreshold = mTouchSlop / 8

    init {
        findScrollerField()
    }

    private fun findScrollerField() {
        try {
            var clazz: Class<*>? = javaClass.superclass
            while (clazz != null && clazz != Any::class.java) {
                scrollerField = clazz.declaredFields.find { it.type == OverScroller::class.java }?.apply { isAccessible = true }
                if (scrollerField != null) break
                clazz = clazz.superclass
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun stopAppbarScroll() {
        try {
            val scroller = scrollerField?.get(this) as? OverScroller
            if (scroller != null && !scroller.isFinished) {
                scroller.abortAnimation()
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isTouchInProgress = true
                stopAppbarScroll()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isTouchInProgress = false
        }
        return super.onInterceptTouchEvent(parent, child, ev)
    }

    override fun onStartNestedScroll(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        if (type == ViewCompat.TYPE_TOUCH) {
            isTouchInProgress = true
            stopAppbarScroll()
        }
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        if (type == ViewCompat.TYPE_TOUCH) isTouchInProgress = false
    }

    override fun setTopAndBottomOffset(offset: Int): Boolean {
        if (lastAcceptedOffset == Int.MIN_VALUE) lastAcceptedOffset = topAndBottomOffset
        if (offset == 0) {
            lastTrend = TREND_NONE
            return applyOffsetActual(offset)
        }
        val diff = offset - lastAcceptedOffset
        if (diff == 0) return false
        if (isJitter(diff)) return false
        return applyOffsetActual(offset)
    }

    private fun isJitter(diff: Int): Boolean {
        if (!isTouchInProgress || lastTrend == TREND_NONE) return false
        val currentTrend = getTrend(diff)
        // 方向反转且幅度小于阈值视为抖动
        return currentTrend != lastTrend && abs(diff) < mJitterThreshold
    }

    private fun getTrend(diff: Int): Int = when {
        diff > 0 -> TREND_DOWN // 展开
        diff < 0 -> TREND_UP   // 折叠
        else -> TREND_NONE
    }

    private fun applyOffsetActual(offset: Int): Boolean {
        val changed = super.setTopAndBottomOffset(offset)
        if (changed) {
            val diff = offset - lastAcceptedOffset
            if (abs(diff) > mSignificantMoveThreshold) {
                lastTrend = getTrend(diff)
            }
            lastAcceptedOffset = offset
        }
        return changed
    }

    companion object {

        private const val TREND_NONE = 0 // 无方向
        private const val TREND_UP = 1    // 向上折叠
        private const val TREND_DOWN = -1 // 向下展开
    }
}