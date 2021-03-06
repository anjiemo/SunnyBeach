package cn.cqautotest.sunnybeach.ktx

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.removeAllItemDecoration() = repeat(itemDecorationCount) { removeItemDecorationAt(it) }

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
 * ?????? LinearLayoutManager ?????????????????????
 */
fun equilibriumAssignmentOfLinear(
    @Px unit: Int,
    outRect: Rect,
    view: View,
    parent: RecyclerView
) {
    // item ?????????
    val itemCount = parent.getItemCount()
    // ?????? item ??? position
    val itemPosition = parent.getChildAdapterPosition(view)
    val layoutManager = parent.checkLinearLayoutManager() ?: return
    // ?????? LinearLayoutManager ???????????????
    val orientation = layoutManager.orientation
    // ???????????? item
    for (index in 0..itemCount) {
        when (itemPosition) {
            // ?????????
            0 -> {
                if (orientation == RecyclerView.VERTICAL) {
                    // ????????? && VERTICAL ???????????? -> ???item?????????????????????
                    outRect.top = unit * 2
                    outRect.bottom = unit
                    outRect.left = unit * 2
                    outRect.right = unit * 2
                } else {
                    // ????????? && HORIZONTAL ???????????? -> ???item?????????????????????
                    outRect.top = unit * 2
                    outRect.bottom = unit * 2
                    outRect.left = unit * 2
                    outRect.right = unit
                }
            }
            // ????????????
            itemCount - 1 -> {
                if (orientation == RecyclerView.VERTICAL) {
                    // ???????????? && VERTICAL ???????????? -> ???item?????????????????????
                    outRect.top = unit
                    outRect.bottom = unit * 2
                    outRect.left = unit * 2
                    outRect.right = unit * 2
                } else {
                    // ???????????? && HORIZONTAL ???????????? -> ???item?????????????????????
                    outRect.top = unit * 2
                    outRect.bottom = unit * 2
                    outRect.left = unit
                    outRect.right = unit * 2
                }
            }
            // ?????????item
            else -> {
                if (orientation == RecyclerView.VERTICAL) {
                    // ?????????item && VERTICAL ???????????? -> ???item??????????????????????????????
                    outRect.top = unit
                    outRect.bottom = unit
                    outRect.left = unit * 2
                    outRect.right = unit * 2
                } else {
                    // ?????????item && HORIZONTAL ???????????? -> ???item??????????????????????????????
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
 * {@link ItemDecoration#getItemOffsets(outRect: Rect,view: View,parent: RecyclerView)} or
 * {@link ItemDecoration#getItemOffsets(outRect: Rect,view: View,parent: RecyclerView,state: RecyclerView.State)}.
 * ?????? GridLayoutManager ?????????????????????
 */
fun equilibriumAssignmentOfGrid(
    unit: Int,
    outRect: Rect,
    view: View,
    parent: RecyclerView
) {
    // item ?????????
    val itemCount = parent.getItemCount()
    // ????????????????????????
    val spanCount = parent.getSpanCount()
    // ?????? item ??? position
    val itemPosition = parent.getChildAdapterPosition(view)
    val layoutManager = parent.checkGridLayoutManager() ?: return
    if (spanCount < 2) {
        equilibriumAssignmentOfLinear(view = view, unit = unit, parent = parent, outRect = outRect)
        return
    }
    // ?????? GridLayoutManager ???????????????
    val orientation = layoutManager.orientation
    if (orientation == RecyclerView.HORIZONTAL) {
        // ??????????????????????????? GridLayoutManager
        throw UnsupportedOperationException("You can???t set a horizontal grid layout because we don???t support???")
    }
    // ???????????? item
    for (index in 0..itemCount) {
        when {
            // ?????????????????????
            itemPosition % spanCount == 0 -> {
                outRect.left = unit * 2
                outRect.right = unit
            }
            // ?????????????????????
            (itemPosition - (spanCount - 1)) % spanCount == 0 -> {
                outRect.left = unit
                outRect.right = unit * 2
            }
            // ?????????????????????????????????
            else -> {
                outRect.left = unit
                outRect.right = unit
            }
        }
        outRect.top = unit * 2
        // ?????????????????????????????????????????????????????????????????????
        if (itemPosition in (itemCount - spanCount) until itemCount) {
            outRect.bottom = unit * 2
        }
    }
}

/**
 * ?????? spanCount
 * ?????????????????????????????? LayoutManager ??? GridLayoutManager ??? RecyclerView ??????
 */
fun RecyclerView.getSpanCount(): Int {
    val layoutManager = checkGridLayoutManager() ?: return 0
    return layoutManager.spanCount
}

/**
 * ?????????????????? RecyclerView ???????????????????????????
 */
fun RecyclerView.getItemCount(): Int {
    val layoutManager = layoutManager ?: return 0
    return layoutManager.itemCount
}

/**
 * ?????? RecyclerView ????????? LinearLayoutManager
 */
private fun RecyclerView.checkLinearLayoutManager(): LinearLayoutManager? {
    val layoutManager =
        layoutManager ?: return null
    require(layoutManager is GridLayoutManager) { "Make sure you are using the GridLayoutManager???" }
    return layoutManager
}

/**
 * ?????? RecyclerView ????????? GridLayoutManager
 */
private fun RecyclerView.checkGridLayoutManager(): GridLayoutManager? {
    val layoutManager =
        layoutManager ?: return null
    require(layoutManager is GridLayoutManager) { "Make sure you are using the GridLayoutManager???" }
    return layoutManager
}
