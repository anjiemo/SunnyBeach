package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import cn.cqautotest.sunnybeach.R

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/7/4
 *    desc   : 默认按照九宫格摆放的自定义控件（可以自行设置相关参数）
 */
class GridLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    // 默认的最大显示数目
    var maxItemCount = DEFAULT_MAX_ITEM_COUNT
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("Maximum display number cannot be less than 0!")
            }
            field = value
        }

    // 默认的最大行数
    var maxItemLines = DEFAULT_MAX_ITEM_LINES

    // 一行摆放的个数，默认为 3
    var spanCount = DEFAULT_SPAN_COUNT

    // 横向的间距
    var horizontalSpace = DEFAULT_HORIZONTAL_SPACE

    // 纵向的间距
    var verticalSpace = DEFAULT_VERTICAL_SPACE

    // 最后一个 childView
    private lateinit var _lastChildView: View
    val lastChildView: View get() = _lastChildView

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GridLayout)
        maxItemCount =
            ta.getInteger(R.styleable.GridLayout_maxItemCount, DEFAULT_MAX_ITEM_COUNT)
        maxItemLines =
            ta.getInteger(R.styleable.GridLayout_maxItemLines, DEFAULT_MAX_ITEM_LINES)
        spanCount = ta.getInteger(R.styleable.GridLayout_spanCount, DEFAULT_SPAN_COUNT)
        verticalSpace =
            ta.getDimensionPixelSize(R.styleable.GridLayout_verticalSpace, DEFAULT_VERTICAL_SPACE)
        horizontalSpace =
            ta.getDimensionPixelSize(R.styleable.GridLayout_horizontalSpace, DEFAULT_HORIZONTAL_SPACE)
        ta.recycle()
    }

    /**
     * 设置网格视图的 item 列表
     */
    fun setGridViews(vararg views: View) {
        setGridViews(views.toList())
    }

    /**
     * 设置网格视图的 item 列表
     */
    fun setGridViews(views: List<View>) {
        removeAllViews()
        for (view in views) {
            addView(view)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 获取自己的宽度
        val width = MeasureSpec.getSize(widthMeasureSpec)
        // 计算 childView 的宽度
        val itemWidth: Int =
            (width - paddingLeft - paddingRight - horizontalSpace * (spanCount - 1)) / spanCount
        var childCount = childCount
        // 计算最大显示的item个数
        childCount = childCount.coerceAtMost(maxItemCount)
        if (childCount <= 0) {
            setMeasuredDimension(0, 0)
            return
        }
        for (i in 0 until childCount) {
            val childView: View = getChildAt(i)
            // 测量 childView
            val itemSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY)
            // 摆放 childView
            measureChild(childView, itemSpec, itemSpec)
        }
        val height: Int =
            (itemWidth * (if (childCount % spanCount == 0) childCount / spanCount else childCount / spanCount + 1) + verticalSpace * ((childCount - 1) / spanCount))
        // 指定自己的宽高
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var count = childCount
        // 计算需要摆放的个数，个数最小原则
        count = count.coerceAtMost(maxItemCount)
        // 如果没有 childView ，则无需摆放
        if (count < 1) return
        // 设置起始位置
        var cl = paddingLeft
        var ct = paddingTop
        // 起始行数
        var lines = 1
        for (index in 0 until count) {
            if (lines == NOT_LIMITED_LINES || lines > maxItemLines) continue
            val targetView = getChildAt(index)
            // 判断是否需要跳过该 View
            if (needSkipView(targetView)) continue
            // 测量 childView
            val width = targetView.measuredWidth
            val height = targetView.measuredHeight
            // 摆放 childView
            targetView.layout(cl, ct, cl + width, ct + height)
            // 累加 childView 的宽度
            cl += width + horizontalSpace
            // 判断是否需要换行（是否已经到了最右边）
            if ((index + 1) % spanCount == 0) {
                // 重置 childView 左边的起始位置
                cl = paddingLeft
                // 叠加 childView 的高度
                ct += height + verticalSpace
                lines++
            }
        }
        // 获取最后一个 childView
        _lastChildView = getChildAt(count - 1)
    }

    /**
     * 判断是否需要跳过该 View
     */
    private fun needSkipView(targetView: View) = targetView.visibility == View.GONE

    companion object {

        // 默认的最大显示的 item 个数
        const val DEFAULT_MAX_ITEM_COUNT = 9

        // 默认一行显示的 item 个数
        const val DEFAULT_SPAN_COUNT = 3

        // 默认显示的最大 item 行数
        const val DEFAULT_MAX_ITEM_LINES = 3

        // 不限制行数
        const val NOT_LIMITED_LINES = -1

        // item 之间的横向间距
        const val DEFAULT_HORIZONTAL_SPACE = 0

        // item 之间的纵向间距
        const val DEFAULT_VERTICAL_SPACE = 0
    }
}