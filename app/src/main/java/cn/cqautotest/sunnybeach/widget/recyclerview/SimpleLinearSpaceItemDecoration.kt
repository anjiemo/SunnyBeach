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
    // 单位间距（实际间距的一半）
    @Px private val unit: Int = 2.dp
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
        if (orientation == RecyclerView.VERTICAL) {
            outRect.bottom = unit
        } else {
            outRect.right = unit
        }
    }
}