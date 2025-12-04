package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/22
 * desc   : 简单宫格视图 - 支持动态图片数量和智能圆角
 */
class SimpleGridLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : GridLayout(context, attrs) {

    private var currentImages: List<String> = emptyList()
    private var mOnNineGridClickListener: OnNineGridClickListener? = null

    // 圆角半径和图片间距，可根据需求调整
    private val cornerRadius = 8.dp.toFloat()
    private val imageSpace = 4.dp

    // 缓存计算的图片尺寸
    private var cachedImageSize = 0

    init {
        columnCount = 3
    }

    fun setData(images: List<String> = emptyList()) = apply {
        currentImages = images
        isVisible = images.isNotEmpty()

        if (images.isEmpty()) {
            clearAllImages()
            return@apply
        }

        // 计算新的列数
        val newColumnCount = when (images.size) {
            1 -> 1
            2, 4 -> 2
            else -> 3
        }

        // ⚠️ 关键修复：必须先清空所有子视图，再改变列数，避免 columnCount 异常
        removeAllViews()
        columnCount = newColumnCount

        // 重置缓存，让 onMeasure 重新计算
        cachedImageSize = 0

        // 重新添加所有 ImageView
        images.forEachIndexed { index, url ->
            val imageView = createImageView(index, newColumnCount)
            addView(imageView)
            bindImage(imageView, url, index, images.size)
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val width = MeasureSpec.getSize(widthSpec)

        if (width > 0 && isNotEmpty() && cachedImageSize == 0) {
            // 计算每个图片的尺寸：(总宽度 - 间距) / 列数
            val totalSpacing = imageSpace * (columnCount + 1)
            cachedImageSize = (width - totalSpacing) / columnCount

            // 更新所有子视图的尺寸
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val lp = child.layoutParams as LayoutParams
                lp.width = cachedImageSize
                lp.height = cachedImageSize
                child.layoutParams = lp
            }
        }

        super.onMeasure(widthSpec, heightSpec)
    }

    private fun clearAllImages() {
        for (i in 0 until childCount) {
            Glide.with(this).clear(getChildAt(i))
        }
        removeAllViews()
    }

    private fun createImageView(index: Int, cols: Int): ImageView {
        val row = index / cols
        val col = index % cols

        return ImageView(context).apply {
            layoutParams = LayoutParams(
                spec(row),
                spec(col)
            ).apply {
                // 初始尺寸，会在 onMeasure 中更新
                width = 0
                height = 0
                setMargins(imageSpace / 2, imageSpace / 2, imageSpace / 2, imageSpace / 2)
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun bindImage(imageView: ImageView, url: String, index: Int, total: Int) {
        // 计算当前位置应该有的圆角
        val corners = calculateCorners(index, total)

        Glide.with(imageView)
            .load(url)
            .transform(
                MultiTransformation(
                    CenterCrop(),
                    GranularRoundedCorners(
                        corners.topLeft, corners.topRight,
                        corners.bottomRight, corners.bottomLeft
                    )
                )
            )
            .into(imageView)

        // 设置点击监听器
        val clickIndex = index
        imageView.setFixOnClickListener {
            mOnNineGridClickListener?.onNineGridItemClick(currentImages, clickIndex)
        }
    }

    /**
     * 根据图片在网格中的位置计算应该有的圆角。
     * 只有位于整体边缘的图片的外角才有圆角。
     */
    private fun calculateCorners(index: Int, total: Int): CornerRadii {
        // 根据总数确定列数
        val cols = when (total) {
            1 -> 1
            2, 4 -> 2
            else -> 3
        }

        // 计算行数
        val rows = (total + cols - 1) / cols

        // 计算当前图片所在的行列
        val row = index / cols
        val col = index % cols

        // 判断是否是第一行/第一列
        val isFirstRow = row == 0
        val isFirstCol = col == 0

        // 判断是否是最后一行
        val isLastRow = row == rows - 1

        // 判断是否是最后一列（考虑最后一行可能不满的情况）
        val isLastCol = if (isLastRow) {
            // 最后一行：检查是否是该行的最后一个
            val itemsInLastRow = total - (rows - 1) * cols
            col == itemsInLastRow - 1
        } else {
            col == cols - 1
        }

        return CornerRadii(
            topLeft = if (isFirstRow && isFirstCol) cornerRadius else 0f,
            topRight = if (isFirstRow && isLastCol) cornerRadius else 0f,
            bottomLeft = if (isLastRow && isFirstCol) cornerRadius else 0f,
            bottomRight = if (isLastRow && isLastCol) cornerRadius else 0f
        )
    }

    fun setOnNineGridClickListener(listener: OnNineGridClickListener?) = apply {
        mOnNineGridClickListener = listener
    }

    fun interface OnNineGridClickListener {
        fun onNineGridItemClick(sources: List<String>, index: Int)
    }

    /**
     * 圆角数据类，存储四个角的圆角半径
     */
    private data class CornerRadii(
        val topLeft: Float,
        val topRight: Float,
        val bottomRight: Float,
        val bottomLeft: Float
    )
}