package cn.cqautotest.sunnybeach.ktx

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.clearItemDecorations() = repeat(itemDecorationCount) { removeItemDecorationAt(it) }

/**
 * Callback method to be invoked when RecyclerView's scroll state changes.
 *
 * @param recyclerView The RecyclerView whose scroll state has changed.
 * @param newState     The updated scroll state. One of {@link #SCROLL_STATE_IDLE},
 *                     {@link #SCROLL_STATE_DRAGGING} or {@link #SCROLL_STATE_SETTLING}.
 */
fun RecyclerView.doScrollStateChanged(action: (recyclerView: RecyclerView, newState: Int) -> Unit): RecyclerView.OnScrollListener =
    addOnScrollListener(onScrollStateChanged = action)

/**
 * Callback method to be invoked when the RecyclerView has been scrolled. This will be
 * called after the scroll has completed.
 * <p>
 * This callback will also be called if visible item range changes after a layout
 * calculation. In that case, dx and dy will be 0.
 *
 * @param recyclerView The RecyclerView which scrolled.
 * @param dx           The amount of horizontal scroll.
 * @param dy           The amount of vertical scroll.
 */
fun RecyclerView.doScrolled(action: (recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit): RecyclerView.OnScrollListener =
    addOnScrollListener(onScrolled = action)

/**
 * Add a listener that will be notified of any changes in scroll state or position.
 *
 * <p>Components that add a listener should take care to remove it when finished.
 * Other components that take ownership of a view may call {@link #clearOnScrollListeners()}
 * to remove all attached listeners.</p>
 *
 * @param listener listener to set
 */
inline fun RecyclerView.addOnScrollListener(
    crossinline onScrollStateChanged: (recyclerView: RecyclerView, newState: Int) -> Unit = { _, _ -> },
    crossinline onScrolled: (recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit = { _, _, _ -> }
): RecyclerView.OnScrollListener {
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            onScrollStateChanged.invoke(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            onScrolled.invoke(recyclerView, dx, dy)
        }
    }
    addOnScrollListener(scrollListener)
    return scrollListener
}

val RecyclerView.itemCount
    get() = adapter?.itemCount ?: 0

/**
 * 在下一次 UI 绘制后添加默认动画
 */
fun RecyclerView.addAfterNextUpdateUIDefaultItemAnimator() {
    itemAnimator ?: run { post { itemAnimator = DefaultItemAnimator() } }
}

/**
 * 清除 RecyclerView 的 item 动画
 */
fun RecyclerView.clearItemAnimator() {
    itemAnimator = null
}