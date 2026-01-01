package cn.cqautotest.sunnybeach.widget.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/01
 * desc   : 专为 [GridLayoutManager] 设计的网格列表间距装饰器。
 *
 * 特性：
 *  1、支持 [GridLayoutManager] 的水平 (Horizontal) 和垂直 (Vertical) 布局。
 *  2、采用线性平分公式进行处理，解决了网格因无法整除导致的 Item 宽度/高度不一致的问题。
 *  3、支持精细控制四个边缘 (Start, Top, End, Bottom) 是否显示间距。
 *  4、兼容 [ConcatAdapter]，支持在不同子 Adapter 衔接处设置自定义间距 [adapterGap]。
 * @property mainSpace 水平方向上的 Item 间距 (单位：px)。沿滚动方向的间距 (Vertical 时为上下，Horizontal 时为左右)。
 * @property crossSpace 垂直方向上的 Item 间距 (单位：px)。垂直于滚动方向的间距 (Vertical 时为左右，Horizontal 时为上下)。
 * @property adapterGap 专门用于 [ConcatAdapter] 中不同子 Adapter 衔接处的特殊间距 (单位: px)。例如：在 TitleAdapter 结束和 ContentAdapter 开始的交界处应用此间距。
 * @property includeStart 是否包含列表起始边缘的间距（垂直布局为左边缘，水平布局为左边缘）。
 * @property includeTop 列表最顶端是否包含间距。
 * @property includeEnd 列表最右侧是否包含间距。
 * @property includeBottom 是否包含列表结束边缘的间距（垂直布局为右边缘，水平布局为右边缘）。
 */
class GridSpaceDecoration(
    @param:Px private val mainSpace: Int,
    @param:Px private val crossSpace: Int = mainSpace,
    @param:Px private val adapterGap: Int = 0,
    private val includeStart: Boolean = true,
    private val includeTop: Boolean = true,
    private val includeEnd: Boolean = true,
    private val includeBottom: Boolean = true
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(rect: Rect, view: View, recyclerView: RecyclerView, state: RecyclerView.State) {
        val params = view.layoutParams as? GridLayoutManager.LayoutParams ?: return
        val layoutManager = recyclerView.layoutManager as? GridLayoutManager ?: return

        val spanCount = layoutManager.spanCount
        val spanIndex = params.spanIndex // 当前 item 在行/列中的起始位置
        val spanSize = params.spanSize   // 当前 item 占据的跨度
        val position = recyclerView.getChildAdapterPosition(view) // 当前 item 的位置

        // 过滤无效位置
        if (position == RecyclerView.NO_POSITION || state.itemCount == 0) return

        val spanSizeLookup = layoutManager.spanSizeLookup
        // 计算当前 item 所属的组索引（垂直布局时为行索引，水平布局时为列索引）
        val groupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount)

        // 判定是否为整个 RecyclerView 的第一组
        val isFirstGroupTotal = groupIndex == 0
        // 判定是否为整个 RecyclerView 的最后一组
        val lastGroupIndexTotal = spanSizeLookup.getSpanGroupIndex(state.itemCount - 1, spanCount)
        val isLastGroupTotal = groupIndex == lastGroupIndexTotal

        // 判定当前位置是否处于两个子 Adapter 的衔接处（仅在 ConcatAdapter 模式下有效）
        val isBoundary = isAdapterBoundary(position, recyclerView)

        // 根据布局方向分发计算任务
        if (layoutManager.orientation == GridLayoutManager.VERTICAL) {
            setupVertical(rect, spanCount, spanIndex, spanSize, isFirstGroupTotal, isLastGroupTotal, isBoundary)
        } else {
            setupHorizontal(rect, spanCount, spanIndex, spanSize, isFirstGroupTotal, isLastGroupTotal, isBoundary)
        }
    }

    /**
     * 判定当前位置是否为其所属 Adapter 的最后一行/列。
     * @return 如果下一位置属于不同的子 Adapter 实例，则返回 true。
     */
    private fun isAdapterBoundary(position: Int, recyclerView: RecyclerView): Boolean {
        val adapter = recyclerView.adapter ?: return false

        if (adapter is ConcatAdapter) {
            // 如果已经是全局最后一项，视作边界
            if (position >= adapter.itemCount - 1) return true
            // 对比当前位置与下一位置所属的子 Adapter 实例，不一致则判定为边界
            return findSubAdapter(adapter, position) != findSubAdapter(adapter, position + 1)
        }

        return false
    }

    /**
     * 在 [ConcatAdapter] 中通过 position 查找其归属的原始子 Adapter。
     */
    private fun findSubAdapter(concatAdapter: ConcatAdapter, position: Int): RecyclerView.Adapter<*>? {
        var count = 0
        for (adapter in concatAdapter.adapters) {
            count += adapter.itemCount
            if (position < count) return adapter
        }
        return null
    }

    /**
     * 针对垂直滚动布局设置间距。
     * 采用公式：edge - (index * edge / count)
     * 这种线性偏移算法能确保无论网格如何分割，所有 Item 的内容宽度完全一致。
     */
    private fun setupVertical(
        rect: Rect, spanCount: Int, spanIndex: Int, spanSize: Int,
        isFirstTotal: Boolean, isLastTotal: Boolean, isBoundary: Boolean
    ) {
        // 侧轴 (Cross Axis): 左右方向
        val leftEdge = if (includeStart) crossSpace else 0
        val rightEdge = if (includeEnd) crossSpace else 0
        rect.left = leftEdge - spanIndex * leftEdge / spanCount
        rect.right = (spanIndex + spanSize) * rightEdge / spanCount

        // 主轴 (Main Axis): 上下方向
        rect.top = if (isFirstTotal) (if (includeTop) mainSpace else 0) else 0
        rect.bottom = when {
            isLastTotal -> if (includeBottom) mainSpace else 0
            isBoundary -> adapterGap
            else -> mainSpace
        }
    }

    /**
     * 针对水平滚动布局设置间距。
     * 原理同 [setupVertical]，但将行进轴与平分轴对调。
     */
    private fun setupHorizontal(
        rect: Rect, spanCount: Int, spanIndex: Int, spanSize: Int,
        isFirstTotal: Boolean, isLastTotal: Boolean, isBoundary: Boolean
    ) {
        // 侧轴 (Cross Axis): 上下方向
        val topEdge = if (includeTop) crossSpace else 0
        val bottomEdge = if (includeBottom) crossSpace else 0
        rect.top = topEdge - spanIndex * topEdge / spanCount
        rect.bottom = (spanIndex + spanSize) * bottomEdge / spanCount

        // 主轴 (Main Axis): 左右方向
        rect.left = if (isFirstTotal) (if (includeStart) mainSpace else 0) else 0
        rect.right = when {
            isLastTotal -> if (includeEnd) mainSpace else 0
            isBoundary -> adapterGap
            else -> mainSpace
        }
    }
}