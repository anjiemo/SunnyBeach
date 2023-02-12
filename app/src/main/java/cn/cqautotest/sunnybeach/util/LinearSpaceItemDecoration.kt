package cn.cqautotest.sunnybeach.util

import android.graphics.Rect
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.equilibriumAssignmentOfLinear

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/8/4
 * desc   : RecyclerView 间距装饰（线性布局管理器）
 */
open class LinearSpaceItemDecoration(
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
        super.getItemOffsets(outRect, view, parent, state)
        equilibriumAssignmentOfLinear(unit, outRect, view, parent)
    }
}