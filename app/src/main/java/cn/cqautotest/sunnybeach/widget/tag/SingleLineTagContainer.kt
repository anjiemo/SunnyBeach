package cn.cqautotest.sunnybeach.widget.tag

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/01
 * desc   : 一个高性能、类型安全的单行标签容器。
 *
 * 该容器专为在 [RecyclerView] 列表嵌套场景下使用而设计。
 * 与传统的嵌套 RecyclerView 方案相比，它具有以下优势：
 * 1、**高性能**：自研轻量级视图复用机制，避免了 RecyclerView 繁重的滚动与回收逻辑，显著降低滑动卡顿。
 * 2、**智能截断**：自动计算容器宽度，当标签总宽度超过可用空间时，自动移除末尾标签并替换为“N+”或自定义的更多样式。
 * 3、**类型安全**：利用 Kotlin 闭包捕获泛型，在绑定标签时无需手动强转数据类型。
 * 4、**高度可定制**：通过 [TagViewAdapter] 接口，可自由定义普通标签与“更多”标签的 UI 表现和点击交互。
 *
 * 使用示例：
 * ```kotlin
 *  tagContainer.setTagGap(8.dp)
 *  tagContainer.setupTags(dataList, object : TagViewAdapter<TagBean, TagVH> {
 *      override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup) = TagVH(...)
 *      override fun bindViewHolder(holder: TagVH, item: TagBean, position: Int) { ... }
 *
 *      // 方案2：将 count 转换为对象，自动复用 bindViewHolder 逻辑
 *      override fun convertRemainingToItem(count: Int, dataList: List<TagBean>, index: Int): TagBean {
 *          return TagBean(name = "$count+", isMore = true)
 *      }
 *  })
 * ```
 */
class SingleLineTagContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val mInflater = LayoutInflater.from(context)

    // 标签之间的横向间距
    private var mTagGap: Int = 0

    // 最大显示数量限制，默认无限制
    private var mMaxTagCount: Int = Int.MAX_VALUE

    private var mMoreViewHolder: TagViewHolder? = null

    // 泛型测量中转站：通过闭包消除 Any 强转，增加宽度参数支持
    private var mMeasureLogic: ((availableWidth: Int) -> Int)? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.SingleLineTagContainer) {
            mTagGap = getDimensionPixelSize(R.styleable.SingleLineTagContainer_tagGap, 0)
            mMaxTagCount = getInt(R.styleable.SingleLineTagContainer_maxTagCount, Int.MAX_VALUE)
        }
    }

    /**
     * 设置标签数据
     * @param data 标签数据
     * @param adapter 标签适配器
     */
    fun <T, VH : TagViewHolder> setupTags(data: List<T>, adapter: TagViewAdapter<T, VH>) {
        // 重置状态：先隐藏所有 View
        children.forEach { it.isGone = true }

        // 根据最大数量限制截取数据
        val displayData = if (data.count() > mMaxTagCount) data.take(mMaxTagCount) else data

        // 预绑定普通标签
        displayData.forEachIndexed { index, item ->
            val holder = getOrCreateViewHolder(index, adapter)
            holder.itemView.isGone = false // 必须设置为非 Gone 才能参与后续测量
            adapter.bindViewHolder(holder, item, index)
        }

        // 核心：封装测量闭包，捕获当前数据、泛型 T 和 VH
        mMeasureLogic = { availableWidth ->
            performTypeSafeMeasure(availableWidth, data, adapter)
        }

        requestLayout()
    }

    /**
     * 设置标签间距
     * @param gap 间隔，默认为 0
     */
    fun setTagGap(gap: Int) {
        if (mTagGap != gap) {
            mTagGap = gap
            requestLayout()
        }
    }

    /**
     * 设置最大标签数量
     * @param count 标签数量，默认为 Int.MAX_VALUE，表示不限制；最低为 0。
     */
    fun setMaxTagCount(count: Int) = apply {
        val newCount = count.coerceAtLeast(0)
        if (mMaxTagCount != newCount) {
            mMaxTagCount = newCount
            requestLayout()
        }
    }

    private fun <T, VH : TagViewHolder> getOrCreateViewHolder(index: Int, adapter: TagViewAdapter<T, VH>): VH {
        // 1. 尝试直接通过索引找现有的 View
        val child = if (index < childCount) getChildAt(index) else null

        // 2. 如果有 View，尝试找 Holder
        @Suppress("UNCHECKED_CAST")
        var holder = child?.getTag(R.id.tag_view_holder) as? VH

        // 3. 这里的逻辑是关键：
        // 如果没有 Holder，或者现有的 View 是“更多”标签（占位错误），则需要创建
        if (holder == null || child == mMoreViewHolder?.itemView) {
            val newHolder = adapter.createViewHolder(mInflater, this)
            val newView = newHolder.itemView

            // 自动化绑定：在这里强制关联，调用者就不需要写了
            newView.setTag(R.id.tag_view_holder, newHolder)

            if (child == mMoreViewHolder?.itemView) {
                addView(newView, index)
            } else if (child == null) {
                addView(newView)
            }
            holder = newHolder
        }

        return holder
    }

    private fun <T, VH : TagViewHolder> getOrCreateMoreViewHolder(adapter: TagViewAdapter<T, VH>): VH {
        // 尝试从缓存或现有的 View 列表中找
        @Suppress("UNCHECKED_CAST")
        var holder = mMoreViewHolder as? VH

        if (holder == null) {
            holder = adapter.createMoreViewHolder(mInflater, this)
            val view = holder.itemView
            // 自动化绑定
            view.setTag(R.id.tag_view_holder, holder)
            view.isGone = true
            addView(view)
            mMoreViewHolder = holder
        }
        return holder
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 1. 优先从 MeasureSpec 中获取真实宽度，而不是依赖 measuredWidth
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        // 如果父布局还没准备好宽度（通常在初次测量时），给一个暂时的宽
        val availableWidth = (widthSize - paddingLeft - paddingRight).coerceAtLeast(0)

        // 2. 只有在宽度模式确定或宽度大于 0 时才运行闭包
        val contentHeight = if (availableWidth > 0 || widthMode == MeasureSpec.UNSPECIFIED) {
            mMeasureLogic?.invoke(availableWidth) ?: 0
        } else 0

        val finalHeight = if (contentHeight > 0) {
            contentHeight + paddingTop + paddingBottom
        } else 0

        setMeasuredDimension(widthSize, resolveSize(finalHeight, heightMeasureSpec))
    }

    private fun <T, VH : TagViewHolder> performTypeSafeMeasure(
        availableWidth: Int,
        data: List<T>,
        adapter: TagViewAdapter<T, VH>
    ): Int {
        var usedWidth = 0
        var maxHeight = 0
        var visibleCount = 0

        val moreVH = getOrCreateMoreViewHolder(adapter)
        val moreView = moreVH.itemView
        // 测量开始前，先让 moreView 归位
        moreView.isGone = true

        // 计算前，我们需要确保 setupTags 传进来的 displayData 对应的 View 是 VISIBLE 状态
        // 否则 child.measure 拿到的宽宽永远是 0
        val displayCount = if (data.size > mMaxTagCount) mMaxTagCount else data.size
        for (idx in 0 until displayCount) {
            getChildAt(idx)?.isGone = false
        }

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            // 排除 moreView 和本来就不该显示的 View（超出 data 范围的）
            if (child == moreView || i >= displayCount) {
                child.isGone = true
                continue
            }

            val currentGap = if (visibleCount == 0) 0 else mTagGap
            val childLp = child.layoutParams
            val childHeightSpec = getChildMeasureSpec(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                0, childLp.height
            )

            // 预测量
            child.measure(MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST), childHeightSpec)

            if (usedWidth + currentGap + child.measuredWidth <= availableWidth) {
                usedWidth += (currentGap + child.measuredWidth)
                maxHeight = maxOf(maxHeight, child.measuredHeight)
                visibleCount++
                child.isGone = false // 确定放下
            } else {
                // 空间不足，尝试显示“更多”
                val remaining = data.size - visibleCount
                if (remaining > 0) {
                    // 核心分发逻辑
                    val moreItem = adapter.convertRemainingToItem(remaining, data, visibleCount)
                    if (moreItem != null) {
                        // 自动转换成功，复用 bindViewHolder，position 传 -1 区分
                        adapter.bindViewHolder(moreVH, moreItem, -1)
                    } else {
                        // 转换失败或未实现，走传统的绑定逻辑
                        adapter.bindMoreViewHolder(moreVH, remaining)
                    }

                    moreView.isGone = false
                    moreView.measure(MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST), childHeightSpec)

                    if (usedWidth + currentGap + moreView.measuredWidth > availableWidth && visibleCount > 0) {
                        // 找到最后一个显示的普通 View 并隐藏
                        getVisibleChildBefore(i)?.isGone = true
                    }
                    maxHeight = maxOf(maxHeight, moreView.measuredHeight)
                }
                // 剩下的全部切断
                for (j in i until childCount) {
                    val c = getChildAt(j)
                    if (c != moreView) c.isGone = true
                }
                break
            }
        }
        return maxHeight
    }

    private fun getVisibleChildBefore(currentIndex: Int): View? {
        for (i in currentIndex - 1 downTo 0) {
            val v = getChildAt(i)
            if (v.isVisible && v != mMoreViewHolder?.itemView) return v
        }
        return null
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val contentHeight = b - t - paddingTop - paddingBottom
        var currentLeft = paddingLeft

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.isGone) continue

            val cl = currentLeft
            val ct = paddingTop + (contentHeight - child.measuredHeight) / 2
            val cr = cl + child.measuredWidth
            val cb = ct + child.measuredHeight

            child.layout(cl, ct, cr, cb)
            currentLeft = cr + mTagGap
        }
    }
}