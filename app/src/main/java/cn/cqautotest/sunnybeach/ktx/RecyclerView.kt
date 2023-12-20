package cn.cqautotest.sunnybeach.ktx

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
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

/**
 * {@link ItemDecoration#getItemOffsets(outRect: Rect,view: View,parent: RecyclerView)} or
 * {@link ItemDecoration#getItemOffsets(outRect: Rect,view: View,parent: RecyclerView,state: RecyclerView.State)}.
 * 均分 LinearLayoutManager 间距的便捷方法
 */
fun equilibriumAssignmentOfLinear(
    @Px unit: Int,
    outRect: Rect,
    view: View,
    parent: RecyclerView
) {
    // item 的个数
    val itemCount = parent.getItemCount()
    // 当前 item 的 position
    val itemPosition = parent.getChildAdapterPosition(view)
    val layoutManager = parent.checkLinearLayoutManager() ?: return
    // 获取 LinearLayoutManager 的布局方向
    val orientation = layoutManager.orientation
    // 遍历所有 item
    for (index in 0..itemCount) {
        when (itemPosition) {
            // 第一个
            0 -> {
                if (orientation == RecyclerView.VERTICAL) {
                    // 第一个 && VERTICAL 布局方式 -> 对item的底部特殊处理
                    outRect.top = unit * 2
                    outRect.bottom = unit
                    outRect.left = unit * 2
                    outRect.right = unit * 2
                } else {
                    // 第一个 && HORIZONTAL 布局方式 -> 对item的右边特殊处理
                    outRect.top = unit * 2
                    outRect.bottom = unit * 2
                    outRect.left = unit * 2
                    outRect.right = unit
                }
            }
            // 最后一个
            itemCount - 1 -> {
                if (orientation == RecyclerView.VERTICAL) {
                    // 最后一个 && VERTICAL 布局方式 -> 对item的顶部特殊处理
                    outRect.top = unit
                    outRect.bottom = unit * 2
                    outRect.left = unit * 2
                    outRect.right = unit * 2
                } else {
                    // 最后一个 && HORIZONTAL 布局方式 -> 对item的左边特殊处理
                    outRect.top = unit * 2
                    outRect.bottom = unit * 2
                    outRect.left = unit
                    outRect.right = unit * 2
                }
            }
            // 中间的item
            else -> {
                if (orientation == RecyclerView.VERTICAL) {
                    // 中间的item && VERTICAL 布局方式 -> 对item的顶部和底部特殊处理
                    outRect.top = unit
                    outRect.bottom = unit
                    outRect.left = unit * 2
                    outRect.right = unit * 2
                } else {
                    // 中间的item && HORIZONTAL 布局方式 -> 对item的左边和右边特殊处理
                    outRect.top = unit * 2
                    outRect.bottom = unit * 2
                    outRect.left = unit
                    outRect.right = unit
                }
            }
        }
    }
}

/**
 * 返回绑定到父 RecyclerView 的适配器中的项目数
 */
fun RecyclerView.getItemCount(): Int {
    val layoutManager = layoutManager ?: return 0
    return layoutManager.itemCount
}

/**
 * 检查 RecyclerView 设置的 LinearLayoutManager
 */
private fun RecyclerView.checkLinearLayoutManager(): LinearLayoutManager? {
    val layoutManager = layoutManager ?: return null
    require(layoutManager is LinearLayoutManager) { "Make sure you are using the LinearLayoutManager！" }
    return layoutManager
}

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