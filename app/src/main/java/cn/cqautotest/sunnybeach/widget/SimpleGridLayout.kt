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
 * desc   : 宫格视图组件 - 支持动态图片数量及智能圆角剪裁
 */
class SimpleGridLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : GridLayout(context, attrs) {

    private var currentImages: List<String> = emptyList()
    private var mOnNineGridClickListener: OnNineGridClickListener? = null

    private val cornerRadius = 8.dp.toFloat()
    private val imageSpace = 4.dp

    // 缓存计算出的单项尺寸，由 onMeasure 确定
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

        // 根据图片数量动态调整网格列数
        val newColumnCount = when (images.size) {
            1 -> 1
            2, 4 -> 2
            else -> 3
        }

        // 切换布局前必须先清空视图，避免 columnCount 与子视图分配冲突
        removeAllViews()
        columnCount = newColumnCount
        cachedImageSize = 0

        images.forEachIndexed { index, url ->
            val imageView = createImageView(index, newColumnCount)
            addView(imageView)
            bindImage(imageView, url, index, images.size)
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val width = MeasureSpec.getSize(widthSpec)

        if (width > 0 && isNotEmpty() && cachedImageSize == 0) {
            if (columnCount == 1) {
                // 单张图限制最大宽度为容器的 60%
                cachedImageSize = (width * 0.6f).toInt()
            } else {
                val totalSpacing = imageSpace * (columnCount + 1)
                cachedImageSize = (width - totalSpacing) / columnCount
            }

            // 同步子视图尺寸
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
            layoutParams = LayoutParams(spec(row), spec(col)).apply {
                width = 0
                height = 0
                setMargins(imageSpace / 2, imageSpace / 2, imageSpace / 2, imageSpace / 2)
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun bindImage(imageView: ImageView, url: String, index: Int, total: Int) {
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

        imageView.setFixOnClickListener {
            mOnNineGridClickListener?.onNineGridItemClick(currentImages, index)
        }
    }

    /**
     * 计算特定位置图片的圆角。仅在网格整体矩形的顶点处应用圆角。
     */
    private fun calculateCorners(index: Int, total: Int): CornerRadii {
        val cols = when (total) {
            1 -> 1
            2, 4 -> 2
            else -> 3
        }
        val rows = (total + cols - 1) / cols

        val row = index / cols
        val col = index % cols

        val isFirstRow = row == 0
        val isFirstCol = col == 0
        val isLastRow = row == rows - 1
        val isLastColFixed = col == cols - 1

        return CornerRadii(
            topLeft = if (isFirstRow && isFirstCol) cornerRadius else 0f,
            topRight = if (isFirstRow && isLastColFixed) cornerRadius else 0f,
            bottomLeft = if (isLastRow && isFirstCol) cornerRadius else 0f,
            bottomRight = if (isLastRow && isLastColFixed) cornerRadius else 0f
        )
    }

    fun setOnNineGridClickListener(listener: OnNineGridClickListener?) = apply {
        mOnNineGridClickListener = listener
    }

    fun interface OnNineGridClickListener {

        /**
         * 宫格项点击回调
         */
        fun onNineGridItemClick(sources: List<String>, index: Int)
    }

    private data class CornerRadii(
        val topLeft: Float,
        val topRight: Float,
        val bottomRight: Float,
        val bottomLeft: Float
    )
}