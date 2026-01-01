package cn.cqautotest.sunnybeach.widget.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import kotlin.math.max

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/01
 * desc   : 万能列表分割线装饰器，自动适配 [LinearLayoutManager] 和 [GridLayoutManager]。
 *
 * ### 核心功能：
 * 1. **自动识别布局**：网格布局使用“线性平分算法”确保 Item 宽度/高度完全一致；线性布局使用固定偏移。
 * 2. **轴向自适配**：基于“主轴/侧轴”逻辑，同时支持垂直和水平滚动方向。
 * 3. **多 Adapter 兼容**：识别 [ConcatAdapter] 边界，解决标题与内容之间间距过大或重复的问题。
 * ### 参数映射说明：
 * - **垂直布局 (Vertical)**：主轴 = 上下 (Top/Bottom)；侧轴 = 左右 (Start/End)。
 * - **水平布局 (Horizontal)**：主轴 = 左右 (Start/End)；侧轴 = 上下 (Top/Bottom)。
 * @property mainSpace 主轴方向（滚动方向）上的 Item 间距。用于控制行与行（垂直）或列与列（水平）之间的空隙。
 * @property crossSpace 侧轴方向（垂直于滚动方向）上的 Item 间距。用于控制网格内部列与列（垂直）或行与行（水平）之间的空隙。
 * @property adapterGap 专门用于 [ConcatAdapter] 中不同子 Adapter 衔接处的特殊间距 (单位: px)。例如：在 TitleAdapter 结束和 ContentAdapter 开始的交界处应用此间距。
 * @property includeStart 是否包含列表起始边缘的间距（垂直布局为左边缘，水平布局为左边缘）。
 * @property includeTop 是否包含列表顶部边缘的间距。
 * @property includeEnd 是否包含列表结束边缘的间距（垂直布局为右边缘，水平布局为右边缘）。
 * @property includeBottom 是否包含列表底部边缘的间距。
 */
class UniversalSpaceDecoration(
    @param:Px private val mainSpace: Int,
    @param:Px private val crossSpace: Int = mainSpace,
    @param:Px private val adapterGap: Int = 0,
    private val includeStart: Boolean = true,
    private val includeTop: Boolean = true,
    private val includeEnd: Boolean = true,
    private val includeBottom: Boolean = true
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(rect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION || state.itemCount == 0) return

        // GridLayoutManager是LinearLayoutManager的子类，所以我们可以同时处理 LinearLayoutManager 和 GridLayoutManager
        val layoutManager = parent.layoutManager as? LinearLayoutManager ?: return
        val isVertical = layoutManager.orientation == RecyclerView.VERTICAL

        // 核心数据准备
        val params = getGridParams(layoutManager, view, position, state)
        val isBoundary = isAdapterBoundary(position, parent)

        // 根据方向分发（内部处理 Main/Cross 逻辑）
        if (isVertical) {
            // 垂直布局：Cross 是左右，Main 是上下
            assignCrossOffsets(rect, params, true, includeStart, includeEnd)
            assignMainOffsets(rect, params, true, isBoundary, includeTop, includeBottom)
        } else {
            // 水平布局：Cross 是上下，Main 是左右
            assignCrossOffsets(rect, params, false, includeTop, includeBottom)
            assignMainOffsets(rect, params, false, isBoundary, includeStart, includeEnd)
        }
    }

    /**
     * 侧轴（Cross Axis）间距计算：解决网格重复间距的核心公式
     */
    private fun assignCrossOffsets(
        rect: Rect,
        params: GridParams,
        isVertical: Boolean,
        includeStart: Boolean,
        includeEnd: Boolean
    ) {
        // 确保 spanCount 不为 0
        val safeSpanCount = max(params.spanCount, 1)

        val start: Int
        val end: Int

        if (includeStart || includeEnd) {
            start = crossSpace - params.spanIndex * crossSpace / safeSpanCount
            end = (params.spanIndex + params.spanSize) * crossSpace / safeSpanCount
        } else {
            start = params.spanIndex * crossSpace / safeSpanCount
            end = crossSpace - (params.spanIndex + params.spanSize) * crossSpace / safeSpanCount
        }

        if (isVertical) {
            rect.left = start
            rect.right = end
        } else {
            rect.top = start
            rect.bottom = end
        }
    }

    /**
     * 主轴（Main Axis）间距计算：处理首尾和 Adapter 衔接
     */
    private fun assignMainOffsets(
        rect: Rect,
        params: GridParams,
        isVertical: Boolean,
        isBound: Boolean,
        includeTop: Boolean,
        includeBottom: Boolean
    ) {
        val head = if (params.isFirstGroup) (if (includeTop) mainSpace else 0) else 0
        val tail = when {
            params.isLastGroup -> if (includeBottom) mainSpace else 0
            isBound -> adapterGap
            else -> mainSpace
        }

        if (isVertical) {
            rect.top = head
            rect.bottom = tail
        } else {
            rect.left = head
            rect.right = tail
        }
    }

    /**
     * 获取网格参数
     * 我们可以将[GridLayoutManager]视作一种特殊的[LinearLayoutManager]。
     */
    private fun getGridParams(layoutManager: LinearLayoutManager, view: View, position: Int, state: RecyclerView.State): GridParams {
        val spanCount: Int
        val spanIndex: Int
        val spanSize: Int
        val groupIndex: Int
        val lastGroupIndex: Int

        if (layoutManager is GridLayoutManager) {
            spanCount = layoutManager.spanCount
            val params = view.layoutParams as GridLayoutManager.LayoutParams
            spanIndex = params.spanIndex
            spanSize = params.spanSize
            groupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(position, spanCount)
            lastGroupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(state.itemCount - 1, spanCount)
        } else {
            spanCount = 1
            spanIndex = 0
            spanSize = 1
            groupIndex = position
            lastGroupIndex = state.itemCount - 1
        }
        return GridParams(spanCount, spanIndex, spanSize, groupIndex == 0, groupIndex == lastGroupIndex)
    }

    /**
     * 判定当前位置 [position] 是否为所属子 Adapter 的“物理边界”。
     *
     * 在使用 [ConcatAdapter] 时（例如 BRVAH 的 QuickAdapterHelper），列表由多个子 Adapter 拼接而成。
     * 此方法通过对比当前项与下一项是否属于同一个子 Adapter 实例，来确定是否到达了衔接处。
     *
     * @return 如果当前项是当前子 Adapter 的最后一项，且后续还有其他 Adapter 的内容，则返回 true。
     */
    private fun isAdapterBoundary(position: Int, parent: RecyclerView): Boolean {
        // 只有 ConcatAdapter 才存在“子 Adapter 边界”概念，普通 Adapter 直接忽略
        val adapter = parent.adapter as? ConcatAdapter ?: return false

        val currentItemCount = adapter.itemCount

        // 如果是无效的 position 或已经是全局最后一项，不需要处理边界衔接（由 isLastGroup 处理）
        if (position < 0 || position >= currentItemCount - 1) return false

        return try {
            // 获取当前项在所属子 Adapter 中的包装对象
            // 通过这个内置方法，我们可以直接拿到该项所属的子 Adapter 引用
            val currentSubAdapter = adapter.getWrappedAdapterAndPosition(position).first
            val nextSubAdapter = adapter.getWrappedAdapterAndPosition(position + 1).first

            // 比较实例引用：如果不同，说明 position 是当前子 Adapter 的终点
            currentSubAdapter != nextSubAdapter
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
            // 发生非法状态时，安全退化
            false
        }
    }

    /**
     * 内部参数包装类，用于抹平 [LinearLayoutManager] 和 [GridLayoutManager] 的差异。
     * * 该类将复杂的网格计算抽象为通用的布局属性，使分割线逻辑只需关注“第几列”和“是否为首尾组”，
     * 而无需关心底层具体的 LayoutManager 实现。
     */
    private data class GridParams(
        val spanCount: Int,
        val spanIndex: Int,
        val spanSize: Int,
        val isFirstGroup: Boolean,
        val isLastGroup: Boolean
    )
}