package cn.cqautotest.sunnybeach.widget.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2023/07/06
 * desc   : 瀑布流管理器列表分割线
 */
class StaggeredGridSpaceDecoration(
    // 单位间距
    @Px private val space: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val params = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        val layoutManager = parent.layoutManager as StaggeredGridLayoutManager
        if (layoutManager.orientation == VERTICAL) {
            drawVertical(outRect, layoutManager.spanCount, params.spanIndex, params.bindingAdapterPosition)
        } else {
            drawHorizontal(outRect, layoutManager.spanCount, params.spanIndex, params.bindingAdapterPosition)
        }
    }

    private fun drawHorizontal(outRect: Rect, spanCount: Int, spanIndex: Int, position: Int) {
        if (position < spanCount) {
            outRect.left = space
        }
        outRect.right = space // item right
        outRect.top = space - spanIndex * space / spanCount // space - row * ((1f / spanCount) * space)
        outRect.bottom = (spanIndex + 1) * space / spanCount // (row + 1) * ((1f / spanCount) * space)
    }

    private fun drawVertical(outRect: Rect, spanCount: Int, spanIndex: Int, position: Int) {
        if (position < spanCount) {
            outRect.top = space
        }
        outRect.bottom = space // item bottom
        outRect.left = space - spanIndex * space / spanCount // space - column * ((1f / spanCount) * space)
        outRect.right = (spanIndex + 1) * space / spanCount // (column + 1) * ((1f / spanCount) * space)
    }

    companion object {

        private const val VERTICAL = RecyclerView.VERTICAL
    }
}