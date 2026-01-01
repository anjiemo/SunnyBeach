package cn.cqautotest.sunnybeach.widget.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/8/4
 * desc   : 专为 [LinearLayoutManager] 设计的列表间距装饰器。
 *
 * 特性：
 *  1、支持 [LinearLayoutManager] 的垂直 (Vertical) 和水平 (Horizontal) 方向。
 *  2、侧向 (Cross Axis) 间距采用简单的左右/上下赋值。
 *  3、兼容 [ConcatAdapter]，支持在不同子 Adapter 衔接处设置自定义间距 [adapterGap]。
 *
 * @property mainSpace 主轴方向（滚动方向）上的 Item 间距。沿滚动方向的间距 (Vertical 时为上下，Horizontal 时为左右)。
 * @property crossSpace 侧轴方向（垂直于滚动方向）上的 Item 间距。垂直于滚动方向的间距 (Vertical 时为左右，Horizontal 时为上下)。
 * @property adapterGap 专门用于 [ConcatAdapter] 中不同子 Adapter 衔接处的特殊间距 (单位: px)。例如：在 TitleAdapter 结束和 ContentAdapter 开始的交界处应用此间距。
 * @property includeStart 是否包含列表起始边缘的间距（垂直布局为左边缘，水平布局为左边缘）。
 * @property includeTop 列表最顶端是否包含间距。
 * @property includeEnd 是否包含列表结束边缘的间距（垂直布局为右边缘，水平布局为右边缘）。
 * @property includeBottom 列表最底端是否包含间距。
 */
class LinearSpaceDecoration(
    @param:Px private val mainSpace: Int,
    @param:Px private val crossSpace: Int = mainSpace,
    @param:Px private val adapterGap: Int = 0,
    private val includeStart: Boolean = true,
    private val includeTop: Boolean = true,
    private val includeEnd: Boolean = true,
    private val includeBottom: Boolean = true
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(rect: Rect, view: View, recyclerView: RecyclerView, state: RecyclerView.State) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val position = recyclerView.getChildAdapterPosition(view)

        // 过滤无效位置
        if (position == RecyclerView.NO_POSITION || state.itemCount == 0) return

        val isFirstTotal = position == 0
        val isLastTotal = position == state.itemCount - 1
        // 判定当前位置是否处于两个子 Adapter 的衔接处（仅在 ConcatAdapter 模式下有效）
        val isBoundary = isAdapterBoundary(position, recyclerView)

        // 根据布局方向分发计算任务
        if (layoutManager.orientation == RecyclerView.VERTICAL) {
            setupVertical(rect, isFirstTotal, isLastTotal, isBoundary)
        } else {
            setupHorizontal(rect, isFirstTotal, isLastTotal, isBoundary)
        }
    }

    /**
     * 判定当前位置是否为其所属子 Adapter 的最后一项。
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
     * 垂直列表布局：主轴为 Top/Bottom，侧轴为 Start/End
     */
    private fun setupVertical(rect: Rect, isFirst: Boolean, isLast: Boolean, isBoundary: Boolean) {
        // 侧轴 (水平方向) 间距
        rect.left = if (includeStart) crossSpace else 0
        rect.right = if (includeEnd) crossSpace else 0

        // 主轴 (垂直方向) 间距
        rect.top = if (isFirst) (if (includeTop) mainSpace else 0) else 0
        rect.bottom = when {
            isLast -> if (includeBottom) mainSpace else 0
            isBoundary -> adapterGap
            else -> mainSpace
        }
    }

    /**
     * 水平列表布局：主轴为 Start/End，侧轴为 Top/Bottom
     */
    private fun setupHorizontal(rect: Rect, isFirst: Boolean, isLast: Boolean, isBoundary: Boolean) {
        // 侧轴 (垂直方向) 间距
        rect.top = if (includeTop) crossSpace else 0
        rect.bottom = if (includeBottom) crossSpace else 0

        // 主轴 (水平方向) 间距
        rect.left = if (isFirst) (if (includeStart) mainSpace else 0) else 0
        rect.right = when {
            isLast -> if (includeEnd) mainSpace else 0
            isBoundary -> adapterGap
            else -> mainSpace
        }
    }
}