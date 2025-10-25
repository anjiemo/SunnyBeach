package cn.cqautotest.sunnybeach.widget.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/07/25
 *    desc   : 图片选择列表分割线
 */
class GridSpaceDecoration(@param:Px private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(rect: Rect, view: View, recyclerView: RecyclerView, state: RecyclerView.State) {
        val position: Int = recyclerView.getChildAdapterPosition(view)
        val spanCount: Int = (recyclerView.layoutManager as GridLayoutManager).spanCount

        if (position < spanCount) {
            // 只有第一行才留出顶部间隙
            rect.top = space
        }

        when {
            (position + 1) % spanCount == 1 -> {
                // 每一行的第一个
                rect.left = space
                rect.right = space / (spanCount + 1)
            }
            (position + 1) % spanCount == 0 -> {
                // 每一行的最后一个
                rect.left = space / (spanCount + 1)
                rect.right = space
            }
            else -> {
                rect.left = (space * ((spanCount - 1f) / spanCount)).roundToInt()
                rect.right = rect.left
            }
        }

        rect.bottom = space
    }
}