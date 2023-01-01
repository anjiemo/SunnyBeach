package cn.cqautotest.sunnybeach.other

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 *    author : anjiemo
 *    github : https://gitee.com/anjiemo/SunnyBeach
 *    time   : 2022/12/23
 *    desc   : 网格列表分割线，参考：https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing/28533234#30701422
 */
class GridSpaceDecoration(@Px private val space: Int, private val includeEdge: Boolean = true) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position: Int = parent.getChildAdapterPosition(view)
        val spanCount: Int = (parent.layoutManager as GridLayoutManager).spanCount
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect.left = space - column * space / spanCount // space - column * ((1f / spanCount) * space)
            outRect.right = (column + 1) * space / spanCount // (column + 1) * ((1f / spanCount) * space)
            if (position < spanCount) { // top edge
                outRect.top = space
            }
            outRect.bottom = space // item bottom
        } else {
            outRect.left = column * space / spanCount // column * ((1f / spanCount) * space)
            outRect.right = space - (column + 1) * space / spanCount // space - (column + 1) * ((1f / spanCount) * space)
            if (position >= spanCount) {
                outRect.top = space // item top
            }
        }
    }
}