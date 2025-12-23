package cn.cqautotest.sunnybeach.ktx

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import timber.log.Timber

/**
 * Reduces drag sensitivity of [ViewPager2] widget.
 * Link：https://al-e-shevelev.medium.com/how-to-reduce-scroll-sensitivity-of-viewpager2-widget-87797ad02414
 * "2" was obtained experimentally.
 */
fun ViewPager2.reduceDragSensitivity(multiplier: Int = 2) {
    try {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this) as RecyclerView

        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true

        val currentTouchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, (currentTouchSlop * multiplier))
    } catch (e: NoSuchFieldException) {
        // 处理字段名找不到的情况（如未来版本更名）
        Timber.e(e)
    } catch (e: IllegalAccessException) {
        Timber.e(e)
    } catch (e: Exception) {
        Timber.e(e)
    }
}

fun ViewPager2.doPageSelected(onPageSelected: (position: Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }
    })
}