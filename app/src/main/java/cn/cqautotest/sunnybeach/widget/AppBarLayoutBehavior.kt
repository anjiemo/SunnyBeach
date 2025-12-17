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

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2023/03/06
 * desc   : 修复 AppBarLayout 导致的滑动抖动问题
 * CoordinatorLayout 滑动抖动究极解决办法：https://www.jianshu.com/p/7863310a4a6c
 * Used dependent library：com.google.android.material:material:1.6.1@aar
 */
class AppBarLayoutBehavior(context: Context?, attrs: AttributeSet?) : AppBarLayout.Behavior(context, attrs) {

    private var isFlinging = false
    private var shouldBlockNestedScroll = false

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        // Timber.d("onInterceptTouchEvent：===> ${child.totalScrollRange}")
        shouldBlockNestedScroll = isFlinging
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> stopAppbarLayoutFling(child)
            // 手指滑动屏幕的时候停止 Fling 事件
            MotionEvent.ACTION_MOVE -> {}
            else -> {}
        }
        return super.onInterceptTouchEvent(parent, child, ev)
    }

    /**
     * 通过反射获取私有的 flingRunnable 属性
     */
    private fun getFlingRunnableField(): Field? {
        // com.google.android.material.appbar.HeaderBehavior.flingRunnable
        val headerBehaviorType = javaClass.superclass.superclass.superclass
        return headerBehaviorType?.getDeclaredField("flingRunnable")
    }

    /**
     * 反射获取私有的 scroller 属性
     */
    private fun getScrollerField(): Field? {
        // com.google.android.material.appbar.HeaderBehavior.scroller
        val headerBehaviorType: Class<*>? = javaClass.superclass.superclass.superclass
        return headerBehaviorType?.getDeclaredField("scroller")
    }

    /**
     * 停止 AppbarLayout 的 Fling 事件
     */
    private fun stopAppbarLayoutFling(appBarLayout: AppBarLayout) {
        // 通过反射拿到 HeaderBehavior 中的 flingRunnable 变量
        runCatching {
            val flingRunnableField = getFlingRunnableField()
            flingRunnableField?.isAccessible = true
            (flingRunnableField?.get(this) as? Runnable)?.let {
                Timber.d("stopAppbarLayoutFling：===> 存在 flingRunnable 变量")
                appBarLayout.removeCallbacks(it)
                flingRunnableField[this] = null
            }
            val scrollerField: Field? = getScrollerField()
            scrollerField?.isAccessible = true
            (scrollerField?.get(this) as? OverScroller)?.let {
                if (it.isFinished.not()) {
                    it.abortAnimation()
                }
            }
        }.onFailure {
            Timber.e(it)
        }
    }

    override fun onStartNestedScroll(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        Timber.d("onStartNestedScroll：===> directTargetChild is ${directTargetChild.javaClass} target is ${target.javaClass}")
        stopAppbarLayoutFling(child)
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
        Timber.d("onNestedPreScroll：===> ${child.totalScrollRange}, dx:$dx, dy:$dy, type:$type")
        // 当 type 返回 ViewCompat.TYPE_NON_TOUCH 时，表示当前 target 处于非 Touch 的滑动，
        // 该 bug 是因为 AppBarLayout 在滑动时，CoordinatorLayout 内的实现 NestedScrollingChild2 接口的滑动子类还未结束其自身的 Fling导致的，
        // 所以这里监听子类的非 Touch 时的滑动，然后 block 掉滑动事件传递给 AppBarLayout。
        takeIf { type == ViewCompat.TYPE_NON_TOUCH }?.let { isFlinging = true }
        takeUnless { shouldBlockNestedScroll }?.let { super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type) }
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        Timber.d("onNestedScroll: ===> target:${target.javaClass}, ${child.totalScrollRange}, dxConsumed:$dxConsumed, dyConsumed:$dyConsumed, type:$type")
        takeUnless { shouldBlockNestedScroll }?.let {
            super.onNestedScroll(
                coordinatorLayout,
                child,
                target,
                dxConsumed,
                dyConsumed,
                dxUnconsumed,
                dyUnconsumed,
                type,
                consumed
            )
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        Timber.d("onStopNestedScroll：===> calling me...")
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)
        isFlinging = false
        shouldBlockNestedScroll = false
    }
}