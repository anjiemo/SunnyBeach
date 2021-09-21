package cn.cqautotest.sunnybeach.util

import android.graphics.Rect
import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @ApplicationName: oa-android
 * @Description: RecyclerView 间距装饰（线性布局管理器）
 * @author: anjiemo
 * @date: 2021/8/4 14:42
 * @version: V1.0.0
 */
open class SimpleLinearSpaceItemDecoration(
    // 单位间距（实际间距的一半）
    private val unit: Int = 2.dp
) : RecyclerView.ItemDecoration() {

    @CallSuper
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        // item 的个数
        val itemCount = parent.getItemCount()
        // 当前 item 的 position
        val itemPosition = parent.getChildAdapterPosition(view)
        val layoutManager = (parent.layoutManager as? LinearLayoutManager) ?: return
        // 获取 LinearLayoutManager 的布局方向
        val orientation = layoutManager.orientation
        if (orientation == RecyclerView.VERTICAL) {
            outRect.bottom = unit
        } else {
            outRect.right = unit
        }
    }
}