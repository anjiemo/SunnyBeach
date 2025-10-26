package cn.cqautotest.sunnybeach.widget.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.ktx.dp

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/8/4
 * desc   : RecyclerView 间距装饰（线性布局管理器）
 */
open class SimpleLinearSpaceItemDecoration(
    // 单位间距
    @param:Px private val unit: Int = 2.dp,
    private val includeEdge: Boolean = true
) : RecyclerView.ItemDecoration() {

    @CallSuper
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = (parent.layoutManager as? LinearLayoutManager) ?: return
        // 获取 LinearLayoutManager 的布局方向
        val orientation = layoutManager.orientation
        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        val lastIndex = layoutManager.itemCount - 1
        if (orientation == RecyclerView.VERTICAL) {
            if (itemPosition == 0 && includeEdge) {
                outRect.top = unit
            }
            if (itemPosition != lastIndex) {
                outRect.bottom = unit
            } else if (includeEdge) {
                outRect.bottom = unit
            }
        } else {
            if (itemPosition == 0 && includeEdge) {
                outRect.left = unit
            }
            if (itemPosition != lastIndex) {
                outRect.right = unit
            } else if (includeEdge) {
                outRect.right = unit
            }
        }
    }
}